package in.nic.ashwini.eForms.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.RelayBase;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.ManualUploadBean;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.models.ValidateFormBean;
import in.nic.ashwini.eForms.repositories.RelayBaseRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RelayServiceUser {

	private final ResponseBean responseBean;
	private final Util utilityService;
	private final ValidationService validationService;
	private final RelayBaseRepo relayBaseRepo;
	 @Value("${fileBasePath}")
	 private String fileBasePath;
	@Autowired
	public RelayServiceUser(ResponseBean responseBean, Util utilityService, ValidationService validationService,
			RelayBaseRepo relayBaseRepo) {
		super();
		this.validationService = validationService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
		this.relayBaseRepo = relayBaseRepo;

	}

	public ResponseBean submitRequest(PreviewFormBean previewFormBean, String ip, String email, String submissionType)
			throws IOException {

		String formType = "relay";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");
		Map<Object, Object> map = validateRequest(previewFormBean);
		System.out.println("Map Size:::: " + map.size());
		System.out.println("Map:::: " + map);
		if (map.size() == 0) {
			responseBean.setErrors(null);
			if (profile != null) {
				status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(),
						profile.getMobile(), profile.getName(), "user");
				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType,
						previewFormBean.getRemarks(), profile.getMobile(), profile.getName(), "user", "");

				ModelMapper modelMapper = new ModelMapper();

				RelayBase relayBase = modelMapper.map(profile, RelayBase.class);
				relayBase.setPdfPath(submissionType);
				if (previewFormBean.getCertFile() != null) {
					System.out.println("CertFILEEEe:::::::::::" + previewFormBean.getCertFile().getOriginalFilename());
					relayBase.setCertFile(previewFormBean.getCertFile().getOriginalFilename());
					relayBase.setRenamedFilepath(
							Constants.LOCAL_FILE_LOCATION + previewFormBean.getCertFile().getOriginalFilename());
					byte[] bytes = previewFormBean.getCertFile().getBytes();
					Path path = Paths
							.get(Constants.LOCAL_FILE_LOCATION + previewFormBean.getCertFile().getOriginalFilename());

					Files.write(path, bytes);

				}
				if (previewFormBean.getHardwareCertFile() != null) {
					System.out.println(
							"HardwareFILEEEe:::::::::::" + previewFormBean.getHardwareCertFile().getOriginalFilename());
					relayBase.setHardwareCertFile(previewFormBean.getHardwareCertFile().getOriginalFilename());
					relayBase.setRenamedHardwareFilepath(Constants.LOCAL_FILE_LOCATION
							+ previewFormBean.getHardwareCertFile().getOriginalFilename());
					byte[] bytes1 = previewFormBean.getHardwareCertFile().getBytes();
					Path path1 = Paths.get(Constants.LOCAL_FILE_LOCATION
							+ previewFormBean.getHardwareCertFile().getOriginalFilename());

					Files.write(path1, bytes1);
				}
				BeanUtils.copyProperties(previewFormBean, relayBase);
				LocalDateTime currentTime = LocalDateTime.now();
				relayBase.setDatetime(currentTime);
				// relayBase.setLastUpdationDateTime(currentTime);
				relayBase.setUserip(ip);

				for (int i = 0; i < 4; i++) {
					relayBase = insert(relayBase);
					if (relayBase.getId() > 0) {
						break;
					}
				}

				if (relayBase.getId() > 0) {
					if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
						if (utilityService.isNicEmployee(email)) {
							status.setRegistrationNo(relayBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_CA_TYPE);
							status.setStatus(Constants.STATUS_CA_PENDING);
							status.setRecipient(profile.getHodEmail());

							finalAuditTrack.setRegistrationNo(relayBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());

							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", relayBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(relayBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						} else {
							status.setRegistrationNo(relayBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_CA_TYPE);
							status.setStatus(Constants.STATUS_CA_PENDING);
							status.setRecipient(profile.getHodEmail());

							finalAuditTrack.setRegistrationNo(relayBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());

							if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", relayBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(relayBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						}
					} else {
						status.setRegistrationNo(relayBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_USER_TYPE);
						status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						status.setRecipient(email);

						finalAuditTrack.setRegistrationNo(relayBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						finalAuditTrack.setToEmail(email);

						if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", relayBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
							responseBean.setRegNumber(relayBase.getRegistrationNo());
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

	public Map<Object, Object> validateRequest(@Valid PreviewFormBean previewFormBean) {
		Map<Object, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();

		// validations

		if (previewFormBean.getOs().isEmpty()) {
			map.put("os_error",
					"Enter Operating System (Name, Version), [Only characters limit[100],whitespaces,comma(,),hypen(-) allowed]");
		}
		if (previewFormBean.getDivisionName().isEmpty()) {
			map.put("division_error",
					"Enter Name of Division, [characters only limit[50], dot(,),comma(,) whitespaces allowed]");
		}
		if (previewFormBean.getServerLoc().isEmpty()) {
			map.put("serverLocError", "please enter location");
		}
		if (previewFormBean.getAppName().isEmpty()) {
			map.put("appNameError", "please enter application name");
		}
		if (validationService.relayUrlValidation(previewFormBean.getAppUrl())) {
			map.put("appUrlError", "please enter correct url");
		}
		if (previewFormBean.getAppUrl().isEmpty()) {
			map.put("appUrlError", "please enter correct url");
		}

		if (previewFormBean.getRequestFor().equals("req_new")) {

			if (previewFormBean.getAppIp().contains(";")) {
				String[] relayip = previewFormBean.getAppIp().split(";");

				int io = 0;

				for (int i = 0; i < relayip.length; i++) {
					String ip = relayip[i];
					boolean ip_error = validationService.serviceipValidation(ip);
					io = io + 1;
					if (ip_error) {

						map.put("appIp_Error_" + io, "Enter the IP Address [e.g.: 164.100.X.X ]");
					}
				}

			} else {
				boolean ip1_error = validationService.baseipValidation(previewFormBean.getAppIp());
				if (ip1_error == true) {
					log.debug("application ip is not correct");
					map.put("appIp_error_1", "Enter the IP Address [e.g.: 164.100.X.X ]");
				}
			}

			if (previewFormBean.getStagingIp().equals("no")) {

				if (previewFormBean.getSecurityAudit().equalsIgnoreCase("Hardware")) {
					if (previewFormBean.getHardwareCertFile() == null
							|| previewFormBean.getHardwareCertFile().getOriginalFilename().isEmpty()) {
						map.put("hardwareCertError", "Please upload a hardware cert file");
						log.debug("need to upload a Hardware cert file");
					}
					if (previewFormBean.getHardwareCertFile() != null) {
						if (!previewFormBean.getHardwareCertFile().getContentType().equals("application/pdf")) {
							map.put("hardwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}

				if (previewFormBean.getSecurityAudit().equalsIgnoreCase("Software")) {
					if (previewFormBean.getCertFile() == null || previewFormBean.getCertFile().getOriginalFilename().isEmpty()) {
						map.put("softwareCertError", "Please upload a software cert file");
						log.debug("need to upload a Software cert file");
					}

					if (previewFormBean.getCertFile() != null) {
						if (!previewFormBean.getCertFile().getContentType().equals("application/pdf")) {
							map.put("softwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}
			}

			if (previewFormBean.getStagingIp().equals("yes")) {
				log.debug("no need to upload a file");
			}
		}

		if (previewFormBean.getRequestFor().equals("req_new") || previewFormBean.getRequestFor().equals("req_add")
				|| previewFormBean.getRequestFor().equals("req_modify")) {

			if (previewFormBean.getPort().isEmpty()) {

				map.put("portError", "please select a port");

			}

			if (previewFormBean.getPort().equals("465")) {

				if (previewFormBean.getRelayAuthId() == null && previewFormBean.getRelayAuthId().isEmpty()) {

					map.put("authIdError", "please enter an auth id");

				} else {
					boolean auth_id_error = validationService.EmailValidation(previewFormBean.getRelayAuthId());
					if (!utilityService.isEmailAvailable(previewFormBean.getRelayAuthId())) {
						map.put("auth_id_error", "Email address does not exist in ldap");
					} else {
						if (auth_id_error == true) {
							map.put("auth_id_error", "Enter auth id in correct format");
						}
					}
				}
			}
			if (previewFormBean.getSenderId().isEmpty()) {
				map.put("senderIdError", "please enter sender Id");
			}
		}

		if (previewFormBean.getServerLoc().equalsIgnoreCase("Other")) {

			if (previewFormBean.getOtherServerLoc().isEmpty()) {
				map.put("otherlocError", "please enter other server location");
			}
		}

		if (previewFormBean.getRequestFor().equals("req_add") || previewFormBean.getRequestFor().equals("req_modify")) {

			if (previewFormBean.getAppIp().contains(";")) {
				String[] relayip = previewFormBean.getAppIp().split(";");
				int io = 0;
				for (int i = 0; i < relayip.length; i++) {
					String ip = relayip[i];

					boolean ip_error = validationService.serviceipValidation(ip);
					io = io + 1;
					if (ip_error) {
						map.put("appIp_Error_" + io, "Enter the IP Address [e.g.: 164.100.X.X ]");
					}
				}
			} else {
				boolean ip1_error = validationService.baseipValidation(previewFormBean.getAppIp());
				if (ip1_error == true) {
					log.debug("application ip is not correct");
					map.put("appIp_error_1", "Enter the IP Address [e.g.: 164.100.X.X ]");
				}
			}

			if (previewFormBean.getOldAppIp().contains(";")) {
				String[] oldRelayip = previewFormBean.getOldAppIp().split(";");
				int it = 0;
				for (int i = 0; i < oldRelayip.length; i++) {
					String ip = oldRelayip[i];

					boolean ip_error = validationService.serviceipValidation(ip);
					it = it + 1;
					if (ip_error) {

						map.put("OldAppIp_Error_" + it, "Enter the IP Address [e.g.: 164.100.X.X ]");
					}
				}

			} else {
				boolean ip3_error = validationService.baseipValidation(previewFormBean.getOldAppIp());
				if (ip3_error == true) {
					log.debug(" old application ip is not correct");
					map.put("OldAppIp_Error_1", "Enter the IP Address [e.g.: 164.100.X.X ]");
				}
			}

			if (previewFormBean.getStagingIp().equals("no")) {

				if (previewFormBean.getSecurityAudit().equalsIgnoreCase("Hardware")) {

					if (previewFormBean.getHardwareCertFile() == null
							|| previewFormBean.getHardwareCertFile().isEmpty()) {
						map.put("hardwareCertError", "Please upload a hardware cert file");
						log.debug("need to upload a Hardware cert file");
					} else {
						if (!previewFormBean.getHardwareCertFile().getContentType().equals("application/pdf")) {
							map.put("hardwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}
				if (previewFormBean.getSecurityAudit().equalsIgnoreCase("Software")) {
					if (previewFormBean.getCertFile() == null || previewFormBean.getCertFile().isEmpty()) {
						map.put("softwareCertError", "Please upload a software cert file");
						log.debug("need to upload a Software cert file");
					} else {
						if (!previewFormBean.getCertFile().getContentType().equals("application/pdf")) {
							map.put("softwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}
			}

			if (previewFormBean.getStagingIp().equals("yes")) {
				log.debug("no need to upload a file");
			}

		}

		if (previewFormBean.getRequestFor().equals("req_new") || previewFormBean.getRequestFor().equals("req_add")
				|| previewFormBean.getRequestFor().equals("req_modify")) {

			if (previewFormBean.getSenderId().isEmpty()) {
				map.put("senderId_error", "please enter sender id");
			}
			if (previewFormBean.getPointName().isEmpty()) {
				map.put("p_name_error", "Please Enter Applicant Name [characters,dot(.) and whitespace]");
			}

			if (validationService.EmailValidation(previewFormBean.getPointEmail())) {
				map.put("p_email_error", "Please Enter Applicant Email]");
			}
			if (previewFormBean.getPointMobileNumber().isEmpty()) {
				map.put("p_mobile_error", "Please Enter Applicant Mobile [e.g:+919999999999]");
			}
			if (previewFormBean.getPort().equals("465")) {
				if (previewFormBean.getRelayAuthId().isEmpty()) {
					map.put("auth_id_error", "Please Enter Correct Auth Id");
				}
			}
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

	public ResponseBean approve(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = "relay";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		RelayBase relayBase = preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, relayBase.getMobile(),
				relayBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
//change

		if (utilityService.isNicEmployee(email)) {
			status.setRegistrationNo(regNumber);
			status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
			status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
			status.setRecipient(Constants.MAILADMIN_EMAIL);

			finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
			finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);

			if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
			status.setRecipient(relayBase.getHodEmail());

			finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
			finalAuditTrack.setToEmail(relayBase.getHodEmail());

			if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", regNumber);
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
								+ relayBase.getHodEmail() + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}
		}
		return responseBean;
	}

	public ResponseBean reject(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = "relay";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		RelayBase relayBase = preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, relayBase.getMobile(),
				relayBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	public RelayBase preview(String regNo) {
		return relayBaseRepo.findByRegistrationNo(regNo);
	}

	@Transactional
	public RelayBase insert(RelayBase relayBase) {
		if (relayBase != null) {
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String pdate = dateFormat.format(date);
			String oldRegNumber = relayBaseRepo.findLatestRegistrationNo();
			String newRegNumber = "RELAY-FORM" + pdate;
			if (oldRegNumber == null || oldRegNumber.isEmpty()) {
				newRegNumber += "0001";
			} else {
				String lastst = oldRegNumber.substring(20, oldRegNumber.length());
				int last = Integer.parseInt(lastst);
				int newrefno = last + 1;
				int len = Integer.toString(newrefno).length();
				if (len == 1) {
					newRegNumber += "000" + newrefno;
				} else if (len == 2) {
					newRegNumber += "00" + newrefno;
				} else if (len == 3) {
					newRegNumber += "0" + newrefno;
				}
			}
			relayBase.setRegistrationNo(newRegNumber);
			relayBase.setSupportActionTaken("p");
			return relayBaseRepo.save(relayBase);
		}
		return null;
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

	public Map<Object, Object> validate(@Valid ValidateFormBean validateFormBean) {

		Map<Object, Object> error = new HashMap<>();

		if (validateFormBean.getOs().isEmpty()) {
			error.put("os_error",
					"Enter Operating System (Name, Version), [Only characters limit[100],whitespaces,comma(,),hypen(-) allowed]");
		}
		if (validateFormBean.getDivisionName().isEmpty()) {
			error.put("division_error",
					"Enter Name of Division, [characters only limit[50], dot(,),comma(,) whitespaces allowed]");
		}
		if (validateFormBean.getServerLoc().isEmpty()) {
			error.put("serverLocError", "please enter location");
		}
		if (validateFormBean.getAppName().isEmpty()) {
			error.put("appNameError", "please enter application name");
		}
		if (validationService.relayUrlValidation(validateFormBean.getAppUrl())) {
			error.put("appUrlError", "please enter correct url");
		}
		if (validateFormBean.getAppUrl().isEmpty()) {
			error.put("appUrlError", "please enter correct url");
		}

		if (validateFormBean.getRequestFor().equals("req_new")) {

			if (validateFormBean.getAppIp().contains(";")) {
				String[] relayip = validateFormBean.getAppIp().split(";");

				int io = 0;

				for (int i = 0; i < relayip.length; i++) {
					String ip = relayip[i];
					boolean ip_error = validationService.serviceipValidation(ip);
					io = io + 1;
					if (ip_error) {

						error.put("appIp_Error_" + io, "Enter the IP Address [e.g.: 164.100.X.X ]");
					}
				}

			} else {
				boolean ip1_error = validationService.baseipValidation(validateFormBean.getAppIp());
				if (ip1_error == true) {
					log.debug("application ip is not correct");
					error.put("appIp_error_1", "Enter the IP Address [e.g.: 164.100.X.X ]");
				}
			}

			if (validateFormBean.getStagingIp().equals("no")) {

				if (validateFormBean.getSecurityAudit().equalsIgnoreCase("Hardware")) {
					if (validateFormBean.getHardwareCertFile() == null
							|| validateFormBean.getHardwareCertFile().getOriginalFilename().isEmpty()) {
						error.put("hardwareCertError", "Please upload a hardware cert file");
						log.debug("need to upload a Hardware cert file");
					}
					if (validateFormBean.getHardwareCertFile() != null) {
						if (!validateFormBean.getHardwareCertFile().getContentType().equals("application/pdf")) {
							error.put("hardwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}

				if (validateFormBean.getSecurityAudit().equalsIgnoreCase("Software")) {
					if (validateFormBean.getCertFile() == null || validateFormBean.getCertFile().getOriginalFilename().isEmpty()) {
						error.put("softwareCertError", "Please upload a software cert file");
						log.debug("need to upload a Software cert file");
					}

					if (validateFormBean.getCertFile() != null) {
						if (!validateFormBean.getCertFile().getContentType().equals("application/pdf")) {
							error.put("softwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}
			}

			if (validateFormBean.getStagingIp().equals("yes")) {
				log.debug("no need to upload a file");
			}
		}

		if (validateFormBean.getRequestFor().equals("req_new") || validateFormBean.getRequestFor().equals("req_add")
				|| validateFormBean.getRequestFor().equals("req_modify")) {

			if (validateFormBean.getPort().isEmpty()) {

				error.put("portError", "please select a port");

			}

			if (validateFormBean.getPort().equals("465")) {

				if (validateFormBean.getRelayAuthId().isEmpty() || validateFormBean.getRelayAuthId() == null) {

					error.put("authIdError", "please enter an auth id");

				} else {
					boolean auth_id_error = validationService.EmailValidation(validateFormBean.getRelayAuthId());
					if (!utilityService.isEmailAvailable(validateFormBean.getRelayAuthId())) {
						error.put("auth_id_error", "Email address does not exist in ldap");
					} else {
						if (auth_id_error == true) {
							error.put("auth_id_error", "Enter auth id in correct format");
						}
					}
				}
			}
			if (validateFormBean.getSenderId().isEmpty()) {
				error.put("senderIdError", "please enter sender Id");
			}
		}

		if (validateFormBean.getServerLoc().equalsIgnoreCase("Other")) {

			if (validateFormBean.getOtherServerLoc().isEmpty()) {
				error.put("otherlocError", "please enter other server location");
			}
		}

		if (validateFormBean.getRequestFor().equals("req_add")
				|| validateFormBean.getRequestFor().equals("req_modify")) {

			if (validateFormBean.getAppIp().contains(";")) {
				String[] relayip = validateFormBean.getAppIp().split(";");
				int io = 0;
				for (int i = 0; i < relayip.length; i++) {
					String ip = relayip[i];

					boolean ip_error = validationService.serviceipValidation(ip);
					io = io + 1;
					if (ip_error) {
						error.put("appIp_Error_" + io, "Enter the IP Address [e.g.: 164.100.X.X ]");
					}
				}
			} else {
				boolean ip1_error = validationService.baseipValidation(validateFormBean.getAppIp());
				if (ip1_error == true) {
					log.debug("application ip is not correct");
					error.put("appIp_error_1", "Enter the IP Address [e.g.: 164.100.X.X ]");
				}
			}

			if (validateFormBean.getOldAppIp().contains(";")) {
				String[] oldRelayip = validateFormBean.getOldAppIp().split(";");
				int it = 0;
				for (int i = 0; i < oldRelayip.length; i++) {
					String ip = oldRelayip[i];

					boolean ip_error = validationService.serviceipValidation(ip);
					it = it + 1;
					if (ip_error) {

						error.put("OldAppIp_Error_" + it, "Enter the IP Address [e.g.: 164.100.X.X ]");
					}
				}

			} else {
				boolean ip3_error = validationService.baseipValidation(validateFormBean.getOldAppIp());
				if (ip3_error == true) {
					log.debug(" old application ip is not correct");
					error.put("OldAppIp_Error_1", "Enter the IP Address [e.g.: 164.100.X.X ]");
				}
			}

			if (validateFormBean.getStagingIp().equals("no")) {

				if (validateFormBean.getSecurityAudit().equalsIgnoreCase("Hardware")) {

					if (validateFormBean.getHardwareCertFile() == null
							|| validateFormBean.getHardwareCertFile().isEmpty()) {
						error.put("hardwareCertError", "Please upload a hardware cert file");
						log.debug("need to upload a Hardware cert file");
					} else {
						if (!validateFormBean.getHardwareCertFile().getContentType().equals("application/pdf")) {
							error.put("hardwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}
				if (validateFormBean.getSecurityAudit().equalsIgnoreCase("Software")) {
					if (validateFormBean.getCertFile() == null || validateFormBean.getCertFile().isEmpty()) {
						error.put("softwareCertError", "Please upload a software cert file");
						log.debug("need to upload a Software cert file");
					} else {
						if (!validateFormBean.getCertFile().getContentType().equals("application/pdf")) {
							error.put("softwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}
			}

			if (validateFormBean.getStagingIp().equals("yes")) {
				log.debug("no need to upload a file");
			}

		}

		if (validateFormBean.getRequestFor().equals("req_new") || validateFormBean.getRequestFor().equals("req_add")
				|| validateFormBean.getRequestFor().equals("req_modify")) {

			if (validateFormBean.getSenderId().isEmpty()) {
				error.put("senderId_error", "please enter sender id");
			}
			if (validateFormBean.getPointName().isEmpty()) {
				error.put("p_name_error", "Please Enter Applicant Name [characters,dot(.) and whitespace]");
			}
			if (validateFormBean.getPointEmail().isEmpty()) {
				error.put("p_email_error", "Please Enter Applicant Email]");
			}
			if (validateFormBean.getPointMobileNumber().isEmpty()) {
				error.put("p_mobile_error", "Please Enter Applicant Mobile [e.g:+919999999999]");
			}
			if (validateFormBean.getPort().equals("465")) {
				if (validateFormBean.getRelayAuthId().isEmpty()) {
					error.put("auth_id_error", "Please Enter Correct Auth Id");
				}
			}
		}
		return error;
	}

	public Map<String, String> fetchMx(String senderId) {
		Map<String, String> response = new HashMap<>();

		if (!utilityService.isEmailAvailable(senderId)) {
			response.put("error", "Only those domains whose mail services are with nic can avail our relay services.");
			response.put("mxdomain", "");
			return response;
		}

		if (senderId.contains("@")) {
			String[] mle = senderId.split("@");
			try {
				String[] mxDomain = lookupMailHosts(mle[1]);
				response.put("mxdomain", lookupMailHosts(mle[1])[0]);
				response.put("error", "");
			} catch (NamingException ex) {
				response.put("error", "Something went wrong!!!");
				response.put("mxdomain", "");
				ex.printStackTrace();
			}
		}
		return response;
	}

	private String[] lookupMailHosts(String domainName) throws NamingException {
		InitialDirContext iDirC = new InitialDirContext();
		Attributes attributes = iDirC.getAttributes("dns:/" + domainName, new String[] { "MX" });
		Attribute attributeMX = attributes.get("MX");
		if (attributeMX == null) {
			return (new String[] { domainName });
		}
		String[][] pvhn = new String[attributeMX.size()][2];
		for (int i = 0; i < attributeMX.size(); i++) {
			pvhn[i] = ("" + attributeMX.get(i)).split("\\s+");
		}
		Arrays.sort(pvhn, new Comparator<String[]>() {
			public int compare(String[] o1, String[] o2) {
				return (Integer.parseInt(o1[0]) - Integer.parseInt(o2[0]));
			}
		});
		String[] sortedHostNames = new String[pvhn.length];
		for (int i = 0; i < pvhn.length; i++) {
			sortedHostNames[i] = pvhn[i][1].endsWith(".") ? pvhn[i][1].substring(0, pvhn[i][1].length() - 1)
					: pvhn[i][1];
		}
		return sortedHostNames;
	}
	
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request, ResponseBean responseBean) throws IOException {
		
		manualUploadBean.setEmail(request.getParameter("email"));
		manualUploadBean.setClientIp(request.getParameter("clientIp"));
		
		responseBean.setRequestType("Forwarding of request by user");
		String formType = "relay";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		RelayBase relayBase = preview(manualUploadBean.getRegNumber());
		String[] contenttype = manualUploadBean.getInfile().getContentType().split("/");
		String ext = contenttype[1];
		String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
				.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.append(".").append(ext).toString();
		
		relayBase.setPdfPath(fileBasePath+outputfile);
		byte[] bytes = manualUploadBean.getInfile().getBytes();
		Path path = Paths.get(fileBasePath + outputfile);
		boolean stat = updaterelaybase(relayBase);
		if(stat) {
			
			Files.write(path, bytes);
		}
		else {
			
			responseBean.setStatus("File failed to upload");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
   		return responseBean;
		}
		String dn = utilityService.findDn(relayBase.getEmail());
		String roDn = utilityService.findDn(manualUploadBean.getEmail());
		List<String> aliases = utilityService.aliases(manualUploadBean.getEmail());
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), relayBase.getHodMobile(),
				relayBase.getHodName(),"usermanual" ,manualUploadBean.getRegNumber());
		status = utilityService.initializeStatusTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), relayBase.getHodMobile(), relayBase.getHodName(),"user");
		
		if (relayBase.getEmployment().equalsIgnoreCase("state")
				&& relayBase.getPostingState().equalsIgnoreCase("Assam")) {
			toWhom = "Coordinator";
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		}else if (relayBase.getEmployment().equalsIgnoreCase("State") && relayBase.getState().equalsIgnoreCase("punjab")
				&& relayBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "Coordinator";
			List<String> punjabCoords = utilityService.fetchPunjabCoords(relayBase.getCity());
			daEmail = String.join(",", punjabCoords);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail()) && (relayBase.getPostingState().equalsIgnoreCase("delhi")
				&& relayBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(relayBase.getEmail()) || utilityService.isNicEmployee(manualUploadBean.getEmail())) {
			toWhom = "ro";
			//daEmail = Constants.NKN_SUPPORT_EMAIL;
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;
			
			
		} else if (dn!=null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "ro";
			//daEmail = Constants.NKN_SUPPORT_EMAIL; 
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;
		} else if (relayBase.getEmployment().equalsIgnoreCase("Others")
				&& relayBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& relayBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& relayBase.getCity().equalsIgnoreCase("pune") && (relayBase.getAddress().toLowerCase().contains("ndc")
						|| relayBase.getAddress().toLowerCase().contains("national data center"))) {
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();

			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
		}  else {
              
			
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(relayBase, OrganizationBean.class);
				toWhom = "Reporting officer";
				daEmail = relayBase.getHodEmail();
				recipientType = Constants.STATUS_CA_TYPE;
				nextStatus = Constants.STATUS_CA_PENDING;
		}
		status.setRegistrationNo(manualUploadBean.getRegNumber());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + manualUploadBean.getRegNumber() + ")  Forwarded Successfully to " + toWhom + "(" + daEmail
					+ ")");
			responseBean.setStatus("Application (" + manualUploadBean.getRegNumber() + ") Forwarded Successfully to " + toWhom
					+ "(" + daEmail + ")");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
		} else {
			log.debug(Constants.ERROR_MESSAGE);
			responseBean.setStatus(Constants.ERROR_MESSAGE);
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
		}
		return responseBean;
	}
	public boolean updaterelaybase(RelayBase relayBase) {
		RelayBase mobiledetails = relayBaseRepo.save(relayBase);
		if (mobiledetails.getId() > 0) {
			return true;
		} else {
			return false;
		}
	}
	

}
