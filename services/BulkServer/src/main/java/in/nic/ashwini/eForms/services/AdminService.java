package in.nic.ashwini.eForms.services;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.naming.directory.SearchControls;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import in.nic.ashwini.eForms.entities.DocUpload;
import in.nic.ashwini.eForms.entities.BulkEmailBase;
import in.nic.ashwini.eForms.entities.BulkUsers;
import in.nic.ashwini.eForms.models.AdminBean;
import in.nic.ashwini.eForms.models.UserForCreate;
import in.nic.ashwini.eForms.models.UserForCreateForAppUsers;
import in.nic.ashwini.eForms.repositories.BulkEmailBaseRepo;
import in.nic.ashwini.eForms.repositories.BulkUserBaseRepo;
import in.nic.ashwini.eForms.utils.Util;

@Service
public class AdminService {

	@Autowired
	BulkEmailBaseRepo bulkEmailBaseRepo;
	
	@Autowired
	BulkUserBaseRepo bulkUserBaseRepo;

	@Autowired
	Util utilityService;

	public void createEmailId(@RequestBody AdminBean adminBean,String registrationNo, String formName, String po, String bo, String domain,
			String email) {
		BulkEmailBase bulkEmailBase1 = bulkEmailBaseRepo.findByRegistrationNo(registrationNo);
		List<BulkUsers> bulkUsers1 = bulkUserBaseRepo.findByRegistrationNo(registrationNo);
		for (BulkUsers bulkUsers : bulkUsers1) {
			
		String mobilePrefix = "";
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		
		String nicDateOfBirth = "";
		if ((bulkUsers.getDob() != null) && (!bulkUsers.getDob().trim().equals(""))) {
			String dob1 = getLDAPModifyDate(bulkUsers.getDob().trim());

			nicDateOfBirth = dob1;
		}

		String fname = "";
		String lname = "";
		if (!bulkEmailBase1.getName().equals("")) {// auth_off_name= name in BulkEmailBase
			String[] splited = bulkEmailBase1.getName().split("\\s+");
			if (splited.length > 0) {
				fname = splited[0];
			}
			if (splited.length > 1) {
				lname = splited[1];
			}

			Date dt = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String final_date = format.format(dt);
			String desc = "";

			if (bulkEmailBase1.getDescription() != null && !bulkEmailBase1.getDescription().equals("")) {
				desc = bulkEmailBase1.getDescription() + " New ID Zimbra : " + final_date;
			}

			
			UserForCreate ufc = new UserForCreate();
			//UserForCreateForAppUsers ufcfa= new UserForCreateForAppUsers();
			ufc.setUsername(bulkUsers.getUid());//need to ask
			ufc.setNicEmployee(utilityService.isNicEmployee(bulkUsers.getMail().trim().toLowerCase()));
			ufc.setPassword(fetchRandomPassword());
			//ufc.setDn("");
			ufc.setInitials("");
			ufc.setFirstName(bulkUsers.getFname()); // givenname=rs.get("fname")//root@123
			ufc.setMiddleName("");//
			ufc.setLastName(bulkUsers.getLname()); // "sn", rs.get("lname")
			ufc.setCn(bulkUsers.getFname() + " " + bulkUsers.getLname()); // "cn", rs.get("fname") + " " + rs.get("lname"));
			ufc.setDisplayName("");
			//ufc.setEmail(adminBean.getFinalEmailId().trim().toLowerCase());
			ufc.setEmail(bulkUsers.getUid() + "@" + adminBean.getDomain());//need to ask
			ufc.setAliases(Arrays.asList(bulkUsers.getUid() + "@nic.in")); // mailequivalentaddress =uid + "@nic.in"
			ufc.setMobile(mobilePrefix + bulkUsers.getMobile());
			ufc.setPostingLocation("");
			ufc.setTelephoneNumber("");
			ufc.setOfficeAddress("");
			ufc.setHomePhone("");
			ufc.setState(bulkUsers.getState());
			ufc.setEmployeeCode("");// not =employeeNumber

			ufc.setMailforwardingaddress(bulkUsers.getUid() + "@gov.in.local");
			ufc.setMailMessageStore("without-container"); // hardcoded in netbeans
			ufc.setIcsCalendar(bulkUsers.getUid() + "@nic.in");
			ufc.setMailHost("ms22.nic.in"); // hardcoded in netbeans
			ufc.setDescription(desc);
			ufc.setTitle(bulkUsers.getDesignation());
			ufc.setNicDateOfBirth(nicDateOfBirth);
			ufc.setNicDateOfRetirement(getLDAPModifyDate(bulkUsers.getDor()));
			ufc.setNicAccountExpDate(getLDAPModifyDate(bulkUsers.getDor()));
			ufc.setDepartmentNumber(bulkUsers.getDepartment() != null ? bulkUsers.getDepartment() : "");
			ufc.setIcsExtendedUserPrefs("ceDefaultAlarmEmail=" + bulkUsers.getMail().trim().toLowerCase());

			ufc.setDavUniqueId("0342b55f-9fac-4bd9-9624-nic" + timestamp.getTime() + "eforms-"
					+ bulkEmailBase1.getRegistrationNo());

			if (!bulkUsers.getMobile().startsWith("+") && bulkUsers.getMobile().length() == 10) {
				mobilePrefix = "+91";
			}
			/*
			 * ufcfa.setUsername(utilityService.findUid(bulkUsers.getUid()));//
			 * ufcfa.setNicEmployee(utilityService.isNicEmployee(bulkUsers.getMail().trim().
			 * toLowerCase())); ufcfa.setPassword(fetchRandomPassword()); //ufcfa.setDn("");
			 * ufcfa.setInitials(""); ufcfa.setFirstName(bulkUsers.getFname()); //
			 * givenname=rs.get("fname") ufcfa.setMiddleName("");//
			 * ufcfa.setLastName(bulkUsers.getLname()); ufcfa.setDisplayName("");
			 * ufcfa.setCn(bulkUsers.getFname() + " " + bulkUsers.getLname()); // "cn",
			 * rs.get("fname") + " " + rs.get("lname"));
			 * //ufcfa.setEmail(email.trim().toLowerCase());
			 * ufcfa.setEmail(adminBean.getPrimaryId() + "@" + adminBean.getDomain());
			 * ufcfa.setAliases(Arrays.asList(bulkUsers.getUid() + "@nic.in")); //
			 * mailequivalentaddress =uid + "@nic.in" ufcfa.setMobile(mobilePrefix +
			 * bulkUsers.getMobile()); ufcfa.setPostingLocation("");
			 * ufcfa.setTelephoneNumber(""); ufcfa.setOfficeAddress("");
			 * ufcfa.setHomePhone(""); ufcfa.setState(bulkUsers.getState());
			 * ufcfa.setEmployeeCode("");// not =employeeNumber
			 * 
			 * ufcfa.setIcsCalendar(bulkUsers.getUid() + "@nic.in");
			 * ufcfa.setDescription(desc); ufcfa.setTitle(bulkUsers.getDesignation());
			 * ufcfa.setNicDateOfBirth(nicDateOfBirth);
			 * ufcfa.setNicDateOfRetirement(getLDAPModifyDate(bulkUsers.getDor()));
			 * ufcfa.setNicAccountExpDate(getLDAPModifyDate(bulkUsers.getDor()));
			 * ufcfa.setDepartmentNumber(bulkUsers.getDepartment() != null ?
			 * bulkUsers.getDepartment() : "");
			 * ufcfa.setIcsExtendedUserPrefs("ceDefaultAlarmEmail=" +
			 * bulkUsers.getMail().trim().toLowerCase());
			 * ufcfa.setDavUniqueId("0342b55f-9fac-4bd9-9624-nic" + timestamp.getTime() +
			 * "eforms-" + bulkEmailBase1.getRegistrationNo());
			 */
			utilityService.sendObj(ufc, bo, po);
			}
		}
	}
	public void createAppId(@RequestBody AdminBean adminBean,String registrationNo, String formName, String po, String bo, String domain,
			String email) {
		BulkEmailBase bulkEmailBase1 = bulkEmailBaseRepo.findByRegistrationNo(registrationNo);
		List<BulkUsers> bulkUsers1 = bulkUserBaseRepo.findByRegistrationNo(registrationNo);
		for (BulkUsers bulkUsers : bulkUsers1) {
			
		String mobilePrefix = "";
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		
		String nicDateOfBirth = "";
		if ((bulkUsers.getDob() != null) && (!bulkUsers.getDob().trim().equals(""))) {
			String dob1 = getLDAPModifyDate(bulkUsers.getDob().trim());

			nicDateOfBirth = dob1;
		}

		String fname = "";
		String lname = "";
		if (!bulkEmailBase1.getName().equals("")) {// auth_off_name= name in BulkEmailBase
			String[] splited = bulkEmailBase1.getName().split("\\s+");
			if (splited.length > 0) {
				fname = splited[0];
			}
			if (splited.length > 1) {
				lname = splited[1];
			}

			Date dt = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String final_date = format.format(dt);
			String desc = "";

			if (bulkEmailBase1.getDescription() != null && !bulkEmailBase1.getDescription().equals("")) {
				desc = bulkEmailBase1.getDescription() + " New ID Zimbra : " + final_date;
			}

			
			
			UserForCreateForAppUsers ufcfa= new UserForCreateForAppUsers();
			if (!bulkUsers.getMobile().startsWith("+") && bulkUsers.getMobile().length() == 10) {
				mobilePrefix = "+91";
			}
			ufcfa.setUsername(bulkUsers.getUid());//
			ufcfa.setNicEmployee(utilityService.isNicEmployee(bulkUsers.getMail().trim().toLowerCase()));
			ufcfa.setPassword(fetchRandomPassword());
			//ufcfa.setDn("");
			ufcfa.setInitials("");
			ufcfa.setFirstName(bulkUsers.getFname()); // givenname=rs.get("fname")
			ufcfa.setMiddleName("");//
			ufcfa.setLastName(bulkUsers.getLname());
			ufcfa.setDisplayName("");
			ufcfa.setCn(bulkUsers.getFname() + " " + bulkUsers.getLname()); // "cn", rs.get("fname") + " " + rs.get("lname"));
			//ufcfa.setEmail(email.trim().toLowerCase());
			ufcfa.setEmail(bulkUsers.getUid() + "@" + adminBean.getDomain());//need to ask
			ufcfa.setAliases(Arrays.asList(bulkUsers.getUid() + "@nic.in")); // mailequivalentaddress =uid + "@nic.in"
			ufcfa.setMobile(mobilePrefix + bulkUsers.getMobile());
			ufcfa.setPostingLocation("");
			ufcfa.setTelephoneNumber("");
			ufcfa.setOfficeAddress("");
			ufcfa.setHomePhone("");
			ufcfa.setState(bulkUsers.getState());
			ufcfa.setEmployeeCode("");// not =employeeNumber
			
			ufcfa.setIcsCalendar(bulkUsers.getUid() + "@nic.in");
			ufcfa.setDescription(desc);
			ufcfa.setTitle(bulkUsers.getDesignation());
			ufcfa.setNicDateOfBirth(nicDateOfBirth);
			ufcfa.setNicDateOfRetirement(getLDAPModifyDate(bulkUsers.getDor()));
			ufcfa.setNicAccountExpDate(getLDAPModifyDate(bulkUsers.getDor()));
			ufcfa.setDepartmentNumber(bulkUsers.getDepartment() != null ? bulkUsers.getDepartment() : "");
			ufcfa.setIcsExtendedUserPrefs("ceDefaultAlarmEmail=" + bulkUsers.getMail().trim().toLowerCase());
			ufcfa.setDavUniqueId("0342b55f-9fac-4bd9-9624-nic" + timestamp.getTime() + "eforms-"
					+ bulkEmailBase1.getRegistrationNo());
			utilityService.sendObj1(ufcfa, bo, po);
			}
		}
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

	public BulkEmailBase preview(String regNo) {
		return bulkEmailBaseRepo.findByRegistrationNo(regNo);
	}

	public void approveMethod(AdminBean adminBean, String regNo) {
		BulkEmailBase bulkEmailBase = bulkEmailBaseRepo.findByRegistrationNo(regNo);
		UserForCreateForAppUsers user = new UserForCreateForAppUsers();
		
		List<String> domain = utilityService.fetchDomains(adminBean.getDomain());
		String reg_no = bulkEmailBase.getRegistrationNo();
		
		String pass = fetchRandomPassword();
		String formType = "bulk"; // BULK_FORM_KEYWORD = "bulk"
		String formName = formType;
		String description = adminBean.getDescription();

		
		

		Boolean createAppID = false;
		if (formType.equals("bulk")) {

			if (bulkEmailBase.getType().equalsIgnoreCase("app")
					|| bulkEmailBase.getType().equalsIgnoreCase("eoffice")) {
				createAppID = true;
			}

		}

		if (createAppID) {
			
			
			createAppId(adminBean, regNo, formName, adminBean.getPo(), adminBean.getBo(), adminBean.getDomain(),bulkEmailBase.getEmail());
			/*
			 * if (createAppId(null, formName, po, bo, domain, final_sms_id, primaryEmail,
			 * reg_no, pass, "", "")) // api {
			 * 
			 * @NotBlank String parentBo;
			 * 
			 * utilityService.createAppForUsers(user, bo, parentBo); } createAppForUsers
			 * Boolean r = fwdObj.updateStatus(reg_no, "completed", formName, "", "", "",
			 * primaryEmail); // line modified primaryEmail added by pr on 16thjan19
			 * 
			 * statusUpdated = r;
			 * 
			 * if (r) { Inform infObj = new Inform();
			 * 
			 * infObj.sendCompIntimation(reg_no, formName, primaryEmail, pass); // line
			 * modified by pr on 19thfeb18
			 * 
			 * 
			 * updateFinalID(primaryEmail, reg_no, formName, description); // line modified
			 * by pr on 19thfeb18///updte finalid in singleregistriaont able isSuccess =
			 * true; isError = false; msg = " Email ID Created with UID: " + final_sms_id +
			 * ", Primary Email : " + primaryEmail + ", MailequivalentAddress  " +
			 * mailEquiv; } else { isSuccess = false; isError = true; msg =
			 * " Email ID Created with UID: " + final_sms_id + ", Primary Email : " +
			 * primaryEmail + ", MailequivalentAddress  " + mailEquiv +
			 * ".However, Status could not be updated."; } }else {
			 * 
			 * po = ""; bo = ""; domain = ""; final_sms_id = ""; // end, code added by pr on
			 * 7thfeb18 isSuccess = false; isError = true; msg =
			 * "UID could not be created : " + final_sms_id; }
			 * 
			 * 
			 * return null;
			 */
		} else {
			createEmailId(adminBean, regNo, formName, adminBean.getPo(), adminBean.getBo(), adminBean.getDomain(),bulkEmailBase.getEmail());
		}

	}

	// below function is used to create a random password
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
			// randomChar.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(randomNum.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZ".length()
			// - 1)));
			randomChar.append(
					"ABCDEFGHJKMNOPQRSTUVWXYZ".charAt(randomNum.nextInt("ABCDEFGHJKMNOPQRSTUVWXYZ".length() - 1)));// line
																													// modified
																													// by
																													// pr
																													// on
																													// 23rdjul18
			break;
		case 1:
			randomChar.append("0123456789".charAt(randomNum.nextInt("0123456789".length() - 1)));
			break;
		case 2:
			randomChar.append("@#$%&*".charAt(randomNum.nextInt("@#$%&*".length() - 1)));
			break;
		case 3:
			// randomChar.append("abcdefghijklmnopqrstuvwxyz".charAt(randomNum.nextInt("abcdefghijklmnopqrstuvwxyz".length()
			// - 1)));
			randomChar.append(
					"abcdefghjkmnopqrstuvwxyz".charAt(randomNum.nextInt("abcdefghjkmnopqrstuvwxyz".length() - 1)));// line
																													// modified
																													// by
																													// pr
																													// on
																													// 23rdjul18
		}
		return randomChar.toString();
	}
	 public String fetchBulkUsers(String registrationNo, String formName, String po, String bo, String domain,
				String email)
	 {
		 
		 //BulkUsers bulkUsers = bulkUserBaseRepo.findByRegistrationNo(registrationNo);
		 
					return null;  
	 }
	 public Boolean checkBulkAndUpdateStatus(String registrationNo, String formName) 
	 {
		return null;
		 
	 }
	 public Boolean fetchBulkStatusCompleted(String registrationNo)
	 {
		return null;
		 
	 }
	 public HashMap fetchBulkUserDetails(String bulk_id)
	 {
		return null;
		 
	 }
	 public ArrayList fetchBulkUsersAccToType(String registrationNo, String type)
	 {
		return null;
		 
	 }
	 public String rejectBulkID()
	 {
		return null;
		 
	 }
	 
	 public String createBulkID() 
	 {
		return null;
		 
	 }
	 //fwd
	 public HashMap fetchBulkDetail(String bulk_id)
	 {
		return null;
		 
	 }

}
