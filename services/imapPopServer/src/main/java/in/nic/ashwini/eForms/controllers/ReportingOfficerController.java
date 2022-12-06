package in.nic.ashwini.eForms.controllers;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.ImapPopBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.ImapPopService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final ImapPopService imappopService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public ReportingOfficerController(ImapPopService imappopService, ResponseBean responseBean, Util utilityService) {
		super();
		this.imappopService = imappopService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Approval of request by Reporting Officer");
		System.out.println("regNumber::: "+regNumber);
		String formType = "imappop";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ImapPopBase imapPopBase = imappopService.preview(regNumber);
		String dn = utilityService.findDn(imapPopBase.getEmail());
		String roDn = utilityService.findDn(email);
		List<String> aliases = utilityService.aliases(email);
		
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, imapPopBase.getHodMobile(),
				imapPopBase.getHodName(),"user");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
				imapPopBase.getHodMobile(), imapPopBase.getHodName(), "ro", regNumber);

		if (dn.toLowerCase().contains("gem.gov.in") || dn.toLowerCase().contains("gem-paid.gov.in")) {
			if (dn.toLowerCase().contains("gem.gov.in")) {
				daEmail = utilityService.fetchGemDAForFreeAccounts();
			} else if (dn.toLowerCase().contains("gem-paid.gov.in")) {
				daEmail = utilityService.fetchGemDAForPaidAccounts();
			}
			
			toWhom = "Delegated Admin";
			recipientType = Constants.STATUS_DA_TYPE;
			nextStatus = Constants.STATUS_DA_PENDING;
		} else if (utilityService.isNicEmployee(email) && (imapPopBase.getPostingState().equalsIgnoreCase("delhi")
				&& imapPopBase.getEmployment().equalsIgnoreCase("central")
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
		} else if(imapPopBase.getEmployment().equalsIgnoreCase("Others") 
				&& imapPopBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& imapPopBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& imapPopBase.getCity().equalsIgnoreCase("pune")
				&& (imapPopBase.getAddress().toLowerCase().contains("ndc")
						|| imapPopBase.getAddress().toLowerCase().contains("national data center"))) {
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();
			
			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
		} else if(imapPopBase.getEmployment().equalsIgnoreCase("State")
				&& imapPopBase.getState().equalsIgnoreCase("punjab")
				&& imapPopBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "Coordinator";
			List<String> punjabCoords = utilityService.fetchPunjabCoords(imapPopBase.getCity());
			daEmail = String.join(",", punjabCoords);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if(imapPopBase.getEmployment().equalsIgnoreCase("State")
				&& imapPopBase.getState().equalsIgnoreCase("Himachal Pradesh")) {
			daEmail = utilityService.fetchHimachalDa();
			toWhom = "Delegated Admin";
			recipientType = Constants.STATUS_DA_TYPE;
			nextStatus = Constants.STATUS_DA_PENDING;
		} else {
			
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(imapPopBase, OrganizationBean.class);
			
			Set<String> da = utilityService.fetchDAs(org);
			Set<String> co = utilityService.fetchCoordinators(org);
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
				daEmail = Constants.MAILADMIN_EMAIL;
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
		
		if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "imappop";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		ImapPopBase imapPopBase = imappopService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, imapPopBase.getHodMobile(), imapPopBase.getHodName(),"ro");

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, imapPopBase.getHodMobile(), imapPopBase.getHodName(), "ro", regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_CA_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_CA_REJECTED);
		finalAuditTrack.setToEmail("");

		if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
