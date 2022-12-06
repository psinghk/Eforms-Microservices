package in.nic.ashwini.eForms.controllers;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.nic.ashwini.eForms.entities.DlistBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.ManualUploadBean;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.DlistBaseRepo;
import in.nic.ashwini.eForms.services.DlistService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final DlistService dlistService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	 @Value("${fileBasePath}")
	 private String fileBasePath;
	
    private  DlistBaseRepo dlistBaseRepo;
	@Autowired
	public ReportingOfficerController(DlistService dlistService, ResponseBean responseBean, Util utilityService) {
		super();
		this.dlistService = dlistService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam String remarks) {
		responseBean.setRequestType("Approval of request by Reporting Officer");
		log.info("Dlist Form approve by {}",email);

		String formType = "dlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		DlistBase dlistBase = dlistService.preview(regNumber);
		String dn = utilityService.findDn(dlistBase.getEmail());
		String roDn = utilityService.findDn(email);
		List<String> aliases = utilityService.aliases(email);
		
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, dlistBase.getHodMobile(),
				dlistBase.getHodName(), "ro");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
				dlistBase.getHodMobile(), dlistBase.getHodName(), "ro", regNumber);
	
		if (utilityService.isNicEmployee(email) && (dlistBase.getPostingState().equalsIgnoreCase("delhi")
				&& dlistBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork"))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "Support";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
			
		} else if(dlistBase.getEmployment().equalsIgnoreCase("Others") 
				&& dlistBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& dlistBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& dlistBase.getCity().equalsIgnoreCase("pune")
				&& (dlistBase.getAddress().toLowerCase().contains("ndc")
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
		}
		
		else if(dlistBase.getEmployment().equalsIgnoreCase("nkn")&&dlistBase.getOrganization().equalsIgnoreCase("Tamil Nadu Veterinary and Animal Sciences University")) {
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(dlistBase, OrganizationBean.class);
			//Set<String> co = utilityService.fetchCoordinators(org);
			toWhom = "Coordinator";
			daEmail="s.gopinath@gov.in";//
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			
		}
		else if(dlistBase.getEmployment().equalsIgnoreCase("State")
				&& dlistBase.getState().equalsIgnoreCase("Himachal Pradesh")) {
			daEmail = utilityService.fetchHimachalDa();
			toWhom = "Delegated Admin";
			recipientType = Constants.STATUS_DA_TYPE;
			nextStatus = Constants.STATUS_DA_PENDING;
		} 
		else {
			
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(dlistBase, OrganizationBean.class);

			Set<String> co = utilityService.fetchCoordinators(org);

				if(co != null && co.size() > 0) {
				toWhom = "Coordinator";
				daEmail = String.join(",", co);
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			} else {
				toWhom = "Admin";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			}
		}
		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);
	//	finalAuditTrack.setAppCaType("");    // need to add submision type
		
		if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom +"("+ daEmail + ")");
			responseBean.setStatus(
					"Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom +"("+ daEmail + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam("remarks") @NotEmpty String remarks) {
		responseBean.setRequestType("Rejection of request by RO.");
		log.info("Dlist Form reject by {}",email);
		String formType = "dlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		DlistBase dlistBase = dlistService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, dlistBase.getHodMobile(), dlistBase.getHodName(), "ro");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, dlistBase.getHodMobile(), dlistBase.getHodName(), "ro", regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_CA_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_CA_REJECTED);
		finalAuditTrack.setToEmail("");

		if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} rejected successfully.", regNumber);
			responseBean.setStatus(regNumber + " rejected successfully");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber("");
		}

		return responseBean;
	}
	
	 public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request, ResponseBean responseBean) throws IOException {
			
			manualUploadBean.setEmail(request.getParameter("email"));
			manualUploadBean.setClientIp(request.getParameter("clientIp"));
			
			responseBean.setRequestType("approving the request by ro");
			log.info("EmailAct manual upload request");
			String formType = "emailactivate";
			Status status = null;
			FinalAuditTrack finalAuditTrack = null;
			DlistBase dlistBase = preview(manualUploadBean.getRegNumber());
			String[] contenttype = manualUploadBean.getInfile().getContentType().split("/");
			String ext = contenttype[1];
			String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
					.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
					.append(".").append(ext).toString();
			
			dlistBase.setPdfPath(fileBasePath+outputfile);
			byte[] bytes = manualUploadBean.getInfile().getBytes();
			Path path = Paths.get(fileBasePath + outputfile);
			boolean stat = dlistBase(dlistBase);
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
	        	 toWhom = "Support";
	 			daEmail = Constants.SUPPORT_EMAIL;
	 			recipientType = Constants.STATUS_SUPPORT_TYPE;
	 			nextStatus = Constants.STATUS_SUPPORT_PENDING;
			} 
	         if (dlistBase.getEmployment().equalsIgnoreCase("State") && dlistBase.getState().equalsIgnoreCase("Himanchal Pradesh")
	 				|| dlistBase.getPostingState().equalsIgnoreCase("Himachal")) {
	         	 toWhom = "Support";
	  			daEmail = Constants.SUPPORT_EMAIL;
	  			recipientType = Constants.STATUS_SUPPORT_TYPE;
	  			nextStatus = Constants.STATUS_SUPPORT_PENDING;
	 		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail()) && (dlistBase.getPostingState().equalsIgnoreCase("delhi")
					&& dlistBase.getEmployment().equalsIgnoreCase("central")
					&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
				toWhom = "Admin";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else if (utilityService.isNicEmployee(dlistBase.getEmail()) || utilityService.isNicEmployee(manualUploadBean.getEmail())) {
				toWhom = "Admin";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				
			} else if (dn!=null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
					&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
				toWhom = "Admin";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				
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
				 toWhom = "Support";
		 			daEmail = Constants.SUPPORT_EMAIL;
		 			recipientType = Constants.STATUS_SUPPORT_TYPE;
		 			nextStatus = Constants.STATUS_SUPPORT_PENDING;
			}
			status.setRegistrationNo(manualUploadBean.getRegNumber());
			status.setRecipientType(recipientType);
			status.setStatus(nextStatus);
			status.setRecipient(daEmail);

			finalAuditTrack.setStatus(nextStatus);
			finalAuditTrack.setToEmail(daEmail);

			if (utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
	   public boolean dlistBase(DlistBase dlistBase) {
		   DlistBase details = dlistBaseRepo.save(dlistBase);
			if (details.getId() > 0) {
				return true;
			} else {
				return false;
			}
		}
		public DlistBase preview(String regNo) {
			return dlistBaseRepo.findByRegistrationNo(regNo);
		}
}
