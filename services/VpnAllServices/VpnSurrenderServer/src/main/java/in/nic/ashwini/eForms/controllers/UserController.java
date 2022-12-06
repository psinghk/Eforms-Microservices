package in.nic.ashwini.eForms.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.VpnBase;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.ManualUploadBean;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.VpnEntryBaseRepo;
import in.nic.ashwini.eForms.services.VpnPushApi;
import in.nic.ashwini.eForms.services.VpnService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/user")
@RestController
public class UserController {

	private final VpnService vpnService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	VpnPushApi vpnPushApi = new VpnPushApi();
	@Value("${fileBasePath}")
	private String fileBasePath;

	@Autowired
	public UserController(VpnService vpnService, ResponseBean responseBean, Util utilityService,
			VpnEntryBaseRepo vpnEntryBaseRepo) {
		super();
		this.vpnService = vpnService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/GetVpnNumber")
	public List<Object> getVpnNumber(String email) {
		//ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		List<Object> vpn_number = new ArrayList<>();
		try {
			String inputLine = "[{\"0\":{\"ipaddress\":\"10.26.64.41\",\"access_details\":[{\"serip\":\"10.1.16.101\",\"serloc\":\"NIC CGO\",\"destport\":\"80,443,22,389,636\",\"desc_service\":\"\"},{\"serip\":\"10.1.16.87\",\"serloc\":\"NIC CGO\",\"destport\":\"80,443,22,389,636\",\"desc_service\":\"\"},{\"serip\":\"164.100.14.82\",\"serloc\":\"delhi\",\"destport\":\"22\",\"desc_service\":\"\"},{\"serip\":\"10.103.15.178\",\"serloc\":\"NDC Delhi\",\"destport\":\"22\",\"desc_service\":\"\"},{\"serip\":\"10.120.43.66\",\"serloc\":\"NDC Delhi\",\"destport\":\"80,443\",\"desc_service\":\"\"},{\"serip\":\"10.120.43.66\",\"serloc\":\"NDC Delhi\",\"destport\":\"22\",\"desc_service\":\"\"},{\"serip\":\"10.1.146.228\",\"serloc\":\"NDC Delhi\",\"destport\":\"80,443\",\"desc_service\":\"\"},{\"serip\":\"1.2.3.3\",\"serloc\":\"NDC Delhi\",\"destport\":\"8080\",\"desc_service\":\"\"},{\"serip\":\"10.122.34.101\",\"serloc\":\"NDC Delhi\",\"destport\":\"22,80,389,443,636\",\"desc_service\":\"\"}],\"name\":\"Meenaxi Indolia\",\"message\":\"Data Available\",\"email\":\"meenaxi.nhq@nic.in\",\"vpn_registration_no\":\"VPN328069\",\"expdate\":\"2022-04-01\"},\"success\":true,\"message\":\"Record Found\"}]";
			vpn_number.add(inputLine);
			// vpn_number = vpnService.getVpnNumbers(email, profile.getMobile());

			System.out.println("vpn details:::: = :: ->" + vpn_number);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return vpn_number;
	}

	@RequestMapping(value = "/getVpnNumberDetails")
	public Map<String, String> getVpnDetails(String vpnNumber, String email) {
		Map<String, String> response = new HashMap<>();
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);

		try {
		/*
			String vpnDetails = vpnService.getVpnDetails(vpnNumber, email, profile.getMobile());
			System.out.println("vnpDetails::::::: -> "+vpnDetails);
			
			if (!vpnDetails.isEmpty()) {
				response.put("flag", "success");
				response.put("vpnNumber", vpnDetails);
			} else {
				response.put("flag", "error");
				response.put("error", "NO VPN NUMBER FOUND");
			}
			*/
			String vpnDetails = "{\"access_details\":[{\"serip\":\"10.1.16.101\",\"serloc\":\"NIC CGO\",\"destport\":\"80,443,22,389,636\",\"desc_service\":\"\"},{\"serip\":\"10.1.16.87\",\"serloc\":\"NIC CGO\",\"destport\":\"80,443,22,389,636\",\"desc_service\":\"\"},{\"serip\":\"164.100.14.82\",\"serloc\":\"delhi\",\"destport\":\"22\",\"desc_service\":\"\"},{\"serip\":\"10.103.15.178\",\"serloc\":\"NDC Delhi\",\"destport\":\"22\",\"desc_service\":\"\"},{\"serip\":\"10.120.43.66\",\"serloc\":\"NDC Delhi\",\"destport\":\"80,443\",\"desc_service\":\"\"},{\"serip\":\"10.120.43.66\",\"serloc\":\"NDC Delhi\",\"destport\":\"22\",\"desc_service\":\"\"},{\"serip\":\"10.1.146.228\",\"serloc\":\"NDC Delhi\",\"destport\":\"80,443\",\"desc_service\":\"\"},{\"serip\":\"1.2.3.3\",\"serloc\":\"NDC Delhi\",\"destport\":\"8080\",\"desc_service\":\"\"},{\"serip\":\"10.122.34.101\",\"serloc\":\"NDC Delhi\",\"destport\":\"22,80,389,443,636\",\"desc_service\":\"\"}],\"success\":true,\"name\":\"Meenaxi Indolia\",\"message\":\"Data Available\",\"email\":\"meenaxi.nhq@nic.in\",\"vpn_registration_no\":\"VPN328069\",\"expdate\":\"2022-04-01\"}";
			response.put("flag", "success");
			response.put("vpnNumber", vpnDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}


	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {
		String formType = Constants.VPN_SURRENDER_FORM_KEYWORD;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		String uid = utilityService.fetchUid(email);
		responseBean.setRequestType("Submission of request");
		boolean isHod = false;
		boolean isHog = false;
		isHod = utilityService.isHod(uid);
		isHog = utilityService.isHog(uid);
		Map<String, Object> map = validateRequest(previewFormBean);
		if (map.size() == 0) {
			responseBean.setErrors(null);
			if (profile != null) {
				status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(),
						profile.getMobile(), profile.getName(), "user");
				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType,
						previewFormBean.getRemarks(), profile.getMobile(), profile.getName(), "user", "");
				ModelMapper modelMapper = new ModelMapper();

				VpnBase vpnBase = modelMapper.map(profile, VpnBase.class);
				vpnBase.setPdfPath(submissionType);
				BeanUtils.copyProperties(previewFormBean, vpnBase);
				LocalDateTime currentTime = LocalDateTime.now();
				vpnBase.setDatetime(currentTime);
				vpnBase.setUserIp(ip);
				vpnBase = vpnService.insert(vpnBase);

				if (vpnBase.getId() > 0) {
					if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
						if (isHod || isHog) {
							status.setRegistrationNo(vpnBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
							status.setStatus(Constants.MAIL_ADMIN_PENDING);
							status.setRecipient(Constants.VPN_MAILADMIN_EMAIL);
							finalAuditTrack.setRegistrationNo(vpnBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.MAIL_ADMIN_PENDING);
							finalAuditTrack.setToEmail(Constants.VPN_MAILADMIN_EMAIL);

							// for RabittMQ
							vpnPushApi.callVpnWebService();

							if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", vpnBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
												+ Constants.VPN_MAILADMIN_EMAIL + ")");
								responseBean.setRegNumber(vpnBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}

						} else {
							if (previewFormBean.getMinistry() != null) {
								if (previewFormBean.getMinistry().equalsIgnoreCase("Railways,Railnet")) {
									profile.setHodEmail("dtele@rb.railnet.gov.in");
								}
							}
							status.setRegistrationNo(vpnBase.getRegistrationNo());
							status.setRecipientType(Constants.STATUS_CA_TYPE);
							status.setStatus(Constants.STATUS_CA_PENDING);
							status.setRecipient(profile.getHodEmail());

							finalAuditTrack.setRegistrationNo(vpnBase.getRegistrationNo());
							finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
							finalAuditTrack.setToEmail(profile.getHodEmail());

							if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
								log.info("{} submitted successfully.", vpnBase.getRegistrationNo());
								responseBean.setStatus(
										"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
												+ profile.getHodEmail() + ")");
								responseBean.setRegNumber(vpnBase.getRegistrationNo());
							} else {
								log.debug("Something went wrong. Please try again after sometime.");
								responseBean.setStatus("Something went wrong. Please try again after sometime.");
								responseBean.setRegNumber("");
							}
						}
					} else {
						status.setRegistrationNo(vpnBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_USER_TYPE);
						status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						status.setRecipient(email);

						finalAuditTrack.setRegistrationNo(vpnBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
						finalAuditTrack.setToEmail(email);

						if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", vpnBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
							responseBean.setRegNumber(vpnBase.getRegistrationNo());
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

	@RequestMapping(value = "/validateRequest")
	public Map<String, Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();

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

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		String formType = Constants.VPN_SURRENDER_FORM_KEYWORD;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		VpnBase vpnBase = vpnService.preview(regNumber);
		String uid = utilityService.fetchUid(email);
		boolean isHod = utilityService.isHod(uid);
		boolean isHog = utilityService.isHog(uid);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, vpnBase.getMobile(),
				vpnBase.getName(), "user");
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);

		if (isHod || isHog) {
			status.setRegistrationNo(vpnBase.getRegistrationNo());
			status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
			status.setStatus(Constants.MAIL_ADMIN_PENDING);
			status.setRecipient(Constants.VPN_MAILADMIN_EMAIL);
			finalAuditTrack.setRegistrationNo(vpnBase.getRegistrationNo());
			finalAuditTrack.setStatus(Constants.MAIL_ADMIN_PENDING);
			finalAuditTrack.setToEmail(Constants.VPN_MAILADMIN_EMAIL);
			// for RabittMQ
			vpnPushApi.callVpnWebService();

			if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", vpnBase.getRegistrationNo());
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
								+ Constants.VPN_MAILADMIN_EMAIL + ")");
				responseBean.setRegNumber(vpnBase.getRegistrationNo());
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber("");
			}

		}

		else {
			if (profile.getMinistry() != null) {
				if (profile.getMinistry().equalsIgnoreCase("Railways,Railnet")) {
					profile.setHodEmail("dtele@rb.railnet.gov.in");
				}
			}
			status.setRegistrationNo(vpnBase.getRegistrationNo());
			status.setRecipientType(Constants.STATUS_CA_TYPE);
			status.setStatus(Constants.STATUS_CA_PENDING);
			status.setRecipient(profile.getHodEmail());

			finalAuditTrack.setRegistrationNo(vpnBase.getRegistrationNo());
			finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
			finalAuditTrack.setToEmail(profile.getHodEmail());

			if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("{} submitted successfully.", vpnBase.getRegistrationNo());
				responseBean.setStatus(
						"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
								+ profile.getHodEmail() + ")");
				responseBean.setRegNumber(vpnBase.getRegistrationNo());
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
			@RequestParam("email") @NotEmpty String email, String remarks) {
		responseBean.setRequestType("Cancellation of request by user.");
		String formType = Constants.VPN_SURRENDER_FORM_KEYWORD;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		VpnBase vpnBase = vpnService.preview(regNumber);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, vpnBase.getMobile(),
				vpnBase.getName(), "user");

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	
	@RequestMapping(value = "/manualupload")
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,
			HttpServletRequest request) throws IOException {

		manualUploadBean.setEmail(request.getParameter("email"));
		manualUploadBean.setClientIp(request.getParameter("clientIp"));

		responseBean.setRequestType("Forwarding of request by user");
		String formType = "sms";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		// ProfileDto profile =
		// utilityService.fetchProfileByEmailInBean(manualUploadBean.getEmail());
		// WifiBase wifiBase = wifiService.preview(manualUploadBean.getRegNumber());
		VpnBase vpnBase = vpnService.preview(manualUploadBean.getRegNumber());
		// ProfileDto profile;

//		//fileadd
//		//update in base table the file in pdf_path column
		String[] contenttype = manualUploadBean.getInfile().getContentType().split("/");
		String ext = contenttype[1];
		String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
				.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).append(".")
				.append(ext).toString();

		vpnBase.setPdfPath(fileBasePath + outputfile);
		byte[] bytes = manualUploadBean.getInfile().getBytes();
		Path path = Paths.get(fileBasePath + outputfile);
		boolean stat = vpnService.updatevpnbase(vpnBase);
		if (stat) {

			Files.write(path, bytes);
		} else {

			responseBean.setStatus("File failed to upload");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
			return responseBean;
		}
//		//EO fileadd
		String dn = utilityService.findDn(vpnBase.getEmail());
		String roDn = utilityService.findDn(manualUploadBean.getEmail());
		List<String> aliases = utilityService.aliases(manualUploadBean.getEmail());
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(manualUploadBean.getClientIp(),
				manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), vpnBase.getHodMobile(),
				vpnBase.getHodName(), "usermanual", manualUploadBean.getRegNumber());
		status = utilityService.initializeStatusTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(),
				formType, manualUploadBean.getRemarks(), vpnBase.getHodMobile(), vpnBase.getHodName(), "user");

		if (vpnBase.getEmployment().equalsIgnoreCase("state") && vpnBase.getPostingState().equalsIgnoreCase("Assam")) {
			toWhom = "Coordinator";
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if (vpnBase.getEmployment().equalsIgnoreCase("State") && vpnBase.getState().equalsIgnoreCase("punjab")
				&& vpnBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "Coordinator";
			List<String> punjabCoords = utilityService.fetchPunjabCoords(vpnBase.getCity());
			daEmail = String.join(",", punjabCoords);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail())
				&& (vpnBase.getPostingState().equalsIgnoreCase("delhi")
						&& vpnBase.getEmployment().equalsIgnoreCase("central")
						&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.VPN_MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(vpnBase.getEmail())
				|| utilityService.isNicEmployee(manualUploadBean.getEmail())) {
			toWhom = "ro";
			// daEmail = Constants.NKN_SUPPORT_EMAIL;
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;

		} else if (dn != null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "ro";
			// daEmail = Constants.NKN_SUPPORT_EMAIL; // temprary commented by rahul
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;

		} else if (vpnBase.getEmployment().equalsIgnoreCase("Others")
				&& vpnBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& vpnBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& vpnBase.getCity().equalsIgnoreCase("pune") && (vpnBase.getAddress().toLowerCase().contains("ndc")
						|| vpnBase.getAddress().toLowerCase().contains("national data center"))) {
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();

			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
		} else {

			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(vpnBase, OrganizationBean.class);
			ProfileDto profile = null;
			toWhom = "Reporting officer";
			daEmail = vpnBase.getHodEmail();
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;
		}
		status.setRegistrationNo(manualUploadBean.getRegNumber());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + manualUploadBean.getRegNumber() + ")  Forwarded Successfully to " + toWhom + "("
					+ daEmail + ")");
			responseBean.setStatus("Application (" + manualUploadBean.getRegNumber() + ") Forwarded Successfully to "
					+ toWhom + "(" + daEmail + ")");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
		} else {
			log.debug(Constants.ERROR_MESSAGE);
			responseBean.setStatus(Constants.ERROR_MESSAGE);
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
		}
		return responseBean;
	}	
}
