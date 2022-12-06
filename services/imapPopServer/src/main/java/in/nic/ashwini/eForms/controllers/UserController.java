package in.nic.ashwini.eForms.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.ImapPopBase;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.NextHopBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.ImapPopService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	private final ImapPopService imappopService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	private NextHopBean nextHopBean;
	@Autowired
	public UserController(ImapPopService imappopService, ResponseBean responseBean, Util utilityService) {
		super();
		this.imappopService = imappopService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {

		String formType = "imappop";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");
		Map<String,Object> map = validateRequest(previewFormBean);
		System.out.println("Map Size:::: "+map.size());
		System.out.println("Map:::: "+map);
		if(map.size() == 0) {
			responseBean.setErrors(null);
		if (profile != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(), profile.getMobile(),
					profile.getName(),"user");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, previewFormBean.getRemarks(),
					profile.getMobile(), profile.getName(),"user","");

			ModelMapper modelMapper = new ModelMapper();
			if (previewFormBean.getEmployment().equalsIgnoreCase("state")
					&& previewFormBean.getState().equalsIgnoreCase("Himachal Pradesh")) {
				List<String> himachalCoords = utilityService.fetchHimachalCoords(previewFormBean.getDepartment());
				if (himachalCoords != null && himachalCoords.size() > 0) {
					String coordEmail = himachalCoords.get(0);
					if (utilityService.isGovEmployee(coordEmail)) {
						HodDetailsDto roDetails = utilityService.getHodValues(coordEmail);
						profile.setHodEmail(coordEmail);
						profile.setHodMobile(roDetails.getMobile());
						profile.setHodName(roDetails.getFirstName());
						profile.setHodDesignation(roDetails.getDesignation());
						profile.setHodTelephone(roDetails.getTelephoneNumber());
					}
				}
			}

			ImapPopBase imapPopBase = modelMapper.map(profile, ImapPopBase.class);
			imapPopBase.setPdfPath(submissionType);
			BeanUtils.copyProperties(previewFormBean, imapPopBase);
			LocalDateTime currentTime = LocalDateTime.now();
			imapPopBase.setDatetime(currentTime);
			imapPopBase.setLastUpdationDateTime(currentTime);
			imapPopBase.setUserIp(ip);

			for (int i = 0; i < 4; i++) {
				imapPopBase = imappopService.insert(imapPopBase);
				if (imapPopBase.getId() > 0) {
					break;
				}
			}

			if (imapPopBase.getId() > 0) {
				if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
					if (utilityService.isNicEmployee(email)) {
						status.setRegistrationNo(imapPopBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
						status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
						status.setRecipient(Constants.MAILADMIN_EMAIL);

						finalAuditTrack.setRegistrationNo(imapPopBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
						finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);

						if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", imapPopBase.getRegistrationNo());
							responseBean.setStatus("Request submitted successfully and forwarded to Admin ("
									+ Constants.MAILADMIN_EMAIL + ")");
							responseBean.setRegNumber(imapPopBase.getRegistrationNo());
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					} else {
						status.setRegistrationNo(imapPopBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_CA_TYPE);
						status.setStatus(Constants.STATUS_CA_PENDING);
						status.setRecipient(profile.getHodEmail());

						finalAuditTrack.setRegistrationNo(imapPopBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
						finalAuditTrack.setToEmail(profile.getHodEmail());

						if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", imapPopBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
											+ profile.getHodEmail() + ")");
							responseBean.setRegNumber(imapPopBase.getRegistrationNo());
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					}
				} else {
					status.setRegistrationNo(imapPopBase.getRegistrationNo());
					status.setRecipientType(Constants.STATUS_USER_TYPE);
					status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					status.setRecipient(email);

					finalAuditTrack.setRegistrationNo(imapPopBase.getRegistrationNo());
					finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					finalAuditTrack.setToEmail(email);

					if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
						log.info("{} submitted successfully.", imapPopBase.getRegistrationNo());
						responseBean.setStatus(
								"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
						responseBean.setRegNumber(imapPopBase.getRegistrationNo());
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
		}else {
			responseBean.setErrors(map);
			responseBean.setRegNumber("");
			responseBean.setStatus("Application could not be submitted.");
		}
		return responseBean;
	}

	@RequestMapping(value = "/validateRequest")
	public Map<String,Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean) {
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();
		
		if(!previewFormBean.getRequestFor().equalsIgnoreCase("imap") && !previewFormBean.getRequestFor().equalsIgnoreCase("pop")) {
			log.debug("Selected protocol is neither imap nor pop");
			map.put("protocolError", "Selected protocol is neither imap nor pop.");
		}
		
		if(!previewFormBean.getTnc()) {
			log.debug("Terms and condition is not selected.");
			map.put("tncError", "Please select Terms and Condition to proceed.");
		}
		
		BeanUtils.copyProperties(previewFormBean, organizationDetails);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;
		
		if(organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
			log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse, ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(orgError != null)
			map.put("orgError",orgError);
		}
		if(map.size() > 1) {
			finalmap.put("errors", map);
		}
		return map;
	}

	
	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = "imappop";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ImapPopBase imapPopBase = imappopService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, imapPopBase.getMobile(), imapPopBase.getName(),"user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		if (utilityService.isNicEmployee(email)) {
			status.setRegistrationNo(regNumber);
			status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
			status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
			status.setRecipient(Constants.MAILADMIN_EMAIL);

			finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
			finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);

			if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", regNumber);
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Admin (" + Constants.MAILADMIN_EMAIL + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}
		} else {
			status.setRegistrationNo(regNumber);
			status.setRecipientType(Constants.STATUS_CA_TYPE);
			status.setStatus(Constants.STATUS_CA_PENDING);
			status.setRecipient(imapPopBase.getHodEmail());

			finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
			finalAuditTrack.setToEmail(imapPopBase.getHodEmail());

			if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", regNumber);
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
								+ imapPopBase.getHodEmail() + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}
		}
		return responseBean;
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = "imappop";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ImapPopBase imapPopBase = imappopService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, imapPopBase.getMobile(), imapPopBase.getName(),"user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	@PostMapping("/validate")
	public Map<String,Object> validate(@RequestParam("protocol") @NotEmpty String protocol) {
		Map<String,Object> error = new HashMap<>();
		if(!(protocol.equalsIgnoreCase("imap") || protocol.equalsIgnoreCase("pop"))) {
			error.put("protocol", "Selected protocol is neither imap nor pop.");
		}
		return error;
	}

	@GetMapping("/fetchNextHop")
	public NextHopBean findNextHop(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {

		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		if (profile != null) {
			if (previewFormBean.getEmployment().equalsIgnoreCase("state")
					&& previewFormBean.getState().equalsIgnoreCase("Himachal Pradesh")) {
				List<String> himachalCoords = utilityService.fetchHimachalCoords(previewFormBean.getDepartment());
				if (himachalCoords != null && himachalCoords.size() > 0) {
					String coordEmail = himachalCoords.get(0);
					if (utilityService.isGovEmployee(coordEmail)) {
						HodDetailsDto roDetails = utilityService.getHodValues(coordEmail);
						profile.setHodEmail(coordEmail);
						profile.setHodMobile(roDetails.getMobile());
						profile.setHodName(roDetails.getFirstName());
						profile.setHodDesignation(roDetails.getDesignation());
						profile.setHodTelephone(roDetails.getTelephoneNumber());
					}
				}
			}

			if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
				if (utilityService.isNicEmployee(email)) {
					nextHopBean.setEmail(Constants.MAILADMIN_EMAIL);
					nextHopBean.setName("iNOC Support");
					nextHopBean.setDesignation("support");
					nextHopBean.setMobile("");
					nextHopBean.setRole("Admin");
					nextHopBean.setStatus("Request is getting forwarded to admin.");
				} else {
					nextHopBean.setEmail(profile.getHodEmail());
					nextHopBean.setName(profile.getHodName());
					nextHopBean.setDesignation(profile.getHodDesignation());
					nextHopBean.setMobile(profile.getHodMobile());
					nextHopBean.setRole("Reporting/Forwarding/Nodal Officer");
					nextHopBean.setStatus("Request is getting forwarded to your reporting/forwarding/nodal officer.");
				}
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
					"Hey, {},  We do not have your profile in eForms. Please go to profile section and make your profile first");
			nextHopBean.setStatus(
					"We do not have your profile in eForms. Please go to profile section and make your profile first");
		}
		return nextHopBean;
	}
}
