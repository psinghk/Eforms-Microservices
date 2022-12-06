package in.nic.ashwini.eForms.controllers;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.DnsService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/support")
@RestController
public class SupportController {
	private final DnsService dnsService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public SupportController(DnsService dnsService, ResponseBean responseBean, Util utilityService) {
		super();
		this.dnsService = dnsService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/forwardToCoordinator")
	public ResponseBean forwardToCoordinator(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks,
			@RequestParam @NotEmpty String coordEmail) {
		log.info("Forwarding registration no {} to Co-ordinator at support by {}.",regNumber,email);
		responseBean.setRequestType("Support is forwarding the request to coordinator");
		String formType = "dns";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
			log.info("Registration no {} email-id {} is not of support. Support mobile from LDAP is {}",regNumber,email,mobileAndName.getMobile());
		}
		
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(),"support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(),"support", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support","support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support","support",
					regNumber);
		}

		toWhom = "Coordinator";
		daEmail = coordEmail;
		recipientType = Constants.STATUS_COORDINATOR_TYPE;
		nextStatus = Constants.STATUS_COORDINATOR_PENDING;

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") has been forwarded Successfully to " + toWhom + "(" + daEmail
					+ ")");
			responseBean.setStatus("Application (" + regNumber + ") has been forwarded Successfully to " + toWhom + "("
					+ daEmail + ")");
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
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		log.info("Rejecting registration no {} at Support by {}.",regNumber,email);
		responseBean.setRequestType("Rejection of request by Support.");
		String formType = "dns";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;

		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
			log.info("Registration no {} email-id {} is not of support. Support mobile from LDAP is {}",regNumber,email,mobileAndName.getMobile());
		}
		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(),"support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(),"support", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support","support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support","support",
					regNumber);
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_SUPPORT_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_SUPPORT_REJECTED);
		finalAuditTrack.setToEmail("");

		if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	@RequestMapping(value = "/forwardToAdmin")
	public ResponseBean forwardToAdmin(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		log.info("Forwarding registration no {} to Admin at support by {}.",regNumber,email);
		responseBean.setRequestType("Support is forwarding the request to Admin");
		String formType = "dns";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
			log.info("Registration no {} email-id {} is not of support. Support mobile from LDAP is {}",regNumber,email,mobileAndName.getMobile());
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(),"support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(),"support", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support","support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support","support",
					regNumber);
		}

		toWhom = "Admin";
		daEmail = Constants.MAILADMIN_EMAIL;
		recipientType = Constants.STATUS_ADMIN_TYPE;
		nextStatus = Constants.STATUS_MAILADMIN_PENDING;

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") has been forwarded Successfully to " + toWhom + "(" + daEmail
					+ ")");
			responseBean.setStatus("Application (" + regNumber + ") has been forwarded Successfully to " + toWhom + "("
					+ daEmail + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}

	@RequestMapping(value = "/pull")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email) {
		log.info("Pull registration no {} at support by {}.",regNumber,email);
		responseBean.setRequestType("Support is pulling the request to itself");
		String formType = "dns";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
			log.info("Registration no {} email-id {} is not of support. Support mobile from LDAP is {}",regNumber,email,mobileAndName.getMobile());
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		
		
		
		String pullBackFrom = "", pullBackFromUser = "", pullBackDB = "", pullBackInform = "";

		List<Status> statusTable = utilityService.fetchStatusTable(regNumber);
		for (Status status2 : statusTable) {
			if (status2.getStatus().equals("coordinator_pending") || status2.getStatus().equals("da_pending")
					|| status2.getStatus().equals("mail-admin_pending")) {
				pullBackFrom = status2.getRecipientType();
				pullBackFromUser = status2.getRecipient();
				if (pullBackFrom.equals("d")) {
					pullBackDB = "DA-Admin(" + pullBackFromUser + ")";
					pullBackInform = "DA-Admin" + "~" + pullBackFromUser;  // dead code Line
				} else if (pullBackFrom.equals("c")) {
					pullBackDB = "Coordinator(" + pullBackFromUser + ")";
					pullBackInform = "Coordinator" + "~" + pullBackFromUser;  // dead code Line
				} else if (pullBackFrom.equals("m")) {
					pullBackDB = "Admin(" + pullBackFromUser + ")";
					pullBackInform = "Admin" + "~" + pullBackFromUser;  // dead code Line
				}
				break;
			}
		}
		
		status = new Status();
		if (mobileAndName != null) {
			status.setSenderName(mobileAndName.getName());
			status.setSenderMobile(mobileAndName.getMobile());
		} else {
			status.setSenderName("iNOC Support");
			status.setSenderMobile("");
		}

		status = utilityService.initializeStatusTable(ip, email, formType, "pulled back by support from " + pullBackDB, mobileAndName.getMobile(), mobileAndName.getName(),"support");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, "", mobileAndName.getMobile(), mobileAndName.getName(),"support", regNumber);
		status.setSubmissionType("pulled_s_" + pullBackFrom);
		
		toWhom = "Support";
		daEmail = Constants.MAILADMIN_EMAIL;
		recipientType = Constants.STATUS_SUPPORT_TYPE;
		nextStatus = Constants.STATUS_SUPPORT_PENDING;

		status.setRegistrationNo(regNumber.toUpperCase());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "(" + daEmail + ")");
			responseBean.setStatus(
					"Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "(" + daEmail + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}
}
