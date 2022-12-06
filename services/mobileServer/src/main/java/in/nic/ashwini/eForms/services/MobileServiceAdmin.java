package in.nic.ashwini.eForms.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import in.nic.ashwini.eForms.entities.MobileBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.models.User;
import in.nic.ashwini.eForms.models.UserAttributes;
import in.nic.ashwini.eForms.repositories.MobileBaseRepo;
import in.nic.ashwini.eForms.repositories.MobileEmpCoordRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MobileServiceAdmin {
	
	private final MobileBaseRepo mobileBaseRepo;
	
	private final Util utilityService;

	private final MobileEmpCoordRepo mobileEmpCoordRepo;
	
	
	@Autowired
	public MobileServiceAdmin(MobileBaseRepo mobileBaseRepo, MobileEmpCoordRepo mobileEmpCoordRepo, Util utilityService) {
		super();
		this.mobileBaseRepo = mobileBaseRepo;
		this.utilityService = utilityService;
		this.mobileEmpCoordRepo = mobileEmpCoordRepo;

	}

	
	
	public ResponseBean approveAdmin( String regNumber,String ip, String email, @RequestParam String remarks,ResponseBean responseBean) {
		
		String formType = "mobile";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		
		MobileBase mobileBase = preview(regNumber);
		
		//utilityService.modifyLDAPAttribute(dn, "mobile", mobileBase.getCountryCode() + mobileBase.getNewMobile());
		UserAttributes userAttributes=new UserAttributes();
		User user =new User();
		user.setMobile(mobileBase.getCountry_code() + mobileBase.getNew_mobile());
		userAttributes.setUser(user);
		utilityService.updateThroughMail(userAttributes);
		
		
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}

		String daEmail = "";
		String recipientType = "";

		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(),"admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(),"admin", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support","admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support","admin",
					regNumber);
		}
		
		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(Constants.STATUS_COMPLETED);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(Constants.STATUS_COMPLETED);
		finalAuditTrack.setToEmail(daEmail);

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
	
	
	public ResponseBean rejectAdmin( String regNumber, String ip, String email,String remarks,ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by Admin.");
		String formType = "mobile";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(),"admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"admin", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support","admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support","admin",
					regNumber);
		}
		
		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.MAIL_ADMIN_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.MAIL_ADMIN_REJECTED);
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
	
	
	
	public ResponseBean forwardToDaAdmin( String regNumber, String ip, String email,String remarks,ResponseBean responseBean) {
		responseBean.setRequestType("Admin is forwarding the request to DA");
		String adminRemarks = "Forwarded by Admin " + email + " to Delegated Admin.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "mobile";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileBase mobileBase = preview(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(mobileBase, OrganizationBean.class);
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

		
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"admin");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"support", regNumber);
		status.setSubmissionType("forwarded_m_d");
		status.setRemarks(adminRemarks);
		
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

			if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				 log.info("Application (" + regNumber + ") has been pulled Successfully to " +
				 toWhom + "(" + daEmail + ")");
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

	
	public ResponseBean pullAdmin(String regNumber, String ip,String email, String remarks,ResponseBean responseBean) {
		responseBean.setRequestType("Admin is reverting the request to support.");
		String adminRemarks = "Reverted by admin " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
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

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"admin");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"support", regNumber);
		status.setSubmissionType("reverted_m_s");
		status.setRemarks(adminRemarks);

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
			 log.info("Application (" + regNumber + ") has been pulled Successfully to " +
			 toWhom + "(" + daEmail + ")");
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
