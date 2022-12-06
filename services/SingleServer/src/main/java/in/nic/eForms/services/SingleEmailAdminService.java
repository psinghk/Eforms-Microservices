package in.nic.eForms.services;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

//import in.nic.eForms.entities.NknSingleEmpCoord;
import in.nic.eForms.entities.SingleEmailBase;
import in.nic.eForms.models.AdminFormBean;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.MobileAndName;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.UserForCreate;
import in.nic.eForms.models.UserForCreateForAppUsers;
import in.nic.eForms.repositories.SingleBaseRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SingleEmailAdminService {

	private final SingleBaseRepo singleBaseRepo;
	private final Util utilityService;

	@Autowired
	public SingleEmailAdminService(SingleBaseRepo singleBaseRepo,
			Util utilityService) {
		super();
		this.singleBaseRepo = singleBaseRepo;
		this.utilityService = utilityService;


	}

	public SingleEmailBase fetchDetails(String regNo) {
		return singleBaseRepo.findByRegistrationNo(regNo);
	}

	public Set<String> getDomain(ProfileDto profile) {
		TreeSet<String> finaldomain = new TreeSet<>();
		//emergency
		//Set<NknSingleEmpCoord> fetchdeails = new HashSet<>();
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
			//emergency
			/*
			 * for (NknSingleEmpCoord nknSingleEmpCoord : fetchdeails) { if
			 * (nknSingleEmpCoord.getDomain() != null &&
			 * !nknSingleEmpCoord.getDomain().isEmpty() &&
			 * !nknSingleEmpCoord.getDomain().trim().equalsIgnoreCase("null")) { if
			 * (nknSingleEmpCoord.getEmp_mail_acc_cat() != null &&
			 * nknSingleEmpCoord.getEmp_mail_acc_cat().trim().equalsIgnoreCase("paid")) {
			 * finaldomain.add(nknSingleEmpCoord.getDomain().trim().toLowerCase()); } else {
			 * finaldomain.add(nknSingleEmpCoord.getDomain().trim().toLowerCase()); } } }
			 */
			// please use ldap for domain
			/*
			 * if (finaldomain.isEmpty()) { return getnknDomain(); }
			 */

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

	public Set<String> findBo(SingleEmailBase singleEmailBase) {
		List<String> bos = new ArrayList<>();
		if (singleEmailBase.getEmployment().trim().equalsIgnoreCase("central")
				|| singleEmailBase.getEmployment().trim().equalsIgnoreCase("ut")) {

			bos = utilityService.fetchBoByMinistry(singleEmailBase.getEmployment(), singleEmailBase.getMinistry(),
					singleEmailBase.getDepartment());
		} else if (singleEmailBase.getEmployment().trim().equalsIgnoreCase("state")) {
			bos = utilityService.fetchBoByState(singleEmailBase.getEmployment(), singleEmailBase.getState(),
					singleEmailBase.getDepartment());
		} else {
			bos = utilityService.fetchBoByOrg(singleEmailBase.getEmployment(), singleEmailBase.getOrganization());
		}
		return (Set<String>) bos;
	}

	public Boolean checkMobileDuplicate(String regNumber, String mobile, String allow_creation) {
		Boolean allow = false;
		String search = utilityService.checkAvailableEmail(mobile);
		if (search.equals("")) {
			allow = true;
		}
		return allow;
	}

	public String fetchRandomPassword() {
		StringBuilder password = new StringBuilder();
		int j = 0;
		for (int i = 0; i < 10; i++) {
			password.append(fetchRandomPasswordCharacters(j));
			j++;
			if (j == 4) {
				j = 0;
			}
		}
		return password.toString();
	}

	private String fetchRandomPasswordCharacters(int pos) {
		Random randomNum = new Random();
		StringBuilder randomChar = new StringBuilder();
		switch (pos) {
		case 0:
			randomChar.append(
					"ABCDEFGHJKMNOPQRSTUVWXYZ".charAt(randomNum.nextInt("ABCDEFGHJKMNOPQRSTUVWXYZ".length() - 1)));
			break;
		case 1:
			randomChar.append("0123456789".charAt(randomNum.nextInt("0123456789".length() - 1)));
			break;
		case 2:
			randomChar.append("@#$%&*".charAt(randomNum.nextInt("@#$%&*".length() - 1)));
			break;
		case 3:

			randomChar.append(
					"abcdefghjkmnopqrstuvwxyz".charAt(randomNum.nextInt("abcdefghjkmnopqrstuvwxyz".length() - 1)));// line
		}
		return randomChar.toString();
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
	
	public Map<String, Object> validateRequest(@Valid @RequestBody AdminFormBean adminBean,@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email) {
		
		 String primaryEmail = adminBean.getPrimaryId()+ "@" + adminBean.getDomain();
         String mailEquiv = adminBean.getFinalEmailId() + "@nic.in";
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		//OrganizationDto organizationDetails = new OrganizationDto();
		
		
		
		if (adminBean.getDescription() == null || !adminBean.getDescription().equals("")) {
			log.debug("Selected Description should be ");
			map.put("descriptionError", "Selected Description should be ");
		}
		if (adminBean.getPo() == null || !utilityService.fetchPos("o=nic.in,dc=nic,dc=in").contains(adminBean.getPo())) {
			log.debug("Selected Po of Birth should be ");
			map.put("poError", "Selected Po should be ");
		}
		if (adminBean.getPo() == null || adminBean.getBo() == null || !utilityService.fetchBos(adminBean.getPo()).contains(adminBean.getBo())) {
			log.debug("Selected Bo of Birth should be ");
			map.put("boError", "Selected Bo should be ");
		}
		if (adminBean.getPo() == null || adminBean.getBo() == null ||adminBean.getDomain() == null || !utilityService.fetchDomains(adminBean.getBo()).contains(adminBean.getDomain())) {
			log.debug("Selected Domain should be ");
			map.put("domainError", "Selected Domain should be ");
		}
		if (adminBean.getFinalEmailId() == null || !adminBean.getFinalEmailId().equals("")) {
			log.debug("Selected FinalEmailId should be ");
			map.put("finalEmailIdError", "Selected FinalEmailId should be ");
		}
		if (adminBean.getPrimaryId() == null || !primaryEmail.equals("")) {
			log.debug("Selected PrimaryId should be ");
			map.put("primaryIdError", "Selected PrimaryId should be ");
		}
		if (adminBean.getAliasId() == null || !mailEquiv.equals("")) {
			log.debug("Selected MailEquivalentAddress should be ");
			map.put("aliasIdError", "Selected AliasId should be ");
		}
		if (adminBean.getStatRemarks() == null || !utilityService.dobValidation(adminBean.getStatRemarks()).equals("")) {
			log.debug("Selected StatRemarks should be ");
			map.put("statRemarksError", "Selected StatRemarks should be ");
		}
		
		
		return finalmap;
	}
	
	
	public Boolean createMailUsers(UserForCreate user, String po, String bo, String domain) throws ParseException {
		Boolean status = false;
		status =utilityService.createMailUsers(user, bo, po);
		return status;
	}

	/*
	 * public void createMailUsers(UserForCreate user, String po, String bo, String
	 * domain) throws ParseException {
	 * 
	 * utilityService.sendObj(user, bo, po);
	 * 
	 * }
	 */
	public Boolean createAppUsers(UserForCreateForAppUsers user, String po, String bo, String domain) throws ParseException {
		Boolean status = false;
		status =utilityService.createAppUsers(user, bo, po);
		return status;
	}

	/*
	 * public void createAppUsers(UserForCreateForAppUsers ufcfa, String po, String
	 * bo, String domain) throws ParseException {
	 * 
	 * utilityService.sendObj1(ufcfa, bo, po);
	 * 
	 * }
	 */

	public Boolean isIdCreated(String regNumber, String po, String bo, String domain, String email, String finalId,
			String primaryId, String remarks) throws ParseException {
		Boolean isIdCreated = false;
		UserForCreate userForCreate = new UserForCreate();
		

		String admin_token = "";

		SingleEmailBase singleEmailBase = fetchDetails(regNumber);
		
		//
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Date dt = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String final_date = format.format(dt);
		String desc = "";

		if (singleEmailBase.getDescription() != null && !singleEmailBase.getDescription().equals("")) {
			desc = singleEmailBase.getDescription() + " New ID Zimbra : " + final_date;
		}

		//

		admin_token = "valid";

		String fname = "", lname = "";
		if (!singleEmailBase.getName().equals("")) {
			String[] splited = singleEmailBase.getName().split("\\s+");
			if (splited.length > 0) {
				fname = splited[0];
			}
			if (splited.length > 1) {
				lname = splited[1];
			}
		}

		userForCreate.setCn(fname + " " + lname);
		userForCreate.setDisplayName(singleEmailBase.getName());
		userForCreate.setDescription(desc);
		
		userForCreate.setFirstName(fname);
		userForCreate.setLastName(lname);
		//
		userForCreate.setEmail(primaryId + "@" + domain);
		userForCreate.setAliases(Arrays.asList(finalId + "@nic.in"));
		userForCreate.setIcsCalendar(finalId + "@nic.in");
		//
		userForCreate.setTitle(singleEmailBase.getDesignation());
		userForCreate.setEmployeeCode(singleEmailBase.getEmpCode());
		userForCreate.setMobile(singleEmailBase.getMobile());
		userForCreate.setOfficeAddress(singleEmailBase.getAddress());
		userForCreate.setPassword(fetchRandomPassword());
		userForCreate.setPostingLocation(singleEmailBase.getCity());
		userForCreate.setState(singleEmailBase.getPostingState());//doubt sunny
		userForCreate.setTelephoneNumber(singleEmailBase.getOfficePhone());
		userForCreate.setUsername(singleEmailBase.getPreferredUid1());
		userForCreate.setNicDateOfBirth(singleEmailBase.getDob());
		userForCreate.setNicDateOfRetirement(singleEmailBase.getDor());
		userForCreate.setNicDateOfRetirement(getLDAPModifyDate(singleEmailBase.getDor()));
		userForCreate.setNicAccountExpDate(getLDAPModifyDate(singleEmailBase.getDor()));
		userForCreate.setDepartmentNumber(singleEmailBase.getDepartment() != null ? singleEmailBase.getDepartment() : "");
		userForCreate.setIcsExtendedUserPrefs("ceDefaultAlarmEmail=" + singleEmailBase.getEmail().trim().toLowerCase());

		userForCreate.setDavUniqueId("0342b55f-9fac-4bd9-9624-nic" + timestamp.getTime() + "eforms-"
				+ singleEmailBase.getRegistrationNo());
		userForCreate.setMailforwardingaddress(finalId + "@gov.in.local");
		userForCreate.setMailMessageStore("without-container"); //
		userForCreate.setMailHost("ms22.nic.in"); 
		
		
		if (!admin_token.isEmpty()) {
			if (createMailUsers(userForCreate, po, bo, domain)) {
				
				isIdCreated = true;
			}
		}
		

		return isIdCreated;
	}

	public Boolean isIdAppCreated(String regNumber, String po, String bo, String domain, String email, String finalId,
			String primaryId, String remarks) throws ParseException {
		Boolean isIdAppCreated = false;
		UserForCreateForAppUsers ufcfa = new UserForCreateForAppUsers();
		String admin_token = "";

		SingleEmailBase singleEmailBase = fetchDetails(regNumber);
		//
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Date dt = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String final_date = format.format(dt);
		String desc = "";

		if (singleEmailBase.getDescription() != null && !singleEmailBase.getDescription().equals("")) {
			desc = singleEmailBase.getDescription() + " New ID Zimbra : " + final_date;
		}

		//

		admin_token = "valid";
		String fname = "", lname = "";
		if (!singleEmailBase.getName().equals("")) {
			String[] splited = singleEmailBase.getName().split("\\s+");
			if (splited.length > 0) {
				fname = splited[0];
			}
			if (splited.length > 1) {
				lname = splited[1];
			}
		}

		ufcfa.setCn(fname + " " + lname);
		ufcfa.setDisplayName(singleEmailBase.getName());
		
		ufcfa.setFirstName(fname);
		ufcfa.setLastName(lname);
		//
		ufcfa.setDescription(desc);
		ufcfa.setEmail(primaryId + "@" + domain);
		ufcfa.setAliases(Arrays.asList(finalId + "@nic.in"));
		ufcfa.setIcsCalendar(finalId + "@nic.in");
		ufcfa.setTitle(singleEmailBase.getDesignation());
		//
		ufcfa.setEmployeeCode(singleEmailBase.getEmpCode());
		ufcfa.setMobile(singleEmailBase.getMobile());
		ufcfa.setOfficeAddress(singleEmailBase.getAddress());
		ufcfa.setPassword(fetchRandomPassword());
		ufcfa.setPostingLocation(singleEmailBase.getCity());
		ufcfa.setState(singleEmailBase.getPostingState());
		ufcfa.setTelephoneNumber(singleEmailBase.getOfficePhone());
		ufcfa.setUsername(singleEmailBase.getPreferredUid1());
		ufcfa.setNicDateOfBirth(singleEmailBase.getDob());
		ufcfa.setNicDateOfRetirement(getLDAPModifyDate(singleEmailBase.getDor()));
		ufcfa.setNicAccountExpDate(getLDAPModifyDate(singleEmailBase.getDor()));
		ufcfa.setDepartmentNumber(singleEmailBase.getDepartment() != null ? singleEmailBase.getDepartment() : "");
		ufcfa.setIcsExtendedUserPrefs("ceDefaultAlarmEmail=" + singleEmailBase.getEmail().trim().toLowerCase());
		ufcfa.setDavUniqueId("0342b55f-9fac-4bd9-9624-nic" + timestamp.getTime() + "eforms-"
				+ singleEmailBase.getRegistrationNo());

		if (!admin_token.isEmpty()) {
			if (createAppUsers(ufcfa, po, bo, domain)) {
				
				isIdAppCreated = true;
			}
		}
		

		return isIdAppCreated;
	}

	public ResponseBean rejectAdmin(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by Admin.");
		String formType = "single";
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

	//
	// public Map<String, Object> validateRequest(@Valid @RequestBody AdminBean
	// adminBean,@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp")
	// @NotEmpty String ip,
	// @RequestParam("email") @NotEmpty String email)
//
	
	public ResponseBean approveAdmin(String regNumber, String ip, String po, String bo, String domain, String email,
			String finalId, String primaryId, String remarks, ResponseBean responseBean) throws ParseException {
		responseBean.setRequestType("Completion of request by Admin");
		SingleEmailBase singleEmailBase = fetchDetails(regNumber);

		Boolean isIdCreated = false;
		Boolean isIdAppCreated = false;
		String formType = "single";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		responseBean.setErrors(null);
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
			log.info("mobileAndName::::"+mobileAndName);
		}
		String daEmail = "";
		String recipientType = "";
		String check = singleEmailBase.getType();
		log.info("employement type::::::" + check);
		if (singleEmailBase.getType().equalsIgnoreCase("app")
				|| singleEmailBase.getType().equalsIgnoreCase("eoffice")) {

			isIdCreated = isIdCreated(regNumber, po, bo, domain, email, finalId, primaryId, remarks);
			if (isIdCreated) {
				mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
				status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
						mobileAndName.getName(), "admin");
				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
						mobileAndName.getMobile(), mobileAndName.getName(), "admin", regNumber);
				status.setRegistrationNo(regNumber);
				status.setRecipientType(recipientType);
				status.setStatus(Constants.STATUS_COMPLETED);
				status.setRecipient(daEmail);

				finalAuditTrack.setStatus(Constants.STATUS_COMPLETED);
				finalAuditTrack.setToEmail(daEmail);
			} else {
				log.info("not created :isIdCreated" + isIdCreated);
			}

		} else {

			isIdAppCreated = isIdAppCreated(regNumber, po, bo, domain, email, finalId, primaryId, remarks);
			if (isIdAppCreated)

			{
				mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
				status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
						mobileAndName.getName(), "admin");
				finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
						mobileAndName.getMobile(), mobileAndName.getName(), "admin", regNumber);
				status.setRegistrationNo(regNumber);
				status.setRecipientType(recipientType);
				status.setStatus(Constants.STATUS_COMPLETED);
				status.setRecipient(daEmail);

				finalAuditTrack.setStatus(Constants.STATUS_COMPLETED);
				finalAuditTrack.setToEmail(daEmail);
			} else {
				log.info("not created :isIdAppCreated" + isIdAppCreated);
			}

		}

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
		responseBean.setRequestType("Admin is forwarding the request to DA");
		String adminRemarks = "Forwarded by Admin " + email + " to Delegated Admin.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "single";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		SingleEmailBase singleEmailBase = fetchDetails(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(singleEmailBase, OrganizationBean.class);
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		// LocalDateTime currentTime = null;
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
		responseBean.setRequestType("Admin is reverting the request to support.");
		String adminRemarks = "Reverted by admin " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "single";
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

		// LocalDateTime currentTime = null;
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
