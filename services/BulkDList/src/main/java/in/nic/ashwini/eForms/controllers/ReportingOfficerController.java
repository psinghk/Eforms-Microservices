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
import in.nic.ashwini.eForms.entities.BulkDlistBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.BulkDlistService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final BulkDlistService dlistService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public ReportingOfficerController(BulkDlistService dlistService, ResponseBean responseBean, Util utilityService) {
		super();
		this.dlistService = dlistService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam String remarks) {
		responseBean.setRequestType("Approval of request by Reporting Officer");
		
		log.info("Bulk Dlist Form Approve by {}",email);		
		String formType = "bulkdlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		BulkDlistBase dlistBase = dlistService.preview(regNumber);
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
		else {
			
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(dlistBase, OrganizationBean.class);
			
//			Set<String> da = utilityService.fetchDAs(org);
			Set<String> co = utilityService.fetchCoordinators(org);
//			if(da != null && da.size() > 0) {
//				toWhom = "Delegated Admin";
//				recipientType = Constants.STATUS_DA_TYPE;
//				nextStatus = Constants.STATUS_DA_PENDING;
//				daEmail = String.join(",", da);
//			}
//			
//			else
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
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty String remarks) {
		log.info("Bulk Dlist Form reject by {}",email);
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "bulkdlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		BulkDlistBase dlistBase = dlistService.preview(regNumber);

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
	
}
