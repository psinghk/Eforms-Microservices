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
import in.nic.ashwini.eForms.services.VpnService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/support")
@RestController
public class SupportController {
	private final VpnService vpnService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public SupportController(VpnService vpnService, ResponseBean responseBean, Util utilityService) {
		super();
		this.vpnService = vpnService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/forwardToCoordinator")
	public ResponseBean forwardToCoordinator(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam String remarks, @RequestParam @NotEmpty String coordEmail) {
		responseBean.setRequestType("Support is forwarding the request to coordinator");
		System.out.println("forwardToCoordinator:::::::::::::::");
		String formType = Constants.VPN_SINGLE_FORM_KEYWORD;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
					mobileAndName.getMobile(), mobileAndName.getName(), "support", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "VPN Support", "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "",
					"VPN Support", "support", regNumber);
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

		if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
		responseBean.setRequestType("Rejection of request by Support.");
		String formType = Constants.VPN_SINGLE_FORM_KEYWORD;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;

		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
					mobileAndName.getMobile(), mobileAndName.getName(), "support", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "VPN Support", "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "",
					"VPN Support", "support", regNumber);
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_SUPPORT_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_SUPPORT_REJECTED);
		finalAuditTrack.setToEmail("");

		if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
	public ResponseBean forwardToAdmin(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam String remarks) {
		System.out.println("AdminSupport::::::::::::");
		responseBean.setRequestType("Support is forwarding the request to Admin");

		String formType = Constants.VPN_SINGLE_FORM_KEYWORD;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
					mobileAndName.getMobile(), mobileAndName.getName(), "support", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "VPN Support", "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "",
					"VPN Support", "support", regNumber);
		}

		toWhom = "Admin";
		daEmail = Constants.VPN_MAILADMIN_EMAIL;
		recipientType = Constants.STATUS_ADMIN_TYPE;
		nextStatus = Constants.STATUS_MAILADMIN_PENDING;

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
		responseBean.setRequestType("Support is pulling the request to itself");
		String formType = Constants.VPN_SINGLE_FORM_KEYWORD;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		String pullBackFrom = "", pullBackFromUser = "", pullBackDB = "",pullBackInform = "";

		List<Status> statusTable = utilityService.fetchStatusTable(regNumber);
		for (Status status2 : statusTable) {
			if (status2.getStatus().equals("coordinator_pending") || status2.getStatus().equals("da_pending")
					|| status2.getStatus().equals("mail-admin_pending")) {
				pullBackFrom = status2.getRecipientType();
				pullBackFromUser = status2.getRecipient();
				if (pullBackFrom.equals("d")) {
					pullBackDB = "DA-Admin(" + pullBackFromUser + ")";
					pullBackInform = "DA-Admin" + "~" + pullBackFromUser;
				} else if (pullBackFrom.equals("c")) {
					pullBackDB = "Coordinator(" + pullBackFromUser + ")";
					pullBackInform = "Coordinator" + "~" + pullBackFromUser;
				} else if (pullBackFrom.equals("m")) {
					pullBackDB = "Admin(" + pullBackFromUser + ")";
					pullBackInform = "Admin" + "~" + pullBackFromUser;
				}
				break;
			}
		}
		
		status = new Status();
		if (mobileAndName != null) {
			status.setSenderName(mobileAndName.getName());
			status.setSenderMobile(mobileAndName.getMobile());
		} else {
			status.setSenderName("VPN Support");
			status.setSenderMobile("");
		}

		status = utilityService.initializeStatusTable(ip, email, formType, "pulled back by support from " + pullBackDB, mobileAndName.getMobile(), mobileAndName.getName(),"support");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, "", mobileAndName.getMobile(), mobileAndName.getName(),"support", regNumber);
		status.setSubmissionType("pulled_s_" + pullBackFrom);
		
		toWhom = "Support";
		daEmail = Constants.VPN_SUPPORT_EMAIL;
		recipientType = Constants.STATUS_SUPPORT_TYPE;
		nextStatus = Constants.STATUS_SUPPORT_PENDING;

		status.setRegistrationNo(regNumber.toUpperCase());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
