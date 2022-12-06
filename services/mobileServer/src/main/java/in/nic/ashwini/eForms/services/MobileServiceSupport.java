package in.nic.ashwini.eForms.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.entities.MobileBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.MobileBaseRepo;
import in.nic.ashwini.eForms.repositories.MobileEmpCoordRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MobileServiceSupport {

	private final MobileBaseRepo mobileBaseRepo;

	private final Util utilityService;

	private final MobileEmpCoordRepo mobileEmpCoordRepo;

	@Autowired
	public MobileServiceSupport(MobileBaseRepo mobileBaseRepo, MobileEmpCoordRepo mobileEmpCoordRepo,
			Util utilityService) {
		super();
		this.mobileBaseRepo = mobileBaseRepo;
		this.utilityService = utilityService;
		this.mobileEmpCoordRepo = mobileEmpCoordRepo;

	}

	public ResponseBean forwardToCoordinator(String regNumber, String ip, String email, String remarks,
			String coordEmail, ResponseBean responseBean) {
		responseBean.setRequestType("Support is forwarding the request to coordinator");
		String formType = "mobile";
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

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	public ResponseBean reject(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by Support.");
		String formType = "mobile";
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

	public ResponseBean forwardToDa(String regNumber, String ip, String email, String remarks, String daEmail,
			ResponseBean responseBean) {
		responseBean.setRequestType("Support is forwarding the request to DA");
		String formType = "mobile";
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

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	public ResponseBean forwardToAdmin(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		responseBean.setRequestType("Support is forwarding the request to Admin");
		String formType = "relay";
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

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	public ResponseBean pull(String regNumber, String ip, String email, ResponseBean responseBean) {
		responseBean.setRequestType("Support is pulling the request to itself");
		String formType = "mobile";
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

		status = utilityService.initializeStatusTable(ip, email, formType, "pulled back by support from " + pullBackDB,
				mobileAndName.getMobile(), mobileAndName.getName(), "support");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, "",
				mobileAndName.getMobile(), mobileAndName.getName(), "support", regNumber);
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

	public MobileBase preview(String regNo) {
		return mobileBaseRepo.findByRegistrationNo(regNo);
	}

	public Set<String> findBo(MobileBase mobileBase) {
		List<String> bo = null;
		if (mobileBase.getEmployment().trim().equalsIgnoreCase("central")
				|| mobileBase.getEmployment().trim().equalsIgnoreCase("ut")) {

			bo = utilityService.fetchBoByMinistry(mobileBase.getEmployment(), mobileBase.getMinistry(),
					mobileBase.getDepartment());
		} else if (mobileBase.getEmployment().trim().equalsIgnoreCase("state")) {
			bo = utilityService.fetchBoByState(mobileBase.getEmployment(), mobileBase.getState(),
					mobileBase.getDepartment());
		} else {
			bo = utilityService.fetchBoByOrg(mobileBase.getEmployment(), mobileBase.getOrganization());
		}
		Set<String> bos = new HashSet<String>(bo);
		return bos;
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

}
