package in.nic.ashwini.eForms.services;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


import in.nic.ashwini.eForms.entities.NknBulkEmailBase;
import in.nic.ashwini.eForms.entities.BulkUsers;
import in.nic.ashwini.eForms.models.AdminBean;
import in.nic.ashwini.eForms.models.UserForCreate;

import in.nic.ashwini.eForms.repositories.NknBulkEmailBaseRepo;
import in.nic.ashwini.eForms.repositories.NknBulkUserBaseRepo;
import in.nic.ashwini.eForms.utils.Util;

@Service
public class AdminService {

	@Autowired
	NknBulkEmailBaseRepo nknBulkEmailBaseRepo;
	
	@Autowired
	NknBulkUserBaseRepo nknBulkUserBaseRepo;

	@Autowired
	Util utilityService;

	public void createEmailId(@RequestBody AdminBean adminBean,String registrationNo, String formName, String po, String bo, String domain,
			String email) {
		NknBulkEmailBase nknBulkEmailBase1 = nknBulkEmailBaseRepo.findByRegistrationNo(registrationNo);
		List<BulkUsers> bulkUsers1 = nknBulkUserBaseRepo.findByRegistrationNo(registrationNo);
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
		if (!nknBulkEmailBase1.getName().equals("")) {// auth_off_name= name in NknBulkEmailBase
			String[] splited = nknBulkEmailBase1.getName().split("\\s+");
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

			if (nknBulkEmailBase1.getDescription() != null && !nknBulkEmailBase1.getDescription().equals("")) {
				desc = nknBulkEmailBase1.getDescription() + " New ID Zimbra : " + final_date;
			}

			UserForCreate ufc = new UserForCreate();
			
			ufc.setUsername(bulkUsers.getUid());//need to ask
			ufc.setNicEmployee(utilityService.isNicEmployee(bulkUsers.getMail().trim().toLowerCase()));
			ufc.setPassword(fetchRandomPassword());
			//ufc.setDn("");
			ufc.setInitials("");
			ufc.setFirstName(bulkUsers.getFname()); // givenname=rs.get("fname")//root@123
			ufc.setMiddleName("");//
			ufc.setLastName(bulkUsers.getLname()); // "sn", rs.get("lname")
			ufc.setCn(bulkUsers.getFname() + " " + bulkUsers.getLname()); 
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
			ufc.setMailMessageStore("without-container"); 
			ufc.setIcsCalendar(bulkUsers.getUid() + "@nic.in");
			ufc.setMailHost("ms22.nic.in"); 
			ufc.setDescription(desc);
			ufc.setTitle(bulkUsers.getDesignation());
			ufc.setNicDateOfBirth(nicDateOfBirth);
			ufc.setNicDateOfRetirement(getLDAPModifyDate(bulkUsers.getDor()));
			ufc.setNicAccountExpDate(getLDAPModifyDate(bulkUsers.getDor()));
			ufc.setDepartmentNumber(bulkUsers.getDepartment() != null ? bulkUsers.getDepartment() : "");
			ufc.setIcsExtendedUserPrefs("ceDefaultAlarmEmail=" + bulkUsers.getMail().trim().toLowerCase());

			ufc.setDavUniqueId("0342b55f-9fac-4bd9-9624-nic" + timestamp.getTime() + "eforms-"
					+ nknBulkEmailBase1.getRegistrationNo());

			if (!bulkUsers.getMobile().startsWith("+") && bulkUsers.getMobile().length() == 10) {
				mobilePrefix = "+91";
			}
			
			utilityService.sendObj(ufc, bo, po);
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

	public NknBulkEmailBase preview(String regNo) {
		return nknBulkEmailBaseRepo.findByRegistrationNo(regNo);
	}

	public void approveMethod(AdminBean adminBean, String regNo) {
		NknBulkEmailBase bulkEmailBase = nknBulkEmailBaseRepo.findByRegistrationNo(regNo);
		
		String formType = "nkn_bulk";
		createEmailId(adminBean, regNo, formType, adminBean.getPo(), adminBean.getBo(), adminBean.getDomain(),bulkEmailBase.getEmail());

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
		 
		 //BulkUsers bulkUsers = nknBulkUserBaseRepo.findByRegistrationNo(registrationNo);
		 
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
