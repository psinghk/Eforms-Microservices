package in.nic.eForms.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.nic.eForms.entities.EmailActBase;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.MobileAndName;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.repositories.EmailActBaseRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class EmailActCOService {
	private final EmailActBaseRepo emailActBaseRepo;
	
	private final Util utilityService;

	@Autowired
	public EmailActCOService(EmailActBaseRepo emailActBaseRepo, Util utilityService) {
		super();
		this.emailActBaseRepo = emailActBaseRepo;
		this.utilityService = utilityService;

	}
	
	public EmailActBase fetchDetails(String regNo) {
		return emailActBaseRepo.findByRegistrationNo(regNo);
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

	public ResponseBean approveCO(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		log.info("Email Activation Form Approve  by CO {}",email);
		responseBean.setRequestType("Approval of request by NIC Coordinator");
		String formType = "emailactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		EmailActBase emailActBase = fetchDetails(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(emailActBase, OrganizationBean.class);
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),
				"coordinator");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(),"coordinator", regNumber);

		String preferred_email1 = "";

		preferred_email1 = emailActBase.getPreferredEmail();

		Set<String> bo = findBo(emailActBase);
		Set<String> domains = new HashSet<>();
		for (String boId : bo) {
			System.out.println("Bo id ::: " + boId);
			List<String> domain = utilityService.findDomains(boId);
            if (domain.size() > 0)
            {
                domains.addAll(domain);
            }
		}
		Set<String> da = utilityService.fetchDAs(org);
		daEmail = String.join(",", da);
		if (da != null && da.size() > 0) {

			if (daEmail.equalsIgnoreCase("kaushal.shailender@nic.in")) {

				String pref1_domain = preferred_email1.split("@")[1].trim().toLowerCase();
				if (domains.size() > 0) {
					if (domains.contains(pref1_domain)) {
						toWhom = "Delegated Admin";
						recipientType = Constants.STATUS_DA_TYPE;
						nextStatus = Constants.STATUS_DA_PENDING;
						daEmail = String.join(",", da);
					} else {
						nextStatus = Constants.STATUS_MAILADMIN_PENDING;
						toWhom = "Admin";
						daEmail = "support@gov.in";
						recipientType = Constants.STATUS_ADMIN_TYPE;
					}
				} else {
					toWhom = "Delegated Admin";
					recipientType = Constants.STATUS_DA_TYPE;
					nextStatus = Constants.STATUS_DA_PENDING;
					daEmail = String.join(",", da);
				}

			} else if (org.getEmployment().equalsIgnoreCase("State") && org.getState().equalsIgnoreCase("Punjab")) {

				String pref1_domain = preferred_email1.split("@")[1].trim().toLowerCase();
				if (domains.size() > 0) {
					if (domains.contains(pref1_domain)) {
						toWhom = "Delegated Admin";
						recipientType = Constants.STATUS_DA_TYPE;
						nextStatus = Constants.STATUS_DA_PENDING;
						daEmail = String.join(",", da);
					} else {
						nextStatus = Constants.STATUS_MAILADMIN_PENDING;
						toWhom = "Admin";
						daEmail = "support@gov.in";
						recipientType = Constants.STATUS_ADMIN_TYPE;
					}
				} else {
					toWhom = "Delegated Admin";
					recipientType = Constants.STATUS_DA_TYPE;
					nextStatus = Constants.STATUS_DA_PENDING;
					daEmail = String.join(",", da);
				}

			} else {
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				toWhom = "Admin";
				daEmail = "support@gov.in";
				recipientType = Constants.STATUS_ADMIN_TYPE;
			}

		} else {
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
			toWhom = "Support";
			daEmail = "support@gov.in";
			recipientType = Constants.STATUS_SUPPORT_TYPE;
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			 log.info("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom + "(" + daEmail + ")");
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

	public ResponseBean rejectCO(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		
		log.info("Email Activation Form reject  by CO {}",email);
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "emailactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),
				"coordinator");

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(),"coordinator", regNumber);

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

	public ResponseBean pullCO(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		
		log.info("Email Activation Form Approve  by pull by CO {}",email);
		responseBean.setRequestType("Coordinator is reverting the request to support.");
		String coordRemarks = "Reverted by coordinator " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			coordRemarks += "~User Remarks - " + remarks;
		}
		String formType = "emailactivate";
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
}
