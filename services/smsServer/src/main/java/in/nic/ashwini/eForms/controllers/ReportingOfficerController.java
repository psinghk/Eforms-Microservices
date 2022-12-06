package in.nic.ashwini.eForms.controllers;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.nic.ashwini.eForms.entities.SmsBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.SmsService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final SmsService smsService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public ReportingOfficerController(SmsService smsService, ResponseBean responseBean, Util utilityService) {
		super();
		this.smsService = smsService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam String remarks) {
		responseBean.setRequestType("Approval of request by Reporting Officer");
		log.info("sms form Approve by {}",email);
		String formType = "sms";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		SmsBase smsBase = smsService.preview(regNumber);

		String dn = utilityService.findDn(smsBase.getTauth_email());
		String roDn = utilityService.findDn(email);
		List<String> aliases = utilityService.aliases(email);
		
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, smsBase.getHodMobile(),
				smsBase.getHodName(),"ro" ,regNumber);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, smsBase.getHodMobile(), smsBase.getHodName(),"ro");
		
		 if (utilityService.isNicEmployee(email) && (smsBase.getPostingState().equalsIgnoreCase("delhi")
				&& smsBase.getEmployment().equalsIgnoreCase("central")
				&& utilityService.isSupportOrOutsourced(smsBase.getEmail()))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isSupportOrOutsourced(smsBase.getEmail()) && utilityService.isSupportOrOutsourced(email)) {
			toWhom = "Support";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
		} else if(smsBase.getEmployment().equalsIgnoreCase("Others") 
				&& smsBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& smsBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& smsBase.getCity().equalsIgnoreCase("pune")
				&& (smsBase.getAddress().toLowerCase().contains("ndc")
						|| smsBase.getAddress().toLowerCase().contains("national data center"))) {
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();
			
			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				daEmail = Constants.SMS_MAILADMIN_EMAIL;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
		} 
		else {

		}  if(smsBase.getEmployment().equalsIgnoreCase("State")
				&& smsBase.getState().equalsIgnoreCase("punjab")
				&& smsBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "Coordinator";
			List<String> punjabCoords = utilityService.fetchPunjabCoords(smsBase.getCity());
			daEmail = String.join(",", punjabCoords);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if(smsBase.getEmployment().equalsIgnoreCase("State")
				&& smsBase.getState().equalsIgnoreCase("Himachal Pradesh")) {
			daEmail = utilityService.fetchHimachalDa();
			toWhom = "Delegated Admin";
			recipientType = Constants.STATUS_DA_TYPE;
			nextStatus = Constants.STATUS_DA_PENDING;
		} else {
			
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(smsBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			Set<String> co = utilityService.fetchCoordinators(org);

				if(co != null && co.size() > 0) {

			if(da != null && da.size() > 0) {
				toWhom = "Delegated Admin";
				recipientType = Constants.STATUS_DA_TYPE;
				nextStatus = Constants.STATUS_DA_PENDING;
				daEmail = String.join(",", da);
			}else if(co != null && co.size() > 0) {

				toWhom = "Coordinator";
				daEmail = String.join(",", co);
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			} else {
				toWhom = "Support";
				daEmail = Constants.SMS_MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_SUPPORT_TYPE;
				nextStatus = Constants.STATUS_SUPPORT_PENDING;
			}
		}
		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);
		
		if (smsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom +"("+ daEmail + ")");
			responseBean.setStatus(
					"Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom +"("+ daEmail + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
		}
	}
		return responseBean;
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty String remarks) {
		responseBean.setRequestType("Rejection of request by RO.");
		log.info("sms form reject by {}",email);
		String formType = "sms";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		SmsBase smsBase = smsService.preview(regNumber);

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, smsBase.getHodMobile(),
				smsBase.getHodName(),"ro" ,regNumber);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, smsBase.getHodMobile(), smsBase.getHodName(),"ro");

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_CA_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_CA_REJECTED);
		finalAuditTrack.setToEmail("");

		if (smsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
	
}
