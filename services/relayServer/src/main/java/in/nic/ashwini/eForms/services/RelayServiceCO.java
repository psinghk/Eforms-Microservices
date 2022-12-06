package in.nic.ashwini.eForms.services;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.entities.RelayBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.RelayBaseRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RelayServiceCO {

	private final ResponseBean responseBean;
	private final Util utilityService;
	private final RelayBaseRepo relayBaseRepo;

	@Autowired
	public RelayServiceCO(ResponseBean responseBean, Util utilityService, RelayBaseRepo relayBaseRepo) {
		super();
		this.relayBaseRepo = relayBaseRepo;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	public ResponseBean approve(String regNumber, String ip, String email, String remarks, ResponseBean resposeBean) {
		responseBean.setRequestType("Approval of request by NIC Coordinator");
		String formType = "relay";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		RelayBase relayBase = preview(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(relayBase, OrganizationBean.class);
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(), "coordinator");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
				mobileAndName.getMobile(), mobileAndName.getName(), "coordinator", regNumber);

//		if (relayBase.getEmployment().equalsIgnoreCase("State") && relayBase.getState().equalsIgnoreCase("punjab")
//				&& relayBase.getPostingState().equalsIgnoreCase("punjab")) {
//			Set<String> da = utilityService.fetchDAs(org);
//			toWhom = "Delegated Admin";
//			recipientType = Constants.STATUS_DA_TYPE;
//			nextStatus = Constants.STATUS_DA_PENDING;
//			daEmail = String.join(",", da);
//		}
//		
//		else {
		toWhom = "Admin";
		daEmail = Constants.MAILADMIN_EMAIL;
		recipientType = Constants.STATUS_ADMIN_TYPE;
		nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		// }

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	public ResponseBean reject(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "relay";
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

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	public ResponseBean pull(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Coordinator is reverting the request to support.");
		String coordRemarks = "Reverted by coordinator " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			coordRemarks += "~User Remarks - " + remarks;
		}
		String formType = "relay";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);

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

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(), "admin");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, remarks,
				mobileAndName.getMobile(), mobileAndName.getName(), "support", regNumber);
		status.setSubmissionType("reverted_m_s");
		status.setRemarks(coordRemarks);

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

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	public RelayBase preview(String regNo) {
		return relayBaseRepo.findByRegistrationNo(regNo);
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

}
