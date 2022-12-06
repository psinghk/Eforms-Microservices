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
import java.util.Optional;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.nic.ashwini.eForms.entities.DlistBase;
import in.nic.ashwini.eForms.entities.ModeratorBase;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.ManualUploadBean;
import in.nic.ashwini.eForms.models.Moderators;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.OwnerModeratorBean;
import in.nic.ashwini.eForms.models.Owners;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.DlistService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	
	private final DlistService dlistService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	
	 @Value("${fileBasePath}")
	 private String fileBasePath;
	
	@Autowired
	public UserController(DlistService dlistService, ResponseBean responseBean, Util utilityService) {
		super();
		this.dlistService = dlistService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {

		List<Moderators> listmoderators = previewFormBean.getModerators();
		List<Owners> listowners = previewFormBean.getOwners();
		
		String formType = "dlist";
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
					profile.getName(), "user");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, previewFormBean.getRemarks(),
					profile.getMobile(), profile.getName(), "user", "");

			ModelMapper modelMapper = new ModelMapper();
			
			profile.getEmail();
			System.out.println(" email of user"+profile.getEmail());
			DlistBase dlistBase = modelMapper.map(profile, DlistBase.class);
			dlistBase.getEmail();
			System.out.println(" email of user--"+dlistBase.getEmail());
			dlistBase.setPdfPath(submissionType);
			BeanUtils.copyProperties(previewFormBean, dlistBase);
			LocalDateTime currentTime = LocalDateTime.now();
			dlistBase.setDatetime(currentTime);
			dlistBase.setUserIp(ip);

			for (int i = 0; i < 4; i++) {
				dlistBase = dlistService.insert(dlistBase,listowners,listmoderators);
				if (dlistBase.getId() > 0) {
					break;
				}
			}
			if (dlistBase.getId() > 0) {
				
				if (submissionType.equalsIgnoreCase("online") || submissionType.equalsIgnoreCase("esign")) {
					if (utilityService.isNicEmployee(email)) {
						status.setRegistrationNo(dlistBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_ADMIN_TYPE);
						status.setStatus(Constants.STATUS_MAILADMIN_PENDING);
						status.setRecipient(Constants.MAILADMIN_EMAIL);

						finalAuditTrack.setRegistrationNo(dlistBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_MAILADMIN_PENDING);
						finalAuditTrack.setToEmail(Constants.MAILADMIN_EMAIL);

						if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", dlistBase.getRegistrationNo());
							responseBean.setStatus("Request submitted successfully and forwarded to Admin ("
									+ Constants.MAILADMIN_EMAIL + ")");
							responseBean.setRegNumber(dlistBase.getRegistrationNo());
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					} else {
						status.setRegistrationNo(dlistBase.getRegistrationNo());
						status.setRecipientType(Constants.STATUS_CA_TYPE);
						status.setStatus(Constants.STATUS_CA_PENDING);
						status.setRecipient(profile.getHodEmail());

						finalAuditTrack.setRegistrationNo(dlistBase.getRegistrationNo());
						finalAuditTrack.setStatus(Constants.STATUS_CA_PENDING);
						finalAuditTrack.setToEmail(profile.getHodEmail());

						if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
							log.info("{} submitted successfully.", dlistBase.getRegistrationNo());
							responseBean.setStatus(
									"Request submitted successfully and forwarded to Reporting/Nodal/Forwarding Officer ("
											+ profile.getHodEmail() + ")");
							responseBean.setRegNumber(dlistBase.getRegistrationNo());
						} else {
							log.debug("Something went wrong. Please try again after sometime.");
							responseBean.setStatus("Something went wrong. Please try again after sometime.");
							responseBean.setRegNumber("");
						}
					}
				} else {
					status.setRegistrationNo(dlistBase.getRegistrationNo());
					status.setRecipientType(Constants.STATUS_USER_TYPE);
					status.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					status.setRecipient(email);

					finalAuditTrack.setRegistrationNo(dlistBase.getRegistrationNo());
					finalAuditTrack.setStatus(Constants.STATUS_MANUAL_UPLOAD);
					finalAuditTrack.setToEmail(email);
					finalAuditTrack.setAppUserType(submissionType);

					if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
						log.info("{} submitted successfully.", dlistBase.getRegistrationNo());
						responseBean.setStatus(
								"Request submitted successfully but it is pending with you only as you selected manual upload option to submit the request.");
						responseBean.setRegNumber(dlistBase.getRegistrationNo());
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
		String formType = "dlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		DlistBase dlistBase = dlistService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, dlistBase.getMobile(), dlistBase.getName(), "user");
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_USER_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_USER_REJECTED);
		finalAuditTrack.setToEmail("");

		if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
	
	@RequestMapping("/validate")
	public Map<String,Object> validate(@RequestParam("list_name") @NotEmpty String list_name , @RequestParam("description_list") @NotEmpty String description_list ,@RequestParam("memberCount") @NotEmpty String memberCount, @RequestParam("list_mod") @NotEmpty String list_mod, @RequestParam("allowed_member") @NotEmpty String allowed_member,  @RequestParam("non_nicnet") @NotEmpty String non_nicnet,  @RequestParam("list_temp") @NotEmpty String list_temp) {
		Map<String,Object> error = new HashMap<>();
		
		if(list_name.isEmpty()) {
			error.put("List name cant be null", "Please enter list name");
			return error;
		}
		
		if(description_list.isEmpty()) {
			error.put("description_list cant be null", "please enter list description");
			return error;
		}
		if(memberCount.isEmpty()) {
			error.put("memberCount cant be null", "Please enter member count");
		}
		if(list_mod.isEmpty()) {
			error.put("list_mod cant be null", "Please enter list mod");
		}
		if(allowed_member==null) {
			error.put("allowed_member cant be null", "Please enter allowed member");
		}
		if(non_nicnet.isEmpty()) {
			error.put("non_nicnet cant be null", "Please enter mail acceptance");
		}
		if(list_temp.isEmpty()) {
			error.put("list_temp cant be null", "Please enter List temp");
		}
		return error;
	}
	
	@RequestMapping(value = "/singleOwnerDataFetch")
	public Optional<ModeratorBase> singleOwnerDataFetch(@RequestParam("id") Long id) {
		
		Optional<ModeratorBase> featchedData = dlistService.singleOwnerDataFetch(id);
		return featchedData;
	}
	
	@RequestMapping(value = "/singleModeratorDataFetch")
	public Optional<ModeratorBase> singleModeratorDataFetch(@RequestParam("id") Long id) {
		
		Optional<ModeratorBase> featchedData = dlistService.singleOwnerDataFetch(id);		
		return featchedData;
	}
	
	@RequestMapping(value = "/singleOwnerDataEditPost")
	public Optional<ModeratorBase> singleOwnerDataEditPost(@ModelAttribute ModeratorBase moderatorBase, @RequestParam("id") Long id) {
		
		 Optional<ModeratorBase> featchedData = dlistService.singleOwnerDataEditPost(id,moderatorBase);	
		 System.out.println("Upadated data"+featchedData);
		 return featchedData;
	}
	
	@RequestMapping(value = "/singleModeratorDataEditPost")
	public Optional<ModeratorBase> singleModeratorDataEditPost(@ModelAttribute ModeratorBase moderatorBase, @RequestParam("id") Long id) {
		
		 Optional<ModeratorBase> featchedData = dlistService.singleOwnerDataEditPost(id,moderatorBase);	
		 System.out.println("Upadated data"+featchedData);
		 return featchedData;
	}
	
	@PostMapping("/singleOwnerDataDeletePost")
	public String singleOwnerDataDeletePost(@RequestParam("id") Long id) {
	      
		dlistService.dlistDataDelete(id);
		return "record deleted of id: "+id;
	}
	
	@PostMapping("/singleModeratorDataDeletePost")
	public String singleModeratorDataDeletePost(@RequestParam("id") Long id) {
	      
		dlistService.dlistDataDelete(id);
		return "record deleted of id: "+id;
	}
	
	@PostMapping("/validate2")
	public Map<String,Object> validateStep2(@Valid @RequestBody OwnerModeratorBean previewFormBean) {
		Map<String,Object> error = new HashMap<>();
		if (previewFormBean.getOwners() == null && previewFormBean.getModerators() == null) {

			error.put("error:", "Please Enter atleast one record");
		}
		else {
			if (previewFormBean.getOwners() != null) {

				for (int i = 0; i < previewFormBean.getOwners().size(); i++) {
					if (previewFormBean.getOwners().get(i).getOwner_email() == null) {
						error.put("error:" + (i + 1), "Please Enter email of owner");
					}
					if (previewFormBean.getOwners().get(i).getOwner_mobile() == null) {
						error.put("error:" + (i + 1), "Please Enter mobile of owner");
					}
					if (previewFormBean.getOwners().get(i).getOwner_name() == null) {
						error.put("error:" + (i + 1), "Please Enter name of owner");
					}
				}
			}

			if (previewFormBean.getModerators() != null) {

				for (int i = 0; i < previewFormBean.getModerators().size(); i++) {
					if (previewFormBean.getModerators().get(i).getTauth_email() == null) {
						error.put("error:" + (i + 1), "Please Enter email of moderator");
					}
					if (previewFormBean.getModerators().get(i).getTmobile() == null) {
						error.put("error:" + (i + 1), "Please Enter mobile of moderator");
					}
					if (previewFormBean.getModerators().get(i).getT_off_name() == null) {
						error.put("error:" + (i + 1), "Please Enter name of moderator");
					}
				}
			}
		}
		return error;
	}

	@RequestMapping(value = "/manualupload")
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request) throws IOException {
		
		manualUploadBean.setEmail(request.getParameter("email"));
		manualUploadBean.setClientIp(request.getParameter("clientIp"));
		
		responseBean.setRequestType("Forwarding of request by user");
		String formType = "dlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		DlistBase dlistBase = dlistService.preview(manualUploadBean.getRegNumber());
		String[] contenttype = manualUploadBean.getInfile().getContentType().split("/");
		String ext = contenttype[1];
		String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
				.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.append(".").append(ext).toString();
		
		dlistBase.setPdfPath(fileBasePath+outputfile);
		byte[] bytes = manualUploadBean.getInfile().getBytes();
		Path path = Paths.get(fileBasePath + outputfile);
		boolean stat = dlistService.updatedlistbase(dlistBase);
		if(stat) {
			
			Files.write(path, bytes);
		}
		else {
			responseBean.setStatus("File failed to upload");
			responseBean.setRegNumber(manualUploadBean.getRegNumber());
   		return responseBean;
		}
		String dn = utilityService.findDn(dlistBase.getEmail());
		String roDn = utilityService.findDn(manualUploadBean.getEmail());
		List<String> aliases = utilityService.aliases(manualUploadBean.getEmail());
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), dlistBase.getHodMobile(),
				dlistBase.getHodName(),"usermanual" ,manualUploadBean.getRegNumber());
		status = utilityService.initializeStatusTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), dlistBase.getHodMobile(), dlistBase.getHodName(),"user");
		
		 if (dlistBase.getEmployment().equalsIgnoreCase("State") && dlistBase.getState().equalsIgnoreCase("punjab")
				&& dlistBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "Coordinator";
			List<String> punjabCoords = utilityService.fetchPunjabCoords(dlistBase.getCity());
			daEmail = String.join(",", punjabCoords);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail()) && (dlistBase.getPostingState().equalsIgnoreCase("delhi")
				&& dlistBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(dlistBase.getEmail()) || utilityService.isNicEmployee(manualUploadBean.getEmail())) {
			toWhom = "ro";
			daEmail = dlistBase.getHodEmail();
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;

		} else if (dn!=null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "ro";
			daEmail = dlistBase.getHodEmail(); 
			recipientType = Constants.STATUS_CA_TYPE;
			nextStatus = Constants.STATUS_CA_PENDING;

		} else if (dlistBase.getEmployment().equalsIgnoreCase("Others")
				&& dlistBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& dlistBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& dlistBase.getCity().equalsIgnoreCase("pune") && (dlistBase.getAddress().toLowerCase().contains("ndc")
						|| dlistBase.getAddress().toLowerCase().contains("national data center"))) {
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
			OrganizationBean org = modelMapper.map(dlistBase, OrganizationBean.class);
				toWhom = "Reporting officer";
				daEmail = dlistBase.getHodEmail();
				recipientType = Constants.STATUS_CA_TYPE;
				nextStatus = Constants.STATUS_CA_PENDING;
		}
		status.setRegistrationNo(manualUploadBean.getRegNumber());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);
		finalAuditTrack.setAppUserType("manual");

		if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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