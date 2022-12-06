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
import in.nic.ashwini.eForms.entities.DlistBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.DlistService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@Validated
@RestController
public class AdminController {

	private final DlistService dlistService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public AdminController(DlistService dlistService, ResponseBean responseBean, Util utilityService) {
		super();
		this.dlistService = dlistService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam String remarks) {
		responseBean.setRequestType("Completion of request by Admin");
		log.info("Dlist Form approve by {}",email);
		String formType = "dlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}

		String daEmail = "";
		String recipientType = "";

		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "admin", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support", "admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support",
					"admin", regNumber);
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(Constants.STATUS_COMPLETED);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(Constants.STATUS_COMPLETED);
		finalAuditTrack.setToEmail(daEmail);

		if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") completed Successfully.");
			responseBean.setStatus("Application (" + regNumber + ") completed Successfully.");
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
			@RequestParam("email") @NotEmpty String email, @NotEmpty String remarks) {
		responseBean.setRequestType("Rejection of request by Admin.");
		log.info("Dlist Form reject by {}",email);
		String formType = "dlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(), "admin", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support", "admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support", "admin", regNumber);
		}
		
		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.MAIL_ADMIN_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.MAIL_ADMIN_REJECTED);
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

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam String remarks) {
		responseBean.setRequestType("Admin is forwarding the request to DA");
		log.info("Dlist Form forwardToDa by {}",email);
		String adminRemarks = "Forwarded by Admin " + email + " to Delegated Admin.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "dlist";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		DlistBase dlistBase = dlistService.preview(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(dlistBase, OrganizationBean.class);
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		LocalDateTime currentTime = null;
		status = new Status();
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		if (mobileAndName != null) {
			status.setSenderName(mobileAndName.getName());
			status.setSenderMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminName(mobileAndName.getName());
		} else {
			status.setSenderName("iNOC Support");
			status.setSenderMobile("");
			finalAuditTrack.setAdminMobile("");
			finalAuditTrack.setAdminName("iNOC Support");
		}

		status.setFormType(formType);
		status.setIp(ip);
		status.setSenderIp(ip);
		status.setOnholdStatus("n");
		status.setFinalId("");
		status.setRemarks(adminRemarks);
		status.setSubmissionType("forwarded_m_d");
		status.setSenderType(Constants.STATUS_ADMIN_TYPE);
		status.setSender(email);
		status.setSenderEmail(email);
		currentTime = LocalDateTime.now();
		status.setCreatedon(currentTime);
		status.setSenderDatetime(currentTime);

		finalAuditTrack.setAdminIp(ip);
		finalAuditTrack.setAdminRemarks(adminRemarks);
		finalAuditTrack.setAdminEmail(email);
		finalAuditTrack.setAdminDatetime(currentTime);
		finalAuditTrack.setToDatetime(currentTime);

		Set<String> da = utilityService.fetchDAs(org);

		if (da != null && da.size() > 0) {
			toWhom = "Delegated Admin";
			recipientType = Constants.STATUS_DA_TYPE;
			nextStatus = Constants.STATUS_DA_PENDING;
			daEmail = String.join(",", da);

			status.setRegistrationNo(regNumber.toUpperCase());
			status.setRecipientType(recipientType);
			status.setStatus(nextStatus);
			status.setRecipient(daEmail);

			finalAuditTrack.setStatus(nextStatus);
			finalAuditTrack.setToEmail(daEmail);

			if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "(" + daEmail
						+ ")");
				responseBean.setStatus("Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "("
						+ daEmail + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber(regNumber);
			}
		} else {
			log.debug("There are no DAs for this request(Organization Details)");
			responseBean.setStatus("There are no DAs for this request(Organization Details)");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}

	@RequestMapping(value = "/revert")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, String remarks) {
		responseBean.setRequestType("Admin is reverting the request to support.");
		log.info("Dlist Form revert by {}",email);
		String adminRemarks = "Reverted by admin " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "";
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

		LocalDateTime currentTime = null;
		status = new Status();
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		if (mobileAndName != null) {
			status.setSenderName(mobileAndName.getName());
			status.setSenderMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminName(mobileAndName.getName());
		} else {
			status.setSenderName("iNOC Support");
			status.setSenderMobile("");
			finalAuditTrack.setAdminMobile("");
			finalAuditTrack.setAdminName("iNOC Support");
		}

		status.setFormType(formType);
		status.setIp(ip);
		status.setSenderIp(ip);
		status.setOnholdStatus("n");
		status.setFinalId("");
		status.setRemarks(adminRemarks);
		status.setSubmissionType("reverted_m_s");
		status.setSenderType(Constants.STATUS_ADMIN_TYPE);
		status.setSender(email);
		status.setSenderEmail(email);
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

		if (dlistService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
