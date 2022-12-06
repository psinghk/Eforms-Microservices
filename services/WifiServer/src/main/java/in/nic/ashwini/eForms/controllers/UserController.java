package in.nic.ashwini.eForms.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.WifiBase;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.FormData;
import in.nic.ashwini.eForms.models.ManualUploadBean;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.WifiService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	private final WifiService wifiService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	 @Value("${fileBasePath}")
	private String fileBasePath;

	@Autowired
	public UserController(WifiService wifiService, ResponseBean responseBean, Util utilityService) {
		super();
		this.wifiService = wifiService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/applicants-wifi-data")
	public List<FormData> fetchApplicantData(@RequestParam("email") @NotEmpty String email) {
			return wifiService.fetchWifiDataForApplicant(utilityService.aliases(email));
	}

	@RequestMapping(value = "/pending-applicants-wifi-data")
	public List<FormData> fetchPendingWifiDataForApplicant(@RequestParam("email") @NotEmpty String email) {
		return wifiService.fetchPendingWifiDataForApplicant(utilityService.aliases(email));
		
	}
	@RequestMapping(value = "/complete-applicants-wifi-data")
	public List<FormData> fetchCompleteWifiDataForApplicant(@RequestParam("deleteEmail") @NotEmpty String deleteEmail) {
		System.out.println(":::::::::::::"+deleteEmail);
		return wifiService.fetchCompleteWifiDataForApplicant(utilityService.aliases(deleteEmail));
		
	}
	
	@RequestMapping(value = "/test")
	public String ok() {
		System.out.println("okk");
		return "ok tested";
	}

	@RequestMapping(value = "/submit-request")
	public ResponseBean submitRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {
		String formType = "wifi";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");
		Map<String, Object> errorMap = validateRequest(previewFormBean);
		if (errorMap.size() == 0) {
			responseBean.setErrors(null);
			if (profile != null) {
				status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(),
						profile.getMobile(), profile.getName(), "user");
				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType,
						previewFormBean.getRemarks(), profile.getMobile(), profile.getName(), "user", "");
			
				ModelMapper modelMapper = new ModelMapper();
				WifiBase wifiBase = modelMapper.map(profile, WifiBase.class);
				wifiBase.setPdfPath(submissionType);
				BeanUtils.copyProperties(previewFormBean, wifiBase);
				LocalDateTime currentTime = LocalDateTime.now();
				wifiBase.setDatetime(currentTime);
				wifiBase.setUserIp(ip);
				List<FormData> formdata = previewFormBean.getWifiFormDetails();
				List<String> macCount = wifiService.countOfMacWithThisUser(utilityService.aliases(email));
				List<String> countOfUserWithThisMac = previewFormBean.getWifiFormDetails().stream()
						.map(s -> s.getMachineAddress()).collect(Collectors.toList());
				String macAddressStr = String.join(",", countOfUserWithThisMac);
				List<String> countUserWithThisMac = wifiService.countOfUserWithThisMac(macAddressStr);

				errorMap = validateAction(wifiBase.getWifiRequest(), macCount.size(), countUserWithThisMac.size());
				if (errorMap.size() < 1) {
					wifiBase = wifiService.insert(wifiBase);
					if (wifiBase.getId() > 0) {
						String regNo = wifiBase.getRegistrationNo();
						wifiService.insertIntoEntry(regNo, formdata);
						if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
							if (utilityService.isNicEmployee(email).booleanValue()) {
								status.setRegistrationNo(wifiBase.getRegistrationNo());
								status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
								status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
								status.setRecipient(Constants.MAILADMIN_EMAIL);
								finalAuditTrack.setRegistrationNo(wifiBase.getRegistrationNo());
								finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
								finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);
								if (wifiService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
									log.info(Constants.SUBMITTED_SUCCESSFULLY, wifiBase.getRegistrationNo());
									responseBean.setStatus("Request submitted successfully and forwarded to Admin ("
											+ Constants.MAILADMIN_EMAIL + ")");
									responseBean.setRegNumber(wifiBase.getRegistrationNo());
								} else {
									log.debug(Constants.ERROR_MESSAGE);
									responseBean.setStatus(Constants.ERROR_MESSAGE);
									responseBean.setRegNumber("");
								}
							} else {
								if (profile.getDepartment() != null) {
									if (StringUtils.isNotEmpty(profile.getDepartment())
											&& profile.getDepartment().contains("Rajasthan Legislative Assembly")) {
										// TODO:: Need to apply check here
										status.setRecipient("mishra.vinod@nic.in");
										finalAuditTrack.setToEmail("mishra.vinod@nic.in");

									} else {
										status.setRecipient(profile.getHodEmail());
										finalAuditTrack.setToEmail(profile.getHodEmail());
									}
								}
								status.setRegistrationNo(wifiBase.getRegistrationNo());
								status.setRecipientType(Constants.STATUS_CA_TYPE);
								status.setStatus(Constants.STATUS_CA_PENDING);
								finalAuditTrack.setRegistrationNo(wifiBase.getRegistrationNo());
								finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
								if (wifiService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
									log.info(Constants.SUBMITTED_SUCCESSFULLY, wifiBase.getRegistrationNo());
									responseBean.setStatus(
											"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
													+ profile.getHodEmail() + ")");
									responseBean.setRegNumber(wifiBase.getRegistrationNo());
								} else {
									log.debug(Constants.ERROR_MESSAGE);
									responseBean.setStatus(Constants.ERROR_MESSAGE);
									responseBean.setRegNumber("");
								}
							}
						} else {
							status.setRegistrationNo(wifiBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_USER_TYPE);
							status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
							status.setRecipient(email);

							finalAuditTrack.setRegistrationNo(wifiBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
							finalAuditTrack.setToEmail(email);

							if (wifiService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info(Constants.SUBMITTED_SUCCESSFULLY, wifiBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
								responseBean.setRegNumber(wifiBase.getRegistrationNo());
							} else {
								log.debug(Constants.ERROR_MESSAGE);
								responseBean.setStatus(Constants.ERROR_MESSAGE);
								responseBean.setRegNumber("");
							}
						}
					} else {
						log.debug(Constants.ERROR_MESSAGE);
						responseBean.setStatus(Constants.ERROR_MESSAGE);
						responseBean.setRegNumber("");
					}

				} else {
					responseBean.setErrors(errorMap);
					responseBean.setRegNumber("");
					responseBean.setStatus("Application could not be submitted.");

				}

			} else {
				log.warn(
						"Hey, {},  We do not have your profile in eForms. Please go to profile section and make your profile first");
				responseBean.setStatus(
						"We do not have your profile in eForms. Please go to profile section and make your profile first");
				responseBean.setRegNumber("");
			}
		} else {
			responseBean.setErrors(errorMap);
			responseBean.setRegNumber("");
			responseBean.setStatus("Application could not be submitted.");
		}

		return responseBean;
	}

	private Map<String, Object> validateAction(String action, int macCount, int countUserWithThisMac) {
		Map<String, Object> errorMap = new HashMap<>();
		if (action.equalsIgnoreCase("request")) {
			if (macCount > 4) {
				log.debug(Constants.MAC_COUNT_EXCEED_MESSAGE);
				responseBean.setStatus(Constants.MAC_COUNT_EXCEED_MESSAGE);
				responseBean.setRegNumber("");
				errorMap.put("error", Constants.MAC_COUNT_EXCEED_MESSAGE);
			}
			if (countUserWithThisMac > 0) {
				log.debug(Constants.USER_COUNT_EXCEED_MESSAGE);
				responseBean.setStatus(Constants.USER_COUNT_EXCEED_MESSAGE);
				responseBean.setRegNumber("");
				errorMap.put("error", Constants.MAC_COUNT_EXCEED_MESSAGE);
			}
		}
		return errorMap;
	}

	@RequestMapping(value = "/validate-request")
	public Map<String, Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetail = new OrganizationDto();
		BeanUtils.copyProperties(previewFormBean, organizationDetail);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetail);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;

		if (organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
			log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse,
						ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			if (orgError != null) {
				map.put("orgError", orgError);
			}
		}
		
		if(!previewFormBean.getTnc()) {
			map.put("tncError", "You must accept Terms and Conditions to proceed.");
		}
		
		if (map.size() > 1) {
			finalmap.put("errors", map);
		}
		return finalmap;
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = "wifi";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		WifiBase wifiBase = wifiService.preview(regNumber);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, wifiBase.getMobile(),
				wifiBase.getName(), "user");
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");
		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");
		if (wifiService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} cancelled successfully.", regNumber);
			responseBean.setStatus(regNumber + " cancelled successfully");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug(Constants.ERROR_MESSAGE);
			responseBean.setStatus(Constants.ERROR_MESSAGE);
			responseBean.setRegNumber("");
		}

		return responseBean;
	}

	@RequestMapping(value = "/testuser")
	public String test() {
		return "ok";
	}
	//@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
	@RequestMapping(value = "/manualupload")
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request) throws IOException {
		
		manualUploadBean.setEmail(request.getParameter("email"));
		manualUploadBean.setClientIp(request.getParameter("clientIp"));
		
		responseBean.setRequestType("Forwarding of request by user");
		String formType = "wifi";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		WifiBase wifiBase = wifiService.preview(manualUploadBean.getRegNumber());
		
//		//fileadd
//		//update in base table the file in pdf_path column
		
		
		
		
		String[] contenttype = manualUploadBean.getFilepart().getContentType().split("/");
		String ext = contenttype[1];
		String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
				.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.append(".").append(ext).toString();
		
		wifiBase.setPdfPath(fileBasePath+outputfile);
		byte[] bytes = manualUploadBean.getFilepart().getBytes();
		Path path = Paths.get(fileBasePath + outputfile);
		boolean stat = wifiService.updatewifibase(wifiBase);
		if(stat) {
			
			Files.write(path, bytes);
		}
		else {
			
			responseBean.setStatus("File failed to upload");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
   		return responseBean;
		}
		
		
		
		
		
		
//		//EO fileadd
		String dn = utilityService.findDn(wifiBase.getEmail());
		String roDn = utilityService.findDn(manualUploadBean.getEmail());
		List<String> aliases = utilityService.aliases(manualUploadBean.getEmail());
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), wifiBase.getHodMobile(),
				wifiBase.getHodName(),"usermanual" ,manualUploadBean.getRegNumber());
		status = utilityService.initializeStatusTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), wifiBase.getHodMobile(), wifiBase.getHodName(),"user");
		
		if (wifiBase.getEmployment().equalsIgnoreCase("state")
				&& wifiBase.getPostingState().equalsIgnoreCase("Assam")) {
			toWhom = "Coordinator";
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		}else if (wifiBase.getEmployment().equalsIgnoreCase("State") && wifiBase.getState().equalsIgnoreCase("punjab")
				&& wifiBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "Coordinator";
			List<String> punjabCoords = utilityService.fetchPunjabCoords(wifiBase.getCity());
			daEmail = String.join(",", punjabCoords);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail()) && (wifiBase.getPostingState().equalsIgnoreCase("delhi")
				&& wifiBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(wifiBase.getEmail()) || utilityService.isNicEmployee(manualUploadBean.getEmail())) {
			toWhom = "Admin";
			daEmail = Constants.NKN_SUPPORT_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (dn!=null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "Wifi Support";
			daEmail = Constants.NKN_SUPPORT_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
		} else if (wifiBase.getEmployment().equalsIgnoreCase("Others")
				&& wifiBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& wifiBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& wifiBase.getCity().equalsIgnoreCase("pune") && (wifiBase.getAddress().toLowerCase().contains("ndc")
						|| wifiBase.getAddress().toLowerCase().contains("national data center"))) {
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
			OrganizationBean org = modelMapper.map(wifiBase, OrganizationBean.class);

//			Set<String> da = utilityService.fetchDAs(org);
//			Set<String> co = utilityService.fetchCoordinators(org);
//			if ((da != null && !da.isEmpty()) || (co != null && !co.isEmpty())) {
//				toWhom = "Coordinator";
//				daEmail = String.join(",", co);
//				recipientType = Constants.STATUS_COORDINATOR_TYPE;
//				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
//			} else {
				
				toWhom = "Support";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_SUPPORT_TYPE;
				nextStatus = Constants.STATUS_SUPPORT_PENDING;
		//	}
		}

		status.setRegistrationNo(manualUploadBean.getRegNumber());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (wifiService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
}
