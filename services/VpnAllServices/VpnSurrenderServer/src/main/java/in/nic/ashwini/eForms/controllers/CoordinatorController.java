package in.nic.ashwini.eForms.controllers;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.VpnBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.VpnService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/coordinator")
@RestController
public class CoordinatorController {
	private final VpnService vpnService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public CoordinatorController(VpnService vpnService, ResponseBean responseBean, Util utilityService) {
		super();
		this.vpnService = vpnService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Approval of request by NIC Coordinator");
		String formType = Constants.VPN_SURRENDER_FORM_KEYWORD;;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(), "coordinator");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
				mobileAndName.getMobile(), mobileAndName.getName(), "coordinator", regNumber);
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
			log.info("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom + "(" + daEmail
					+ ")");
			responseBean.setStatus("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom
					+ "(" + daEmail + ")");
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
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = Constants.VPN_SURRENDER_FORM_KEYWORD;;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(), "coordinator");

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
				mobileAndName.getMobile(), mobileAndName.getName(), "coordinator", regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_COORDINATOR_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_COORDINATOR_REJECTED);
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

	@RequestMapping(value = "/revert")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Coordinator is reverting the request to support.");
		String coordRemarks = "Reverted by coordinator " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			coordRemarks += "~User Remarks - " + remarks;
		}
		String formType = Constants.VPN_SURRENDER_FORM_KEYWORD;;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);

		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"coordinator");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"coordinator", regNumber);
		status.setSubmissionType("reverted_c_s");
		//status = new Status();

		toWhom = "Support";
		daEmail = Constants.VPN_MAILADMIN_EMAIL;
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
