package in.nic.eForms.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.nic.eForms.entities.EmailActBase;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.MobileAndName;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.repositories.EmailActBaseRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailActAdminService {

	private final EmailActBaseRepo emailActBaseRepo;
	private final Util utilityService;

	@Autowired
	public EmailActAdminService(EmailActBaseRepo emailActBaseRepo,
			Util utilityService) {
		super();
		this.emailActBaseRepo = emailActBaseRepo;
		this.utilityService = utilityService;
	}

	public EmailActBase fetchDetails(String regNo) {
		return emailActBaseRepo.findByRegistrationNo(regNo);
	}

	public Set<String> getDomain(ProfileDto profile) {
		TreeSet<String> finaldomain = new TreeSet<>();
		
		try {
			if (profile.getEmployment().equalsIgnoreCase("central") || profile.getEmployment().equalsIgnoreCase("ut")) {
				log.info(":::::1::::" + profile.getEmployment() + ":::::::::::" + profile.getMinistry()
						+ "::::::::::" + profile.getDepartment());
				List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
						profile.getMinistry(), profile.getDepartment());
				for (String string : temp) {
					if (string != null) {
						finaldomain.add(string);
						log.info(":::::1::::" + finaldomain);
					}
				}
			} else if (profile.getEmployment().equalsIgnoreCase("state")) {
				log.info(":::::2::::" + profile.getEmployment() + ":::::::::::" + profile.getState()
						+ "::::::::::" + profile.getDepartment());
				List<String> temp = utilityService.fetchDomainsByCatAndMinAndDep(profile.getEmployment(),
						profile.getState(), profile.getDepartment());

				for (String string : temp) {
					if (!string.equals("null")) {
						finaldomain.add(string);
						log.info(":::::2::::" + finaldomain);
					}
				}
			} else {
				List<String> temp = utilityService.fetchDomainsByCatAndMin(profile.getEmployment(),
						profile.getMinistry());
				log.info(":::::3::::" + profile.getEmployment() + ":::::::::::" + profile.getOrganization());
				for (String string : temp) {
					if (!string.equals("null")) {
						finaldomain.add(string);
						log.info(":::::3::::" + finaldomain);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return finaldomain;
	}

	public List<String>fetchByCentralMinistry(String empCategory)
	{
		List<String> fcm = new ArrayList<>();
		fcm=utilityService.fetchByCentralMinistry(empCategory);
		System.out.println(fcm+"checking the data");
		return fcm;
	}
	
	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

	public Set<String> findBo(EmailActBase emailActBase) {
		List<String> bos = new ArrayList<>();
		if (emailActBase.getEmployment().trim().equalsIgnoreCase("central")
				|| emailActBase.getEmployment().trim().equalsIgnoreCase("ut")) {

			bos = utilityService.fetchBoByMinistry(emailActBase.getEmployment(), emailActBase.getMinistry(),
					emailActBase.getDepartment());
		} else if (emailActBase.getEmployment().trim().equalsIgnoreCase("state")) {
			bos = utilityService.fetchBoByState(emailActBase.getEmployment(), emailActBase.getState(),
					emailActBase.getDepartment());
		} else {
			bos = utilityService.fetchBoByOrg(emailActBase.getEmployment(), emailActBase.getOrganization());
		}
		return (Set<String>) bos;
	}

	public String getLDAPModifyDate(String DBDate) {
		String LDAPDate = "";
		if (DBDate != null && !DBDate.equals("")) {
			String dd = "", mm = "", yy = "";
			String[] arr = DBDate.split("-");
			dd = arr[0];
			mm = arr[1];
			yy = arr[2];
			LDAPDate = yy + mm + dd + "000000Z";
		}
		return LDAPDate;
	}

	public ResponseBean rejectAdmin(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		
		log.info("Email Activation Form reject by {}",email);
		responseBean.setRequestType("Rejection of request by Admin.");
		String formType = "emailactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;

		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		if (mobileAndName != null) {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), "admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
					mobileAndName.getMobile(), mobileAndName.getName(), "admin", regNumber);
		} else {
			status = utilityService.initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support", "admin");
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, "",
					"iNOC Support", "admin", regNumber);
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
	
	public ResponseBean approveAdmin(String regNumber, String ip, String email,
	 String remarks, ResponseBean responseBean) throws ParseException {
		
        responseBean.setRequestType("Completion of request by Admin");
		log.info("Email Activation Form approve by {}",email);
		String formType = "emailactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
       // For Active status in LDAP
		EmailActBase emailActBase = fetchDetails(regNumber);
		String preferEmail = emailActBase.getPreferredEmail();
		String uid = utilityService.fetchUid(preferEmail);
		System.out.println("uid ###### "+uid);
		utilityService.activateFromLdap(uid);
		
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
	
	public ResponseBean forwardToDaAdmin(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		log.info("Email Activation Form forwardToDaAdmin by {}",email);
		responseBean.setRequestType("Admin is forwarding the request to DA");
		String adminRemarks = "Forwarded by Admin " + email + " to Delegated Admin.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "emailactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		EmailActBase emailActBase = fetchDetails(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(emailActBase, OrganizationBean.class);
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

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

	public ResponseBean pullAdmin(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		log.info("Email Activation Form pullAdmin by {}",email);
		responseBean.setRequestType("Admin is reverting the request to support.");
		String adminRemarks = "Reverted by admin " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "emailactivate";
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
