package in.nic.ashwini.eForms.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.DnsBase;
import in.nic.ashwini.eForms.entities.DnsBulkCampaigns;
import in.nic.ashwini.eForms.entities.DnsBulkCname;
import in.nic.ashwini.eForms.entities.DnsBulkDmarc;
import in.nic.ashwini.eForms.entities.DnsBulkMx;
import in.nic.ashwini.eForms.entities.DnsBulkPtr;
import in.nic.ashwini.eForms.entities.DnsBulkSpf;
import in.nic.ashwini.eForms.entities.DnsBulkSrv;
import in.nic.ashwini.eForms.entities.DnsBulkTxt;
import in.nic.ashwini.eForms.entities.DnsBulkUpload;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.NextHopBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.models.ValidatedDnsBean;
import in.nic.ashwini.eForms.services.DnsService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/dns/user")
@RestController
public class UserController {
	private final DnsService dnsService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	private NextHopBean nextHopBean;

	@Autowired
	public UserController(DnsService dnsService, ResponseBean responseBean, Util utilityService) {
		super();
		this.dnsService = dnsService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType,
			@RequestParam("requestType") @NotEmpty String requestType,
			@RequestParam("recordType") @NotEmpty String recordType,
			@RequestParam("campaignId") Long campaignId) {
		log.info("Submiting {} Request by User {}.", submissionType,email);
		String formType = "dns";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");
		Map<String, Object> map = validateRequest(previewFormBean);
		
		if (map.isEmpty()) {
			responseBean.setErrors(null);
			if (profile != null) {
				status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(), profile.getMobile(),
						profile.getName(),"user");
				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, previewFormBean.getRemarks(),
						profile.getMobile(), profile.getName(), "user","");

				ModelMapper modelMapper = new ModelMapper();

				DnsBase dnsBase = modelMapper.map(profile, DnsBase.class);
				dnsBase.setPdfPath(submissionType);
				BeanUtils.copyProperties(previewFormBean, dnsBase);
				LocalDateTime currentTime = LocalDateTime.now();
				dnsBase.setDatetime(currentTime);
				dnsBase.setLastUpdationDateTime(currentTime);
				dnsBase.setUserIp(ip);
				dnsBase.setReqFor(requestType);
				dnsBase.setReqOtherRecord(recordType);
				dnsBase.setFormType("dns_bulk");
				dnsBase.setReqId("NA");

				for (int i = 0; i < 4; i++) {
					dnsBase = dnsService.insert(dnsBase);
					log.info("{} request submitted in database with ID {} in attempt {}",dnsBase.getId(),(i+1));
					if (dnsBase.getId() > 0) {
						break;
					}
				}

				if (dnsBase.getId() > 0) {
					if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
//						if (utilityService.isNicEmployee(email)) {
//							status.setRegistrationNo(dnsBase.getRegistrationNo());
//							status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
//							status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
//							status.setRecipient(Constants.MAILADMIN_EMAIL);
//
//							finalAuditTrack.setRegistrationNo(dnsBase.getRegistrationNo());
//							finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
//							finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);
//
//							if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
//								log.info("{} submitted successfully.", dnsBase.getRegistrationNo());
//								responseBean.setStatus("Request submitted successfully and forwarded to Admin ("
//										+ Constants.MAILADMIN_EMAIL + ")");
//								responseBean.setRegNumber(dnsBase.getRegistrationNo());
//							} else {
//								log.debug("Something went wrong. Please try again after sometime.");
//								responseBean.setStatus("Something went wrong. Please try again after sometime.");
//								responseBean.setRegNumber("");
//							}
//						} else {
						status.setRegistrationNo(dnsBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_CA_TYPE);
						status.setStatus(Constants.STATUS_CA_PENDING);
						status.setRecipient(profile.getHodEmail());

						finalAuditTrack.setRegistrationNo(dnsBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
						finalAuditTrack.setToEmail(profile.getHodEmail());

						if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							int i = dnsService.updateRegNumberInCampaignTable(campaignId, dnsBase.getRegistrationNo());
							int j = dnsService.updateRegNumberInBulkTable(campaignId, recordType,
									dnsBase.getRegistrationNo());
							if (i > -1 && j > -1) {
								log.info("{} submitted successfully.", dnsBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(dnsBase.getRegistrationNo());
							} else {
								log.debug("Reg number could not be updated in campaign and bulk tables.");
								responseBean.setStatus(
										"Something went wrong. Details could not be updated. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
						// }
					} else {
						status.setRegistrationNo(dnsBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_USER_TYPE);
						status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						status.setRecipient(email);

						finalAuditTrack.setRegistrationNo(dnsBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						finalAuditTrack.setToEmail(email);

						if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							int i = dnsService.updateRegNumberInCampaignTable(campaignId, dnsBase.getRegistrationNo());
							int j = dnsService.updateRegNumberInBulkTable(campaignId, recordType,
									dnsBase.getRegistrationNo());
							if (i > -1 && j > -1) {
								log.info("{} submitted successfully.", dnsBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
								responseBean.setRegNumber(dnsBase.getRegistrationNo());
							} else {
								log.debug("Reg number could not be updated in campaign and bulk tables.");
								responseBean.setStatus(
										"Something went wrong. Details could not be updated. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					}
				} else {
					log.debug("Something went wrong. Please try again after sometime.");
					responseBean.setStatus("Something went wrong. Please try again after sometime.");
					responseBean.setRegNumber("");
				}
			} else {
				log.warn(
						"Hey, {},  We do not have your profile in eForms. Please go to profile section and make your profile first");
				responseBean.setStatus(
						"We do not have your profile in eForms. Please go to profile section and make your profile first");
				responseBean.setRegNumber("");
			}
		} else {
			responseBean.setErrors(map);
			responseBean.setRegNumber("");
			responseBean.setStatus("Application could not be submitted.");
		}
		return responseBean;
	}

	@RequestMapping(value = "/updateRequest")
	public ResponseBean updateRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType,
			@RequestParam("requestType") @NotEmpty String requestType,
			@RequestParam("recordType") @NotEmpty String recordType,
			@RequestParam("campaignId") @NotEmpty Long campaignId,
			@RequestParam("regNo") @NotEmpty String registrationNumber) {
		log.info("Updating registration no {} by user {}.",registrationNumber,email);
		if (registrationNumber.isEmpty()) {
			responseBean.setRequestType("Updation and Submission of request");
			return submitRequest(previewFormBean, ip, email, submissionType,requestType,recordType, campaignId);
		}

		responseBean.setRequestType("Updation of request");
		Map<String, Object> map = validateRequest(previewFormBean);

		if (map.size() == 0) {
			responseBean.setErrors(null);
			DnsBase dnsBase = dnsService.findByRegistrationNo(registrationNumber);

			LocalDateTime currentTime = LocalDateTime.now();
			dnsBase.setLastUpdationDateTime(currentTime);
			dnsBase = dnsService.update(dnsBase);
			log.info("Update of registration no {} by user {} in database table ID {}.",registrationNumber,email,dnsBase.getId());

			if (dnsBase.getId() > 0) {
				log.info("{} updated successfully.", dnsBase.getRegistrationNo());
				responseBean.setStatus("Request (" + dnsBase.getRegistrationNo() + ") updated successfully");
				responseBean.setRegNumber(dnsBase.getRegistrationNo());
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}
		} else {
			responseBean.setErrors(map);
			responseBean.setRegNumber("");
			responseBean.setStatus("Application could not be submitted.");
		}
		return responseBean;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		log.info("Approving registration no {} by {}.",regNumber,email);
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = "dns";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		DnsBase dnsBase = dnsService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, dnsBase.getMobile(), dnsBase.getName(),"user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

//		if (utilityService.isNicEmployee(email)) {
//			status.setRegistrationNo(regNumber);
//			status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
//			status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
//			status.setRecipient(Constants.MAILADMIN_EMAIL);
//
//			finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
//			finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);
//
//			if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
//				log.info("{} submitted successfully.", regNumber);
//				responseBean.setStatus(
//						"Request submitted successfully and forwarded to Admin (" + Constants.MAILADMIN_EMAIL + ")");
//				responseBean.setRegNumber(regNumber);
//			} else {
//				log.debug("Something went wrong. Please try again after sometime.");
//				responseBean.setStatus("Something went wrong. Please try again after sometime.");
//				responseBean.setRegNumber("");
//			}
//		} else {
		status.setRegistrationNo(regNumber);
		status.setRecipientType(Constants.STATUS_CA_TYPE);
		status.setStatus(Constants.STATUS_CA_PENDING);
		status.setRecipient(dnsBase.getHodEmail());

		finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
		finalAuditTrack.setToEmail(dnsBase.getHodEmail());

		if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} submitted successfully.", regNumber);
			responseBean
					.setStatus("Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
							+ dnsBase.getHodEmail() + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber("");
		}
		// }
		return responseBean;
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		log.info("Rejecting registration no {} by user {}.",regNumber,email);
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = "dns";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		DnsBase dnsBase = dnsService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, dnsBase.getMobile(), dnsBase.getName(),"user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} cancelled successfully.", regNumber);
			responseBean.setStatus(regNumber + " cancelled successfully");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber("");
		}

		return responseBean;
	}

	@GetMapping("/fetchNextHop")
	public NextHopBean findNextHop(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {
		
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		if (profile != null) {
			if (previewFormBean.getEmployment().equalsIgnoreCase("state")
					&& previewFormBean.getState().equalsIgnoreCase("Himachal Pradesh")) {
				// List<String> himachalCoords =
				// utilityService.fetchHimachalCoords(previewFormBean.getDepartment());
				// if (himachalCoords != null && himachalCoords.size() > 0) {
				String coordEmail = "kaushal.shailender@nic.in";
				// if (utilityService.isGovEmployee(coordEmail)) {
				HodDetailsDto roDetails = utilityService.getHodValues(coordEmail);
				profile.setHodEmail(coordEmail);
				profile.setHodMobile(roDetails.getMobile());
				profile.setHodName(roDetails.getFirstName());
				profile.setHodDesignation(roDetails.getDesignation());
				profile.setHodTelephone(roDetails.getTelephoneNumber());
				// }
				// }
			}

			if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
//				if (utilityService.isNicEmployee(email)) {
//					nextHopBean.setEmail(Constants.MAILADMIN_EMAIL);
//					nextHopBean.setName("iNOC Support");
//					nextHopBean.setDesignation("support");
//					nextHopBean.setMobile("");
//					nextHopBean.setRole("Admin");
//					nextHopBean.setStatus("Request is getting forwarded to admin.");
//				} else {
				nextHopBean.setEmail(profile.getHodEmail());
				nextHopBean.setName(profile.getHodName());
				nextHopBean.setDesignation(profile.getHodDesignation());
				nextHopBean.setMobile(profile.getHodMobile());
				nextHopBean.setRole("Reporting/Forwarding/Nodal Officer");
				nextHopBean.setStatus("Request is getting forwarded to your reporting/forwarding/nodal officer.");
				// }
			} else {
				nextHopBean.setEmail(email);
				nextHopBean.setName(profile.getName());
				nextHopBean.setDesignation(profile.getDesignation());
				nextHopBean.setMobile(profile.getMobile());
				nextHopBean.setRole("self");
				nextHopBean.setStatus("Request is pending with you only as you opted for manual submission.");
			}

		} else {
			log.warn(
					"Hey, {},  We do not have your profile in eForms. Please go to profile section and make your profile first",email);
			nextHopBean.setStatus(
					"We do not have your profile in eForms. Please go to profile section and make your profile first");
		}
		return nextHopBean;
	}

	@RequestMapping(value = "/firstLevelSubmission")
	public Map<Object, Object> firstLevelSubmission(@Valid @RequestBody List<ValidatedDnsBean> dnsData,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("name") @NotEmpty String name,
			@RequestParam("requestType") @NotBlank(message = "requestType must be either req_new, req_modify or req_delete") String requestType,
			@RequestParam("recordType") @NotBlank(message = "recordType can not be empty. It must be either aaaa, mx, srv, spf, txt, dmarc or ptr") String recordType,
			@RequestParam(value = "renamedFilePath", required = false) String renamedFilePath,
			@RequestParam(value = "uploadedFile", required = false) String uploadedFile) {
		Map<Object, Object> ExcelValidate = new HashMap<>();
		log.debug("firstLevelSubmission API started");
		if (dnsData.size() <= 0) {
			ExcelValidate.put("errorMessage", "Please enter domain or Upload valid file. You can not keep it empty!!!");
		} else {
			boolean flag = true;

			DnsBulkCampaigns campaignBean = dnsService.addDnsCampaign(name, email, dnsData, requestType, recordType,
					renamedFilePath, uploadedFile);
			long campaignId = campaignBean.getId();
			if (campaignId != -1) {

				String errorInString = "";
				List<String> alreadyAllowed = null;
				List<String> alreadyAappliedForThisDomain = null;
				List<String> alreadyAappliedForThisIp = null;
				List<ValidatedDnsBean> successBean = new ArrayList<>();
				List<ValidatedDnsBean> errorBean = new ArrayList<>();
				Long id = -1l;
				ExcelValidate.put("campaign", campaignBean);

				switch (requestType) {
				case "req_new":
					switch (recordType) {
					case "aaaa":
						alreadyAappliedForThisDomain = new ArrayList<>();
						alreadyAappliedForThisIp = new ArrayList<>();
						alreadyAllowed = new ArrayList<>();

						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (dnsBean.getNewIp().isEmpty()) {
								flag = false;
								errorInString += "Please enter new IP." + " | ";
							}

							if (dnsBean.getDomain().startsWith("www.")) {
								String returnString = dnsService.checkDomain(dnsBean.getDomain());
								if (!returnString.isEmpty()) {
									if (dnsService.doesIPbelongToNIC(returnString) || dnsService.doesIPbelongToNIC(dnsBean.getNewIp())) {
										if (dnsService.isValidIp(returnString)) {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") already exists with CNAME (" + returnString
													+ "). Please use modify option." + " | ";
											flag = false;
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") already exists with ip (" + returnString + ")" + " | ";
											flag = false;
										}
									}
								}
							}

							String domainIP = dnsBean.getDomain() + ":" + dnsBean.getNewIp();

							if (flag) {
								String originalDomain = "";
								String domain = dnsBean.getDomain();
								if (alreadyAllowed.contains(domainIP)) {
									flag = false;
									errorInString += "Duplicate record, Kindly remove this." + " | ";
								} else {
									if (dnsService.isThirdLevelDomain(dnsBean.getDomain())) {
										if (alreadyAappliedForThisDomain.contains(dnsBean.getDomain())) {
											errorInString += "You have already applied for this domain ("
													+ dnsBean.getDomain() + ")" + " | ";
											flag = false;
										} else {
											if (dnsBean.getDomain().startsWith("www.")) {
												originalDomain = dnsBean.getDomain();
												dnsBean.setDomain(dnsBean.getDomain().substring(4));
											}
											String returnString = dnsService.checkDomain(dnsBean.getDomain());
											if (!returnString.isEmpty()) {
												if (dnsService.doesIPbelongToNIC(returnString)
														|| dnsService.doesIPbelongToNIC(dnsBean.getNewIp())) {
													if (originalDomain.startsWith("www.")) {
														if (dnsService.isValidIp(returnString)) {
															errorInString += "Domain (" + dnsBean.getDomain()
																	+ ") already exists with CNAME (" + returnString
																	+ ")." + " Please add (" + originalDomain
																	+ ") as CNAME. | ";
															flag = false;
														} else {
															errorInString += "Domain (" + dnsBean.getDomain()
																	+ ") already exists with ip (" + returnString + ")."
																	+ " Please add (" + originalDomain
																	+ ") as CNAME. | ";
															flag = false;
														}
													} else {
														if (dnsService.isValidIp(returnString)) {
															errorInString += "Domain (" + domain
																	+ ") already exists with CNAME (" + returnString
																	+ "). Please use modify option." + " | ";
															flag = false;
														} else {
															errorInString += "Domain (" + domain
																	+ ") already exists with ip (" + returnString + ")"
																	+ " | ";
															flag = false;
														}
													}
												}
											} else {
												if (dnsService.doesIPbelongToNIC(dnsBean.getNewIp())) {
													if (alreadyAappliedForThisIp.contains(dnsBean.getNewIp())) {
														errorInString += "You have already applied for this IP ("
																+ dnsBean.getNewIp() + ")" + " | ";
														flag = false;
													} else {
														returnString = dnsService.checkIP(dnsBean.getNewIp());
														if (!returnString.isEmpty()) {
															errorInString += "IP (" + dnsBean.getNewIp()
																	+ ") already exists with domain (" + returnString
																	+ ")" + " | ";
															flag = false;
														} else {
															alreadyAappliedForThisDomain.add(dnsBean.getDomain());
															alreadyAappliedForThisIp.add(dnsBean.getNewIp());
															alreadyAllowed.add(domainIP);
														}
													}
												}
											}
										}
									} else {
										if (dnsService.isGovDomain(dnsBean.getDomain())) {
											errorInString += "Please go to https://registry.gov.in/domain_process.php and coordinate with sunita.singh@nic.in"
													+ " | ";
											flag = false;
										} else {
											if (dnsService.doesIPbelongToNIC(dnsBean.getNewIp())) {
												String returnString = dnsService.checkIP(dnsBean.getNewIp());
												if (!returnString.isEmpty()) {
													if (dnsService.isThirdLevelDomain(returnString)) {
														errorInString += "IP (" + dnsBean.getNewIp()
																+ ") already exists with domain (" + returnString + ")"
																+ " | ";
														flag = false;
													} else {
														alreadyAllowed.add(domainIP);
													}
												} else {
													alreadyAllowed.add(domainIP);
												}
											}
										}
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "cname":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getCnameTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getCnameTxt().isEmpty()) {
									errorInString += "Enter the CNAME [e.g.: demo.nic.in or demo.gov.in]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getCnameTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "mx":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getMxTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getMxTxt().isEmpty()) {
									errorInString += "Enter MX value, [Alphanumeric,dot(.),comma(,),hyphen(-),underscore(_),slash(/) and whitespaces] allowed"
											+ " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getMxTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "spf":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getSpfTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getSpfTxt().isEmpty()) {
									errorInString += "Enter valid SPF value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getSpfTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "srv":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getSrvTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {

								if (dnsBean.getSrvTxt().isEmpty()) {
									errorInString += "Enter valid SRV value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getSrvTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "txt":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getTxtTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {

								if (dnsBean.getTxtTxt().isEmpty()) {
									errorInString += "Enter valid TXT value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getTxtTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "ptr":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getPtrTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {

								if (dnsBean.getPtrTxt().isEmpty()) {
									errorInString += "Enter the IPV4/IPV6 Address" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {

										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getPtrTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "dmarc":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getDmarcTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {

								if (dnsBean.getDmarcTxt().isEmpty()) {
									errorInString += "Enter valid DMARC value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {

										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getDmarcTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					default:
						break;
					}
					break;
				case "req_delete":
					switch (recordType) {
					case "aaaa":
						alreadyAappliedForThisDomain = new ArrayList<>();
						alreadyAappliedForThisIp = new ArrayList<>();
						alreadyAllowed = new ArrayList<>();

						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (!dnsBean.getNewIp().isEmpty() && !dnsService.isValidIp(dnsBean.getNewIp())) {
								flag = false;
								errorInString += "Please enter valid new IP." + " | ";
							}

							String domainIP = dnsBean.getDomain() + ":" + dnsBean.getNewIp();

							if (flag) {
								if (alreadyAllowed.contains(domainIP)) {
									flag = false;
									errorInString += "Duplicate record, Kindly remove this." + " | ";
								} else {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (!dnsBean.getNewIp().isEmpty()) {
											if (dnsService.isThirdLevelDomain(dnsBean.getDomain())) {
												String returnString = dnsService.checkDomain(dnsBean.getDomain());
												if (returnString.isEmpty()) {
													errorInString += "This domain (" + dnsBean.getDomain()
															+ ") is not available. Enter correct domain." + " | ";
													flag = false;
												} else if (!returnString.equalsIgnoreCase(dnsBean.getNewIp())) {
													errorInString += "Please enter the correct Old IP mapped with domain ("
															+ dnsBean.getDomain() + ")" + " | ";
													flag = false;
												} else {
													alreadyAllowed.add(domainIP);
												}
											} else {
												if (dnsService.isGovDomain(dnsBean.getDomain())) {
													errorInString += "Please go to https://registry.gov.in/domain_process.php and coordinate with sunita.singh@nic.in"
															+ " | ";
													flag = false;
												} else {
													String returnString = dnsService.checkDomain(dnsBean.getDomain());
													if (returnString.isEmpty()) {
														errorInString += "This domain (" + dnsBean.getDomain()
																+ ") is not available. Enter correct domain." + " | ";
														flag = false;
													} else if (!returnString.equalsIgnoreCase(dnsBean.getNewIp())) {
														errorInString += "Please enter the correct Old IP mapped with domain ("
																+ dnsBean.getDomain() + ")" + " | ";
														flag = false;
													} else {
														alreadyAllowed.add(domainIP);
													}
												}
											}
										} else {
											if (dnsService.isThirdLevelDomain(dnsBean.getDomain())) {
												String returnString = dnsService.checkDomain(dnsBean.getDomain());
												if (returnString.isEmpty()) {
													errorInString += "This domain (" + dnsBean.getDomain()
															+ ") is not available. Enter correct domain." + " | ";
													flag = false;
												} else {
													alreadyAllowed.add(domainIP);
												}
											} else {
												if (dnsService.isGovDomain(dnsBean.getDomain())) {
													errorInString += "Please go to https://registry.gov.in/domain_process.php and coordinate with sunita.singh@nic.in"
															+ " | ";
													flag = false;
												} else {
													String returnString = dnsService.checkDomain(dnsBean.getDomain());
													if (returnString.isEmpty()) {
														errorInString += "This domain (" + dnsBean.getDomain()
																+ ") is not available. Enter correct domain." + " | ";
														flag = false;
													} else {
														alreadyAllowed.add(domainIP);
													}
												}
											}
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "cname":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getCnameTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getCnameTxt().isEmpty()) {
									errorInString += "Enter the CNAME [e.g.: demo.nic.in or demo.gov.in]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getCnameTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "mx":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getMxTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getMxTxt().isEmpty()) {
									errorInString += "Enter MX value, [Alphanumeric,dot(.),comma(,),hyphen(-),underscore(_),slash(/) and whitespaces] allowed"
											+ " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getMxTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "spf":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getSpfTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getSpfTxt().isEmpty()) {
									errorInString += "Enter valid SPF value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getSpfTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "srv":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getSrvTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {

								if (dnsBean.getSrvTxt().isEmpty()) {
									errorInString += "Enter valid SRV value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getSrvTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "txt":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getTxtTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {

								if (dnsBean.getTxtTxt().isEmpty()) {
									errorInString += "Enter valid TXT value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getTxtTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "ptr":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getPtrTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {

								if (dnsBean.getPtrTxt().isEmpty()) {
									errorInString += "Enter the IPV4/IPV6 Address" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {

										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getPtrTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "dmarc":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getDmarcTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {

								if (dnsBean.getDmarcTxt().isEmpty()) {
									errorInString += "Enter valid DMARC value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {

										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getDmarcTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					default:
						break;
					}
					break;
				case "req_modify":
					switch (recordType) {
					case "aaaa":
						alreadyAappliedForThisDomain = new ArrayList<>();
						alreadyAappliedForThisIp = new ArrayList<>();
						alreadyAllowed = new ArrayList<>();

						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (dnsBean.getNewIp().isEmpty()) {
								flag = false;
								errorInString += "Please enter new IP." + " | ";
							}

							String domainIP = dnsBean.getDomain() + ":" + dnsBean.getNewIp();

							if (flag) {
								if (alreadyAllowed.contains(domainIP)) {
									flag = false;
									errorInString += "Duplicate record, Kindly remove this." + " | ";
								} else {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.isThirdLevelDomain(dnsBean.getDomain())) {
											if (alreadyAappliedForThisDomain.contains(dnsBean.getDomain())) {
												errorInString += "You have already applied for this domain ("
														+ dnsBean.getDomain() + ")" + " | ";
												flag = false;
											} else {
												String returnString = dnsService.checkDomain(dnsBean.getDomain());
												if (returnString.isEmpty()) {
													errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
													flag = false;
												} else {
													if (!dnsBean.getOldIp().isEmpty()
															&& !returnString.equalsIgnoreCase(dnsBean.getOldIp())
															&& !dnsService.isValidIp(returnString)) {
														errorInString += "Please enter the correct Old IP mapped with domain ("
																+ dnsBean.getDomain() + ")" + " | ";
														flag = false;
													}

													if (dnsService.doesIPbelongToNIC(dnsBean.getNewIp())) {
														if (alreadyAappliedForThisIp.contains(dnsBean.getNewIp())) {
															errorInString += "You have already applied for this IP ("
																	+ dnsBean.getNewIp() + ")" + " | ";
															flag = false;
														} else {
															returnString = dnsService.checkIP(dnsBean.getNewIp());
															if (!returnString.isEmpty()) {
																errorInString += "IP (" + dnsBean.getNewIp()
																		+ ") already exists with domain ("
																		+ returnString + ")" + " | ";
																flag = false;
															} else {
																alreadyAappliedForThisDomain.add(dnsBean.getDomain());
																alreadyAappliedForThisIp.add(dnsBean.getNewIp());
																alreadyAllowed.add(domainIP);
															}
														}
													}
												}
											}
										} else {
											if (dnsService.isGovDomain(dnsBean.getDomain())) {
												errorInString += "Please go to https://registry.gov.in/domain_process.php and coordinate with sunita.singh@nic.in"
														+ " | ";
												flag = false;
											} else {
												String returnString = dnsService.checkDomain(dnsBean.getDomain());
												if (returnString.isEmpty()) {
													errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
													flag = false;
												} else {
													if (!dnsBean.getOldIp().isEmpty()
															&& !returnString.equalsIgnoreCase(dnsBean.getOldIp())
															&& !dnsService.isValidIp(returnString)) {
														errorInString += "Please enter the correct Old IP mapped with domain ("
																+ dnsBean.getDomain() + ")" + " | ";
														flag = false;
													}

													if (dnsService.doesIPbelongToNIC(dnsBean.getNewIp())) {
														if (alreadyAappliedForThisIp.contains(dnsBean.getNewIp())) {
															errorInString += "You have already applied for this IP ("
																	+ dnsBean.getNewIp() + ")" + " | ";
															flag = false;
														} else {
															returnString = dnsService.checkIP(dnsBean.getNewIp());
															if (!returnString.isEmpty()) {
																if (dnsService.isThirdLevelDomain(returnString)) {
																	errorInString += "IP (" + dnsBean.getNewIp()
																			+ ") already exists with domain ("
																			+ returnString + ")" + " | ";
																	flag = false;
																} else {
																	alreadyAllowed.add(domainIP);
																}
															} else {
																alreadyAllowed.add(domainIP);
															}
														}
													}
												}
											}
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "cname":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getCnameTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getCnameTxt().isEmpty()) {
									errorInString += "Enter valid CNAME [e.g.: demo.nic.in or demo.gov.in]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getCnameTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "mx":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getMxTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getMxTxt().isEmpty()) {
									errorInString += "Enter valid MX value, [Alphanumeric,dot(.),comma(,),hyphen(-),underscore(_),slash(/) and whitespaces] allowed"
											+ " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {

										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getMxTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "spf":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getSpfTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getSpfTxt().isEmpty()) {
									errorInString += "Enter valid SPF value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getSpfTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "srv":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getSrvTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {

								if (dnsBean.getSrvTxt().isEmpty()) {
									errorInString += "Enter valid SRV value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getSrvTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "txt":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getTxtTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getTxtTxt().isEmpty()) {
									errorInString += "Enter valid TXT value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getTxtTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "ptr":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getPtrTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getPtrTxt().isEmpty()) {
									errorInString += "Enter the valid IPV4/IPV6 Address" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getPtrTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					case "dmarc":
						alreadyAllowed = new ArrayList<>();
						for (ValidatedDnsBean dnsBean : dnsData) {
							errorInString = "";
							flag = true;
							id = -1l;
							
							if (alreadyAllowed.contains(dnsBean.getDomain() + ":" + dnsBean.getDmarcTxt())) {
								flag = false;
								errorInString += "Duplicate record, Kindly remove this." + " | ";
							} else {
								if (dnsBean.getDmarcTxt().isEmpty()) {
									errorInString += "Enter valid DMARC value [limit 2-300]" + " | ";
									flag = false;
								}

								if (flag) {
									if (dnsService.isApplicantOwner(dnsBean.getDomain(), email)) {
										if (dnsService.checkDomain(dnsBean.getDomain()).isEmpty()) {
											errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
											flag = false;
										} else {
											alreadyAllowed.add(dnsBean.getDomain() + ":" + dnsBean.getDmarcTxt());
										}
									} else {
										if (dnsService.doNslookup(dnsBean.getDomain())) {
											errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
													+ dnsBean.getDomain() + ")";
										} else {
											errorInString += "Domain (" + dnsBean.getDomain()
													+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
										}
										flag = false;
									}
								}
							}

							if (flag) {
								id = dnsService.addValidRecordsInBulkTable(dnsBean, campaignId, recordType);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								successBean.add(dnsBean);
							} else {
								errorInString = errorInString.replaceAll("\\s*\\|\\s*$", "");
								id = dnsService.addInvalidRecordsInBulkTable(dnsBean, errorInString, campaignId, recordType);
								dnsBean.setError(errorInString);
								dnsBean.setCampaignId(campaignId);
								dnsBean.setId(id);
								errorBean.add(dnsBean);
							}
						}
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
				ExcelValidate.put("success", successBean);
				ExcelValidate.put("error", errorBean);
			} else {
				ExcelValidate.put("errorMessage", "Campaign could not be generated!!!");
			}
		}
		return ExcelValidate;
	}

	@RequestMapping(value = "/validateRequest")
	public Map<String, Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();

		if (previewFormBean.getEmpCode() == null
				|| (previewFormBean.getEmpCode() != null && previewFormBean.getEmpCode().isEmpty())) {
			log.debug("Employee code is either null or empty");
			map.put("empCode", "Employee code is either null or empty.");
		}

		if (!previewFormBean.getTnc()) {
			log.debug("Terms and condition is not selected.");
			map.put("tncError", "Please select Terms and Condition to proceed.");
		}

		BeanUtils.copyProperties(previewFormBean, organizationDetails);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;

		if (organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
			log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse,
						ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (orgError != null)
				map.put("orgError", orgError);
		}
		if (map.size() > 1) {
			finalmap.put("errors", map);
		}
		return map;
	}

	@RequestMapping(value = "/fetchOpenCampaigns")
	public List<DnsBulkCampaigns> fetchOpenCampaigns(@RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email) {
		return dnsService.fetchOpenCampaigns(email);
	}

	@RequestMapping(value = "/fetchSingleRecord")
	public Object fetchSingleRecord(@RequestParam("id") Long id, @RequestParam("recordType") String recordType) {
		return dnsService.fetchSingleRecord(id, recordType);
	}

	@RequestMapping(value = "/updateSingleRecord")
	public Map<String, Object> updateSingleRecord(@RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestBody ValidatedDnsBean dnsBean) {
		Map<String, Object> map = new HashMap<>();
		List<Object> returnedObjects = dnsService.findDuplicateRecords(dnsBean);
		if (!returnedObjects.isEmpty()) {
			Object o = dnsService.fetchSingleRecord(dnsBean.getId(), dnsBean.getReqOtherAdd());
			if (o != null) {
				ValidatedDnsBean validatedDnsBean = updateErrorMessage(dnsBean, o, returnedObjects);
				if (validatedDnsBean.getError().isEmpty()) {
					FinalAuditTrack finalAuditTrackStatus = isRequestCompleted(dnsBean, o);
					if (dnsBean.getError().isEmpty()) {
						if (finalAuditTrackStatus != null) {
							if (!finalAuditTrackStatus.getStatus().contains("complete")) {
								if (dnsBean.getReqOtherAdd().equalsIgnoreCase("aaaa")) {
									dnsService.updateUrlTable(dnsBean);
									dnsService.updateCnameTable(dnsBean);
									dnsService.updateNewIpTable(dnsBean);
									dnsService.updateOldIpTable(dnsBean);
								}
								dnsService.updateValidRecordsInBulkTable(dnsBean);
							}
						} else {
							dnsService.updateValidRecordsInBulkTable(dnsBean);
						}
					}
					map.put("status", "record successfully updated");
					map.put("submitted record", validatedDnsBean);
					map.put("inserted data", dnsService.fetchSuccessfulBulkUploadData(dnsBean.getCampaignId(),
							dnsBean.getReqOtherAdd()));
				} else {
					map.put("status", "record could not be updated");
					map.put("submitted record", validatedDnsBean);
				}
			} else {
				map.put("status", "record does not exist");
				map.put("submitted record", dnsBean);
			}
		} else {
			String errorMessage = validateSingleRecord(dnsBean, email);
			if (errorMessage.isEmpty()) {
				Object o = dnsService.fetchSingleRecord(dnsBean.getId(), dnsBean.getReqOtherAdd());
				if (o != null) {
					FinalAuditTrack finalAuditTrackStatus = isRequestCompleted(dnsBean, o);
					if (dnsBean.getError().isEmpty()) {
						if (finalAuditTrackStatus != null) {
							if (!finalAuditTrackStatus.getStatus().contains("complete")) {
								if (dnsBean.getReqOtherAdd().equalsIgnoreCase("aaaa")) {
									dnsService.updateUrlTable(dnsBean);
									dnsService.updateCnameTable(dnsBean);
									dnsService.updateNewIpTable(dnsBean);
									dnsService.updateOldIpTable(dnsBean);
								}
								dnsService.updateValidRecordsInBulkTable(dnsBean);
							}
						} else {
							dnsService.updateValidRecordsInBulkTable(dnsBean);
						}
						map.put("status", "record successfully updated");
						map.put("submitted record", dnsBean);
						map.put("inserted data", dnsService.fetchSuccessfulBulkUploadData(dnsBean.getCampaignId(),
								dnsBean.getReqOtherAdd()));
					}
				} else {
					map.put("status", "record does not exist");
					map.put("submitted record", dnsBean);
				}
			} else {
				map.put("status", errorMessage);
				map.put("submitted record", dnsBean);
			}
		}
		return map;
	}

	private ValidatedDnsBean updateErrorMessage(ValidatedDnsBean dnsBean, Object o, List<Object> returnedObjects) {
		dnsBean.setError("");
		switch (dnsBean.getReqOtherAdd()) {
		case "cname":
			DnsBulkCname dnsBulkCname = (DnsBulkCname) o;
			if (dnsBulkCname.getDnsError() != null && !dnsBulkCname.getDnsError().isEmpty()) {
				dnsBean.setError("Duplicate record!!! Kindly delete this.");
			} else {
				Object object = returnedObjects.get(0);
				List<DnsBulkCname> dnsBulkCnameList = (List<DnsBulkCname>) object;
				if (dnsBulkCnameList.size() > 1) {
					dnsBean.setError("This record is already there!!!");
				} else if (dnsBulkCnameList.size() == 1) {
					if (dnsBulkCnameList.get(0).getId() != dnsBean.getId()) {
						dnsBean.setError("This record is already there!!!");
					}
				}
			}
			break;
		case "mx":
			DnsBulkMx dnsBulkMx = (DnsBulkMx) o;
			if (dnsBulkMx.getDnsError() != null && !dnsBulkMx.getDnsError().isEmpty()) {
				dnsBean.setError("Duplicate record!!! Kindly delete this.");
			} else {
				Object object = returnedObjects.get(0);
				List<DnsBulkMx> dnsBulkMxList = (List<DnsBulkMx>) object;
				if (dnsBulkMxList.size() > 1) {
					dnsBean.setError("This record is already there!!!");
				} else if (dnsBulkMxList.size() == 1) {
					if (dnsBulkMxList.get(0).getId() != dnsBean.getId()) {
						dnsBean.setError("This record is already there!!!");
					}
				}
			}
			break;
		case "txt":
			DnsBulkTxt dnsBulkTxt = (DnsBulkTxt) o;
			if (dnsBulkTxt.getDnsError() != null && !dnsBulkTxt.getDnsError().isEmpty()) {
				dnsBean.setError("Duplicate record!!! Kindly delete this.");
			} else {
				Object object = returnedObjects.get(0);
				List<DnsBulkTxt> dnsBulkTxtList = (List<DnsBulkTxt>) object;
				if (dnsBulkTxtList.size() > 1) {
					dnsBean.setError("This record is already there!!!");
				} else if (dnsBulkTxtList.size() == 1) {
					if (dnsBulkTxtList.get(0).getId() != dnsBean.getId()) {
						dnsBean.setError("This record is already there!!!");
					}
				}
			}
			break;
		case "ptr":
			DnsBulkPtr dnsBulkPtr = (DnsBulkPtr) o;
			if (dnsBulkPtr.getDnsError() != null && !dnsBulkPtr.getDnsError().isEmpty()) {
				dnsBean.setError("Duplicate record!!! Kindly delete this.");
			} else {
				Object object = returnedObjects.get(0);
				List<DnsBulkPtr> dnsBulkPtrList = (List<DnsBulkPtr>) object;
				if (dnsBulkPtrList.size() > 1) {
					dnsBean.setError("This record is already there!!!");
				} else if (dnsBulkPtrList.size() == 1) {
					if (dnsBulkPtrList.get(0).getId() != dnsBean.getId()) {
						dnsBean.setError("This record is already there!!!");
					}
				}
			}
			break;
		case "spf":
			DnsBulkSpf dnsBulkSpf = (DnsBulkSpf) o;
			if (dnsBulkSpf.getDnsError() != null && !dnsBulkSpf.getDnsError().isEmpty()) {
				dnsBean.setError("Duplicate record!!! Kindly delete this.");
			} else {
				Object object = returnedObjects.get(0);
				List<DnsBulkSpf> dnsBulkSpfList = (List<DnsBulkSpf>) object;
				if (dnsBulkSpfList.size() > 1) {
					dnsBean.setError("This record is already there!!!");
				} else if (dnsBulkSpfList.size() == 1) {
					if (dnsBulkSpfList.get(0).getId() != dnsBean.getId()) {
						dnsBean.setError("This record is already there!!!");
					}
				}
			}
			break;
		case "srv":
			DnsBulkSrv dnsBulkSrv = (DnsBulkSrv) o;
			if (dnsBulkSrv.getDnsError() != null && !dnsBulkSrv.getDnsError().isEmpty()) {
				dnsBean.setError("Duplicate record!!! Kindly delete this.");
			} else {
				Object object = returnedObjects.get(0);
				List<DnsBulkSrv> dnsBulkSrvList = (List<DnsBulkSrv>) object;
				if (dnsBulkSrvList.size() > 1) {
					dnsBean.setError("This record is already there!!!");
				} else if (dnsBulkSrvList.size() == 1) {
					if (dnsBulkSrvList.get(0).getId() != dnsBean.getId()) {
						dnsBean.setError("This record is already there!!!");
					}
				}
			}
			break;
		case "dmarc":
			DnsBulkDmarc dnsBulkDmarc = (DnsBulkDmarc) o;
			if (dnsBulkDmarc.getDnsError() != null && !dnsBulkDmarc.getDnsError().isEmpty()) {
				dnsBean.setError("Duplicate record!!! Kindly delete this.");
			} else {
				Object object = returnedObjects.get(0);
				List<DnsBulkDmarc> dnsBulkDmarcList = (List<DnsBulkDmarc>) object;
				if (dnsBulkDmarcList.size() > 1) {
					dnsBean.setError("This record is already there!!!");
				} else if (dnsBulkDmarcList.size() == 1) {
					if (dnsBulkDmarcList.get(0).getId() != dnsBean.getId()) {
						dnsBean.setError("This record is already there!!!");
					}
				}
			}
			break;
		case "aaaa":
			DnsBulkUpload dnsBulkUpload = (DnsBulkUpload) o;
			if (dnsBulkUpload.getDnsError() != null && !dnsBulkUpload.getDnsError().isEmpty()) {
				dnsBean.setError("Duplicate record!!! Kindly delete this.");
			} else {
				Object object = returnedObjects.get(0);
				List<DnsBulkUpload> dnsBulkUploadList = (List<DnsBulkUpload>) object;
				if (dnsBulkUploadList.size() > 1) {
					dnsBean.setError("This record is already there!!!");
				} else if (dnsBulkUploadList.size() == 1) {
					if (dnsBulkUploadList.get(0).getId() != dnsBean.getId()) {
						dnsBean.setError("This record is already there!!!");
					}
				}

				if (dnsBean.getReq().equals("req_modify") && dnsBean.getReqOtherAdd().equalsIgnoreCase("aaaa")) {
					String returnString = dnsService.checkDomain(dnsBean.getDomain());
					if (!dnsBean.getOldIp().isEmpty() && !returnString.equalsIgnoreCase(dnsBean.getOldIp())) {
						dnsBean.setError(
								"Please enter the correct Old IP mapped with domain (" + dnsBean.getDomain() + ")");
					}
				}
			}
			break;
		}
		return dnsBean;
	}

	private String validateSingleRecord(@NotEmpty ValidatedDnsBean dnsData, String email) {
		log.debug("validation of single record in Preview");
		boolean flag = true;

		String errorInString = "";
		String originalDomain = dnsData.getDomain();
		String domain = dnsData.getDomain();

		switch (dnsData.getReq()) {
		case "req_new":
			switch (dnsData.getReqOtherAdd()) {
			case "aaaa":
				if (dnsData.getNewIp().isEmpty()) {
					flag = false;
					errorInString += "Please enter new IP." + " | ";
				}

				if (dnsData.getDomain().startsWith("www.")) {
					String returnString = dnsService.checkDomain(dnsData.getDomain());
					if (!returnString.isEmpty()) {
						if (dnsService.doesIPbelongToNIC(returnString) || dnsService.doesIPbelongToNIC(dnsData.getNewIp())) {
							if (dnsService.isValidIp(returnString)) {
								errorInString += "Domain (" + dnsData.getDomain() + ") already exists with CNAME ("
										+ returnString + "). Please use modify option." + " | ";
								flag = false;
							} else {
								errorInString += "Domain (" + dnsData.getDomain() + ") already exists with ip ("
										+ returnString + ")" + " | ";
								flag = false;
							}
						}
					}
				}

				if (flag) {
					if (dnsService.isThirdLevelDomain(dnsData.getDomain())) {
						if (dnsData.getDomain().startsWith("www.")) {
							dnsData.setDomain(originalDomain.substring(4));
						}
						String returnString = dnsService.checkDomain(dnsData.getDomain());
						if (!returnString.isEmpty()) {
							if (dnsService.doesIPbelongToNIC(returnString) || dnsService.doesIPbelongToNIC(dnsData.getNewIp())) {
								if (originalDomain.startsWith("www.")) {
									if (dnsService.isValidIp(returnString)) {
										errorInString += "Domain (" + dnsData.getDomain()
												+ ") already exists with CNAME (" + returnString + ")."
												+ " Please add (" + originalDomain + ") as CNAME. | ";
										flag = false;
									} else {
										errorInString += "Domain (" + dnsData.getDomain() + ") already exists with ip ("
												+ returnString + ")." + " Please add (" + originalDomain
												+ ") as CNAME. | ";
										flag = false;
									}
								} else {
									if (dnsService.isValidIp(returnString)) {
										errorInString += "Domain (" + domain + ") already exists with CNAME ("
												+ returnString + "). Please use modify option." + " | ";
										flag = false;
									} else {
										errorInString += "Domain (" + domain + ") already exists with ip ("
												+ returnString + ")" + " | ";
										flag = false;
									}
								}
							}
						} else {
							if (dnsService.doesIPbelongToNIC(dnsData.getNewIp())) {
								returnString = dnsService.checkIP(dnsData.getNewIp());
								if (!returnString.isEmpty()) {
									errorInString += "IP (" + dnsData.getNewIp() + ") already exists with domain ("
											+ returnString + ")" + " | ";
									flag = false;
								}
							}
						}

					} else {
						if (dnsService.isGovDomain(dnsData.getDomain())) {
							errorInString += "Please go to https://registry.gov.in/domain_process.php and coordinate with sunita.singh@nic.in"
									+ " | ";
							flag = false;
						} else {
							if (dnsService.doesIPbelongToNIC(dnsData.getNewIp())) {
								String returnString = dnsService.checkIP(dnsData.getNewIp());
								if (!returnString.isEmpty()) {
									if (dnsService.isThirdLevelDomain(returnString)) {
										errorInString += "IP (" + dnsData.getNewIp() + ") already exists with domain ("
												+ returnString + ")" + " | ";
										flag = false;
									}
								}
							}
						}
					}
				}
				break;
			case "cname":
				if (dnsData.getCnameTxt().isEmpty()) {
					errorInString += "Enter the CNAME [e.g.: demo.nic.in or demo.gov.in]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "mx":
				if (dnsData.getMxTxt().isEmpty()) {
					errorInString += "Enter MX value, [Alphanumeric,dot(.),comma(,),hyphen(-),underscore(_),slash(/) and whitespaces] allowed";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
							flag = false;
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "spf":
				if (dnsData.getSpfTxt().isEmpty()) {
					errorInString += "Enter valid SPF value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "srv":
				if (dnsData.getSrvTxt().isEmpty()) {
					errorInString += "Enter valid SRV value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "txt":
				if (dnsData.getTxtTxt().isEmpty()) {
					errorInString += "Enter valid TXT value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "ptr":
				if (dnsData.getPtrTxt().isEmpty()) {
					errorInString += "Enter the IPV4/IPV6 Address";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {

						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "dmarc":
				if (dnsData.getDmarcTxt().isEmpty()) {
					errorInString += "Enter valid DMARC value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {

						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			default:
				break;
			}
			break;
		case "req_delete":
			switch (dnsData.getReqOtherAdd()) {
			case "aaaa":
				if (!dnsData.getNewIp().isEmpty() && !dnsService.isValidIp(dnsData.getNewIp())) {
					errorInString += "Please enter valid new IP.";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (!dnsData.getNewIp().isEmpty()) {
							if (dnsService.isThirdLevelDomain(dnsData.getDomain())) {
								String returnString = dnsService.checkDomain(dnsData.getDomain());
								if (returnString.isEmpty()) {
									errorInString += "This domain (" + dnsData.getDomain()
											+ ") is not available. Enter correct domain.";
								} else if (!returnString.equalsIgnoreCase(dnsData.getNewIp())) {
									errorInString += "Please enter the correct Old IP mapped with domain ("
											+ dnsData.getDomain() + ")";
								}
							} else {
								if (dnsService.isGovDomain(dnsData.getDomain())) {
									errorInString += "Please go to https://registry.gov.in/domain_process.php and coordinate with sunita.singh@nic.in";
								} else {
									String returnString = dnsService.checkDomain(dnsData.getDomain());
									if (returnString.isEmpty()) {
										errorInString += "This domain (" + dnsData.getDomain()
												+ ") is not available. Enter correct domain.";
									} else if (!returnString.equalsIgnoreCase(dnsData.getNewIp())) {
										errorInString += "Please enter the correct Old IP mapped with domain ("
												+ dnsData.getDomain() + ")";
									}
								}
							}
						} else {
							if (dnsService.isThirdLevelDomain(dnsData.getDomain())) {
								String returnString = dnsService.checkDomain(dnsData.getDomain());
								if (returnString.isEmpty()) {
									errorInString += "This domain (" + dnsData.getDomain()
											+ ") is not available. Enter correct domain.";
								}
							} else {
								if (dnsService.isGovDomain(dnsData.getDomain())) {
									errorInString += "Please go to https://registry.gov.in/domain_process.php and coordinate with sunita.singh@nic.in";
								} else {
									String returnString = dnsService.checkDomain(dnsData.getDomain());
									if (returnString.isEmpty()) {
										errorInString += "This domain (" + dnsData.getDomain()
												+ ") is not available. Enter correct domain.";
									}
								}
							}
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "cname":

				if (dnsData.getCnameTxt().isEmpty()) {
					errorInString += "Enter the CNAME [e.g.: demo.nic.in or demo.gov.in]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
							flag = false;
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
						flag = false;
					}
				}

				break;
			case "mx":
				if (dnsData.getMxTxt().isEmpty()) {
					errorInString += "Enter MX value, [Alphanumeric,dot(.),comma(,),hyphen(-),underscore(_),slash(/) and whitespaces] allowed";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "spf":
				if (dnsData.getSpfTxt().isEmpty()) {
					errorInString += "Enter valid SPF value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
							flag = false;
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "srv":
				if (dnsData.getSrvTxt().isEmpty()) {
					errorInString += "Enter valid SRV value [limit 2-300]" + " | ";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "txt":
				if (dnsData.getTxtTxt().isEmpty()) {
					errorInString += "Enter valid TXT value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "ptr":
				if (dnsData.getPtrTxt().isEmpty()) {
					errorInString += "Enter the IPV4/IPV6 Address";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "dmarc":
				if (dnsData.getDmarcTxt().isEmpty()) {
					errorInString += "Enter valid DMARC value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			default:
				break;
			}
			break;
		case "req_modify":
			switch (dnsData.getReqOtherAdd()) {
			case "aaaa":
				if (dnsData.getNewIp().isEmpty()) {
					errorInString += "Please enter new IP.";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.isThirdLevelDomain(dnsData.getDomain())) {
							String returnString = dnsService.checkDomain(dnsData.getDomain());
							if (returnString.isEmpty()) {
								errorInString += "It seems a new domain!!! Please enter the correct domain.";
							} else {
								if (!dnsData.getOldIp().isEmpty() && !returnString.equalsIgnoreCase(dnsData.getOldIp())
										&& !dnsService.isValidIp(returnString)) {
									errorInString += "Please enter the correct Old IP mapped with domain ("
											+ dnsData.getDomain() + ")";
								}

								if (dnsService.doesIPbelongToNIC(dnsData.getNewIp())) {
									returnString = dnsService.checkIP(dnsData.getNewIp());
									if (!returnString.isEmpty()) {
										errorInString += "IP (" + dnsData.getNewIp() + ") already exists with domain ("
												+ returnString + ")";
									}
								}
							}
						} else {
							if (dnsService.isGovDomain(dnsData.getDomain())) {
								errorInString += "Please go to https://registry.gov.in/domain_process.php and coordinate with sunita.singh@nic.in";
							} else {
								String returnString = dnsService.checkDomain(dnsData.getDomain());
								if (returnString.isEmpty()) {
									errorInString += "It seems a new domain!!! Please enter the correct domain. | ";
								} else {
									if (!dnsData.getOldIp().isEmpty()
											&& !returnString.equalsIgnoreCase(dnsData.getOldIp())
											&& !dnsService.isValidIp(returnString)) {
										errorInString += "Please enter the correct Old IP mapped with domain ("
												+ dnsData.getDomain() + ")";
									}

									if (dnsService.doesIPbelongToNIC(dnsData.getNewIp())) {
										returnString = dnsService.checkIP(dnsData.getNewIp());
										if (!returnString.isEmpty()) {
											if (dnsService.isThirdLevelDomain(returnString)) {
												errorInString += "IP (" + dnsData.getNewIp()
														+ ") already exists with domain (" + returnString + ")";
											}
										}
									}
								}
							}
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "cname":
				if (dnsData.getCnameTxt().isEmpty()) {
					errorInString += "Enter valid CNAME [e.g.: demo.nic.in or demo.gov.in]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "mx":
				if (dnsData.getMxTxt().isEmpty()) {
					errorInString += "Enter valid MX value, [Alphanumeric,dot(.),comma(,),hyphen(-),underscore(_),slash(/) and whitespaces] allowed";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "spf":
				if (dnsData.getSpfTxt().isEmpty()) {
					errorInString += "Enter valid SPF value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "srv":
				if (dnsData.getSrvTxt().isEmpty()) {
					errorInString += "Enter valid SRV value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "txt":
				if (dnsData.getTxtTxt().isEmpty()) {
					errorInString += "Enter valid TXT value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "ptr":
				if (dnsData.getPtrTxt().isEmpty()) {
					errorInString += "Enter the valid IPV4/IPV6 Address";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
							flag = false;
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			case "dmarc":
				if (dnsData.getDmarcTxt().isEmpty()) {
					errorInString += "Enter valid DMARC value [limit 2-300]";
				} else {
					if (dnsService.isApplicantOwner(dnsData.getDomain(), email)) {
						if (dnsService.checkDomain(dnsData.getDomain()).isEmpty()) {
							errorInString += "It seems a new domain!!! Please enter the correct domain.";
						}
					} else {
						if (dnsService.doNslookup(dnsData.getDomain())) {
							errorInString += "Since, you are not the owner of this domain, please coordinate with dns-request@nic.in (01124305093) to modify the IP for domain ("
									+ dnsData.getDomain() + ")";
						} else {
							errorInString += "Domain (" + dnsData.getDomain()
									+ ") does not exist. Kindly get it created first and then apply for adding records through modify option.";
						}
					}
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return errorInString;
	}

	private FinalAuditTrack isRequestCompleted(ValidatedDnsBean dnsBean, Object o) {
		FinalAuditTrack finalAuditTrackStatus = null;
		dnsBean.setError("");
		switch (dnsBean.getReqOtherAdd()) {
		case "cname":
			DnsBulkCname dnsBulkCname = (DnsBulkCname) o;
			if (dnsBulkCname.getRegistrationNo() != null && !dnsBulkCname.getRegistrationNo().isEmpty()) {
				finalAuditTrackStatus = utilityService.fetchFinalAuditTrack(dnsBulkCname.getRegistrationNo());
			}
			break;
		case "mx":
			DnsBulkMx dnsBulkMx = (DnsBulkMx) o;
			if (dnsBulkMx.getRegistrationNo() != null && !dnsBulkMx.getRegistrationNo().isEmpty())
				finalAuditTrackStatus = utilityService.fetchFinalAuditTrack(dnsBulkMx.getRegistrationNo());
			break;
		case "txt":
			DnsBulkTxt dnsBulkTxt = (DnsBulkTxt) o;
			if (dnsBulkTxt.getRegistrationNo() != null && !dnsBulkTxt.getRegistrationNo().isEmpty())
				finalAuditTrackStatus = utilityService.fetchFinalAuditTrack(dnsBulkTxt.getRegistrationNo());
			break;
		case "ptr":
			DnsBulkPtr dnsBulkPtr = (DnsBulkPtr) o;
			if (dnsBulkPtr.getRegistrationNo() != null && !dnsBulkPtr.getRegistrationNo().isEmpty())
				finalAuditTrackStatus = utilityService.fetchFinalAuditTrack(dnsBulkPtr.getRegistrationNo());
			break;
		case "spf":
			DnsBulkSpf dnsBulkSpf = (DnsBulkSpf) o;
			if (dnsBulkSpf.getRegistrationNo() != null && !dnsBulkSpf.getRegistrationNo().isEmpty())
				finalAuditTrackStatus = utilityService.fetchFinalAuditTrack(dnsBulkSpf.getRegistrationNo());
			break;
		case "srv":
			DnsBulkSrv dnsBulkSrv = (DnsBulkSrv) o;
			if (dnsBulkSrv.getRegistrationNo() != null && !dnsBulkSrv.getRegistrationNo().isEmpty())
				finalAuditTrackStatus = utilityService.fetchFinalAuditTrack(dnsBulkSrv.getRegistrationNo());
			break;
		case "dmarc":
			DnsBulkDmarc dnsBulkDmarc = (DnsBulkDmarc) o;
			if (dnsBulkDmarc.getRegistrationNo() != null && !dnsBulkDmarc.getRegistrationNo().isEmpty())
				finalAuditTrackStatus = utilityService.fetchFinalAuditTrack(dnsBulkDmarc.getRegistrationNo());
			break;
		case "aaaa":
			DnsBulkUpload dnsBulkUpload = (DnsBulkUpload) o;
			if (dnsBulkUpload.getRegistrationNo() != null && !dnsBulkUpload.getRegistrationNo().isEmpty())
				finalAuditTrackStatus = utilityService.fetchFinalAuditTrack(dnsBulkUpload.getRegistrationNo());
			break;
		}
		return finalAuditTrackStatus;
	}

}