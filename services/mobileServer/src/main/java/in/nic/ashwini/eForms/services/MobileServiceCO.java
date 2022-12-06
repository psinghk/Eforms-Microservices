package in.nic.ashwini.eForms.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.entities.MobileBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.MobileBaseRepo;
import in.nic.ashwini.eForms.repositories.MobileEmpCoordRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MobileServiceCO {

	private final MobileBaseRepo mobileBaseRepo;

	private final Util utilityService;

	private final MobileEmpCoordRepo mobileEmpCoordRepo;

	@Autowired
	public MobileServiceCO(MobileBaseRepo mobileBaseRepo, MobileEmpCoordRepo mobileEmpCoordRepo, Util utilityService) {
		super();
		this.mobileBaseRepo = mobileBaseRepo;
		this.utilityService = utilityService;
		this.mobileEmpCoordRepo = mobileEmpCoordRepo;

	}

	public ResponseBean approveCO(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {

		responseBean.setRequestType("Approval of request by NIC Coordinator");
		String formType = "mobile";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileBase mobileBase = preview(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(mobileBase, OrganizationBean.class);
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		String dn = utilityService.findDn(mobileBase.getEmail());
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(), "coordinator");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
				mobileAndName.getMobile(), mobileAndName.getName(), "coordinator", regNumber);

		if (mobileBase.getEmployment().equalsIgnoreCase("State") && mobileBase.getState().equalsIgnoreCase("punjab")
				&& mobileBase.getPostingState().equalsIgnoreCase("punjab")) {
			Set<String> da = utilityService.fetchDAs(org);
			toWhom = "Delegated Admin";
			recipientType = Constants.STATUS_DA_TYPE;
			nextStatus = Constants.STATUS_DA_PENDING;
			daEmail = String.join(",", da);
		} else {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		}
		Set<String> emp_bos = findBo(mobileBase);
		Set<String> da = utilityService.fetchDAs(org);
		Set<String> co = utilityService.fetchCoordinators(org);
		String[] namesList = dn.split(",");
		String name1 = namesList[0];
		String name2 = namesList[1];
		String name3 = namesList[2];

		String[] stringList = name3.split("=");
		String list1 = stringList[0];
		String ldap_bos = stringList[1];

//		Po bos = utilityService.fetchBos(dn);
//		Set<String> ldap_bos=bos.getBo();
		// boolean matchBos = CollectionUtils.containsAny(emp_bos, ldap_bos);

		boolean matchBos = emp_bos.contains(ldap_bos);

		if (matchBos) {
			if (da != null && da.size() > 0) {
				toWhom = "Delegated Admin";
				recipientType = Constants.STATUS_DA_TYPE;
				nextStatus = Constants.STATUS_DA_PENDING;
				daEmail = String.join(",", da);
			} else if (co != null && co.size() > 0) {
				toWhom = "admin";
				daEmail = String.join(",", co);
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
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

	public ResponseBean pullCO(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {

		responseBean.setRequestType("Coordinator is reverting the request to support.");
		String coordRemarks = "Reverted by coordinator " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			coordRemarks += "~User Remarks - " + remarks;
		}
		String formType = "mobile";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);

		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(), "coordinator");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, remarks,
				mobileAndName.getMobile(), mobileAndName.getName(), "coordinator", regNumber);
		status.setSubmissionType("reverted_c_s");

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

	public ResponseBean rejectCO(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "mobile";
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
