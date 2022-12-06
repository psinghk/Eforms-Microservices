package in.nic.ashwini.eForms.controllers;

import java.time.LocalDateTime;
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
import in.nic.ashwini.eForms.services.ImapPopService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/support")
@RestController
public class SupportController {
	private final ImapPopService imappopService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public SupportController(ImapPopService imappopService, ResponseBean responseBean, Util utilityService) {
		super();
		this.imappopService = imappopService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/forwardToCoordinator")
	public ResponseBean forwardToCoordinator(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks,
			@RequestParam @NotEmpty String coordEmail) {
		responseBean.setRequestType("Support is forwarding the request to coordinator");
		String formType = "imappop";
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
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support", "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "",
					"iNOC Support", "support", regNumber);
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

		if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
		String formType = "imappop";
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
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support", "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "",
					"iNOC Support", "support", regNumber);
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_SUPPORT_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_SUPPORT_REJECTED);
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

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks,
			@RequestParam @NotEmpty String daEmail) {
		responseBean.setRequestType("Support is forwarding the request to DA");
		String formType = "imappop";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
					mobileAndName.getMobile(), mobileAndName.getName(), "support", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support", "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "",
					"iNOC Support", "support", regNumber);
		}

		toWhom = "Delegated Admin";
		recipientType = Constants.STATUS_DA_TYPE;
		nextStatus = Constants.STATUS_DA_PENDING;

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	@RequestMapping(value = "/forwardToAdmin")
	public ResponseBean forwardToAdmin(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Support is forwarding the request to Admin");
		String formType = "imappop";
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
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support", "support");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "",
					"iNOC Support", "support", regNumber);
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

		if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
		String formType = "imappop";
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
		String pullBackFrom = "", pullBackFromUser = "", pullBackDB = "", pullBackInform = "";

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
		LocalDateTime currentTime = null;
		status = new Status();
		if (mobileAndName != null) {
			status.setSenderName(mobileAndName.getName());
			status.setSenderMobile(mobileAndName.getMobile());
		} else {
			status.setSenderName("iNOC Support");
			status.setSenderMobile("");
		}

		status.setFormType(formType);
		status.setIp(ip);
		status.setSenderIp(ip);
		status.setOnholdStatus("n");
		status.setFinalId("");
		status.setRemarks("pulled back by support from " + pullBackDB);
		status.setSubmissionType("pulled_s_" + pullBackFrom);
		status.setSenderType(pullBackFrom);
		status.setSender(pullBackFromUser);
		status.setSenderEmail(pullBackFromUser);
		currentTime = LocalDateTime.now();
		status.setCreatedon(currentTime);
		status.setSenderDatetime(currentTime);

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		finalAuditTrack.setSupportIp(null);
		finalAuditTrack.setSupportRemarks(null);
		finalAuditTrack.setSupportMobile(null);
		finalAuditTrack.setSupportName(null);
		finalAuditTrack.setSupportEmail(null);
		finalAuditTrack.setSupportDatetime(null);
		finalAuditTrack.setToDatetime(currentTime);

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

		if (imappopService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
