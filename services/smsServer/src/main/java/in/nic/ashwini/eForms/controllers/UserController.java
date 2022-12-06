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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.SmsBase;
import in.nic.ashwini.eForms.exceptions.GlobalCheck;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.ManualUploadBean;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.DocsService;
import in.nic.ashwini.eForms.services.SmsService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	private final SmsService smsService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	
	@Autowired
	GlobalCheck check;
	@Autowired
	DocsService docsService;
	
	 @Value("${fileBasePath}")
	 private String fileBasePath;
	
	@Autowired
	public UserController(SmsService smsService, ResponseBean responseBean, Util utilityService) {
		super();
		this.smsService = smsService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {
		log.info("sms Submit request called by {}",email);
		System.out.println(previewFormBean.getSms_service());
		String formType = "sms";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
		responseBean.setRequestType("Submission of request");
		Map<String,Object> map = validateRequest(previewFormBean);
		if(map.size() == 0) {
			responseBean.setErrors(null);
		if (profile != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, previewFormBean.getRemarks(),
					profile.getMobile(), profile.getName(), "user");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType,previewFormBean.getRemarks(), profile.getMobile(), profile.getName(), "user", "");
				
			ModelMapper modelMapper = new ModelMapper();

			SmsBase smsBase = modelMapper.map(profile, SmsBase.class);
			smsBase.setPdfPath(submissionType);
			BeanUtils.copyProperties(previewFormBean, smsBase);
			List<String> sms_service = previewFormBean.getSms_service();
			
			StringBuilder sb=new StringBuilder();
			for(String ss:sms_service) {
				
				sb.append(ss);
			}
			smsBase.setSms_service(sb.toString());
			
			LocalDateTime currentTime = LocalDateTime.now();
			smsBase.setDatetime(currentTime);
			smsBase.setUserIp(ip);

			for (int i = 0; i < 4; i++) {
				smsBase = smsService.insert(smsBase);
				if (smsBase.getId() > 0) {
					break;
				}
			}
			if (smsBase.getId() > 0) {
				if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
					if (utilityService.isNicEmployee(email)) {
						status.setRegistrationNo(smsBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
						status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
						status.setRecipient(Constants.MAILADMIN_EMAIL);

						finalAuditTrack.setRegistrationNo(smsBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
						finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);

						if (smsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", smsBase.getRegistrationNo());
							responseBean.setStatus("Request submitted successfully and forwarded to Admin ("
									+ Constants.MAILADMIN_EMAIL + ")");
							responseBean.setRegNumber(smsBase.getRegistrationNo());
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					} else {
						status.setRegistrationNo(smsBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_CA_TYPE);
						status.setStatus(Constants.STATUS_CA_PENDING);
						status.setRecipient(profile.getHodEmail());

						finalAuditTrack.setRegistrationNo(smsBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
						finalAuditTrack.setToEmail(profile.getHodEmail());
						finalAuditTrack.setAppUserType(submissionType);

						if (smsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", smsBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
											+ profile.getHodEmail() + ")");
							responseBean.setRegNumber(smsBase.getRegistrationNo());
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					}
				} else {
					status.setRegistrationNo(smsBase.getRegistrationNo());
					status.setRecipientType(Constants.STATUS_USER_TYPE);
					status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					status.setRecipient(email);

					finalAuditTrack.setRegistrationNo(smsBase.getRegistrationNo());
					finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					finalAuditTrack.setToEmail(email);

					if (smsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
						log.info("{} submitted successfully.", smsBase.getRegistrationNo());
						responseBean.setStatus(
								"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
						responseBean.setRegNumber(smsBase.getRegistrationNo());
					} 
					else {
						log.debug("Something went wrong. Please try again after sometime.");
						responseBean.setStatus("Something went wrong. Please try again after sometime.");
						responseBean.setRegNumber("");
					}
				}
			} 
			else {
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

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam String remarks) {
		responseBean.setRequestType("Cancellation of request by user.");
		
		log.info("sms form reject by user" +email);
		String formType = "sms";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		SmsBase smsBase = smsService.preview(regNumber);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, smsBase.getMobile(),smsBase.getName(), "user");
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (smsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
	
	@PostMapping("/validate1")
	public Map<String,Object> validate1(@RequestParam("app_name") @NotEmpty String app_name ,@RequestParam("app_url") @NotEmpty String app_url, @RequestParam("server_loc") @NotEmpty String server_loc, @RequestParam("base_ip") @NotEmpty String base_ip) {
		Map<String,Object> error = new HashMap<>();
		log.info("validate the request");
		if (app_name.isEmpty()) {
			error.put("app_nameError:", "Please Enter App Name:");

		}
		if (app_url.isEmpty()) {
			error.put("error:", "Please Enter App Url:");

		}
		if (server_loc.isEmpty()) {
			error.put("error:", "Please Enter Server Loacation:");

		}
		if (base_ip.isEmpty()) {
			error.put("error:", "Please Enter Base Ip:");

		}
		return error;
	}

	@PostMapping("/validate2")
	public Map<String,Object> validate2(@RequestParam("t_off_name") @NotEmpty String t_off_name ,@RequestParam("tdesignation") @NotEmpty String tdesignation, @RequestParam("taddrs") @NotEmpty String taddrs, @RequestParam("tstate") @NotEmpty String tstate, @RequestParam("tcity") @NotEmpty String tcity , @RequestParam("tpin") @NotEmpty String tpin , @RequestParam("tmobile") @NotEmpty String tmobile , @RequestParam("tauth_email") @NotEmpty String tauth_email ) {
		Map<String,Object> error = new HashMap<>();
		log.info("validate the request");
		
		if (t_off_name.isEmpty()) {
			error.put("error:", "Please Enter Technical admin name:");

		}
		if (tdesignation.isEmpty()) {
			error.put("error:", "Please Enter Technical admin address:");

		}
		if (taddrs.isEmpty()) {
			error.put("error:", "Please Enter TCity Name:");

		}
		if (tstate.isEmpty()) {
			error.put("error:", "Please Enter Technical admin state:");

		}
		if (tcity.isEmpty()) {
			error.put("error:", "Please Enter Technical admin city:");     
 
		}
		if (tpin.isEmpty()) {
			error.put("error:", "Please Enter Technical admin pin:");

		}
		if (tmobile.isEmpty()) {
			error.put("error:", "Please Enter Technical admin mobile number:");

		}
		if (tauth_email.isEmpty()) {
			error.put("error:", "Please Enter Tauth_email:");

		}
		return error;
	}
	
	@PostMapping("/validate3")
	public Map<String,Object> validate3(@RequestParam("bauth_off_name") @NotEmpty String bauth_off_name ,@RequestParam("bdesignation") @NotEmpty String bdesignation, @RequestParam("baddrs") @NotEmpty String baddrs, @RequestParam("bstate") @NotEmpty String bstate, @RequestParam("bcity") @NotEmpty String bcity , @RequestParam("bpin") @NotEmpty String bpin , @RequestParam("bmobile") @NotEmpty String bmobile , @RequestParam("bauth_email") @NotEmpty String bauth_email ) {
		Map<String,Object> error = new HashMap<>();
		log.info("validate the request");
		
		if (bauth_off_name.isEmpty()) {
			error.put("error:", "Please Enter Biling Owner Name:");

		}
		if (bdesignation.isEmpty()) {
			error.put("error:", "Please Enter Biling Owner designation:");

		}
		if (baddrs.isEmpty()) {
			error.put("error:", "Please Enter Biling Owner address:");

		}
		if (bcity.isEmpty()) {
			error.put("error:", "Please Enter Biling Owner city:");

		}
		if (bstate.isEmpty()) {
			error.put("error:", "Please Enter Biling Owner state:");

		}if (bmobile.isEmpty()) {
			error.put("error:", "Please Enter Biling Owner mobile:");

		}
		if (bauth_email.isEmpty()) {
			error.put("error:", "Please Enter Biling Owner email:");
		}
		
		return error;
	}
	
	@PostMapping("/validate4")
	public Map<String,Object> validate4(@RequestParam("audit") @NotEmpty String audit ,@RequestParam("domestic_traf") @NotEmpty String domestic_traf) {
		Map<String,Object> error = new HashMap<>();
		log.info("validate the request");
		if (audit.isEmpty()) {
			error.put("error:", "Please Enter security audit:");

		}
		if (domestic_traf.isEmpty()) {
			error.put("error:", "Please Enter Domestic trafic:");
		}
		return error;
	}
	
	@RequestMapping(value = "/manualupload")
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request) throws IOException {
		
		manualUploadBean.setEmail(request.getParameter("email"));
		manualUploadBean.setClientIp(request.getParameter("clientIp"));
		
		responseBean.setRequestType("Forwarding of request by user");
		String formType = "sms";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		SmsBase smsBase = smsService.preview(manualUploadBean.getRegNumber());
		String[] contenttype = manualUploadBean.getInfile().getContentType().split("/");
		String ext = contenttype[1];
		String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
				.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.append(".").append(ext).toString();
		
		smsBase.setPdfPath(fileBasePath+outputfile);
		byte[] bytes = manualUploadBean.getInfile().getBytes();
		Path path = Paths.get(fileBasePath + outputfile);
		boolean stat = smsService.updatesmsbase(smsBase);
		if(stat) {
			
			Files.write(path, bytes);
		}
		else {
			responseBean.setStatus("File failed to upload");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
   		return responseBean;
		}
		String dn = utilityService.findDn(smsBase.getEmail());
		String roDn = utilityService.findDn(manualUploadBean.getEmail());
		List<String> aliases = utilityService.aliases(manualUploadBean.getEmail());
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), smsBase.getHodMobile(),
				smsBase.getHodName(),"usermanual" ,manualUploadBean.getRegNumber());
		status = utilityService.initializeStatusTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), smsBase.getHodMobile(), smsBase.getHodName(),"user");
		
		if (smsBase.getEmployment().equalsIgnoreCase("state")
				&& smsBase.getPostingState().equalsIgnoreCase("Assam")) {
			toWhom = "Coordinator";
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		}else if (smsBase.getEmployment().equalsIgnoreCase("State") && smsBase.getState().equalsIgnoreCase("punjab")
				&& smsBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "Coordinator";
			List<String> punjabCoords = utilityService.fetchPunjabCoords(smsBase.getCity());
			daEmail = String.join(",", punjabCoords);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail()) && (smsBase.getPostingState().equalsIgnoreCase("delhi")
				&& smsBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(smsBase.getEmail()) || utilityService.isNicEmployee(manualUploadBean.getEmail())) {
			toWhom = "ro";
             daEmail = smsBase.getHodEmail();
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;
				
		} else if (dn!=null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "ro";
			 daEmail = smsBase.getHodEmail();;  
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;

		} else if (smsBase.getEmployment().equalsIgnoreCase("Others")
				&& smsBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& smsBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& smsBase.getCity().equalsIgnoreCase("pune") && (smsBase.getAddress().toLowerCase().contains("ndc")
						|| smsBase.getAddress().toLowerCase().contains("national data center"))) {
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
			OrganizationBean org = modelMapper.map(smsBase, OrganizationBean.class);
			ProfileDto profile=null;
				toWhom = "Reporting officer";
				daEmail = smsBase.getHodEmail();
				recipientType = Constants.STATUS_CA_TYPE;
				nextStatus = Constants.STATUS_CA_PENDING;
		}
		status.setRegistrationNo(manualUploadBean.getRegNumber());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (smsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
