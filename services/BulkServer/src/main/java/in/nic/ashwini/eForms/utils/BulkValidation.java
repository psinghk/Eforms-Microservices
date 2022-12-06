package in.nic.ashwini.eForms.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.entities.BulkEmailBase;
import in.nic.ashwini.eForms.entities.BulkUsers;
import in.nic.ashwini.eForms.entities.UidCheck;
import in.nic.ashwini.eForms.models.AdminBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.UploadMultipleFilesBean;
import in.nic.ashwini.eForms.repositories.UidCheckRepo;

/*import in.nic.bulk.email.entity.UidCheck;
import in.nic.bulk.email.service.BulkRegistrationService;
import in.nic.bulk.email.service.EmploymentCoordinatorService;
import in.nic.bulk.email.utility.RestApi;*/

@Component
public class BulkValidation {

	@Autowired
	UidCheckRepo uidCheckRepo;

	@Autowired
	Util utilityService;

	// @Autowired
	// AdminBean adminBean;

	BulkEmailBase bulkEmailBase = new BulkEmailBase();

	Connection con = null, conSlave = null;

	HashMap<String, String> values = new HashMap<String, String>();
	String dn_default = "dc=nic,dc=in";

	public HashMap<String, Object> checkUploadedFiles(PreviewFormBean previewFormBean) {
		HashMap<String, Object> map = new HashMap<>();
		if (previewFormBean.getInfile().get(0).isEmpty())
			map.put("file", "you need at least one file to upload");
		return map;
	}

	public boolean isReservedKeyWord(String uid) {
		boolean flag = false;
		Optional<UidCheck> en = uidCheckRepo.findById(uid);
		return en.isPresent();
	}

	public HashMap<String, String> Fname(String fname) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (fname == null || fname.equals("")) {
			errorMsg = "First name can not be blank.";
		} else {
			if (!fname.matches("^[a-z|A-Z|]+[a-z|A-Z|.|\\s]*")) {
				errorMsg = "Enter First name in correct format.";
			} else {
				valid = "true";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	public HashMap<String, String> Lname(String lname) {

		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (lname == null || lname.equals("")) {
			errorMsg = "Last name can not be blank.";
		} else {
			if (!lname.matches("^[a-z|A-Z|]+[a-z|A-Z|.|\\s]*")) {
				errorMsg = "Enter Last name in correct format.";
			} else {
				valid = "true";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	public HashMap<String, String> countrycode(String code) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (code == null || code.equals("")) {
			errorMsg = "Country code can not be blank.";
		} else {
			if (!code.matches("^[0-9]{2,5}$")) {
				errorMsg = "Enter Country code in correct format.";
			} else {
				valid = "true";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	public HashMap<String, String> Designation(String desig) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (desig == null || desig.equals("")) {
			errorMsg = "Designation is blank.";
		} else {
			if (!desig.matches("^[a-zA-Z0-9\\s,.\\-\\/\\(\\)\\&\\_]{2,100}$")) {
				errorMsg = "Enter Designation in correct format.";
				System.out.println("DESIG: " + errorMsg);
			} else {
				valid = "true";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	public HashMap<String, String> Department(String dept) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (dept == null || dept.equals("")) {
			errorMsg = "Department can not be blank.";
		} else {
			if (!dept.matches("^[A-Za-z0-9-&\\s\\_\\.\\-\\,\\&]*{2,100}$")) {
				errorMsg = "Enter Department in correct format.";
			} else {
				valid = "true";
			}
		}
		values.put("valid", valid);//
		values.put("errorMsg", errorMsg);
		return values;
	}

	public HashMap<String, String> State(String state) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (state == null || state.equals("")) {
			errorMsg = "State can not be blank.";
		} else {
			if (!state.matches("^[a-z|A-Z|]+[a-z|A-Z|.|\\s\\&]*")) {
				errorMsg = "Enter State in correct format.";
			} else {
				valid = "true";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	public HashMap<String, String> Mobile(String mobile, String code) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		String mob = "";
		if (mobile == null || mobile.equals("")) {
			errorMsg = "Mobile can not be blank.";
		} else {
			try {
				long mm = Double.valueOf(mobile).longValue();
				mob = "+" + mm + code;
				values.put("mob", mob);
			} catch (Exception e) {
				// all.add("Mobile number is not valid. Where row is:" + count + " and column
				// is:6");
			}
			if (code.equals("+91") && !mob.matches("^[+0-9]{13}$")) {
				errorMsg = "Please enter valid mobile number with country code[e.g: 919999999999], limit[12 digits].";
			} else if (!mob.matches("^[+0-9]{8,15}$") && !code.equals("91")) {
				errorMsg = "Please enter valid mobile number with country code [e.g: 123456789],  limit[8,14 digits].";
			} else {
				valid = "true";
			}
//                if (!mob.matches("^[0-9]{12}$")) {
//                    errorMsg = "Mobile number is not valid.";
//                } else {
//                    valid = "true";
//                }
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	// below function added by pr on 10thsep18

	public HashMap<String, String> MobileAdmin(String mobile) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		String mob = "";
		if (mobile == null || mobile.equals("")) {
			errorMsg = "Mobile can not be blank.";
		} else {
			try {
				System.out.println("mobile::::::::" + mobile);
				if (mobile.startsWith("+")) // as values on admin panel are coming directly from the DB with + appended
				{
					mobile = mobile.substring(1);
				}
				long mm = Double.valueOf(mobile).longValue();
				mob = "+" + mm;
				System.out.println("mob::::::::" + mob);
				values.put("mob", mob);
			} catch (Exception e) {
				// all.add("Mobile number is not valid. Where row is:" + count + " and column
				// is:6");
			}
			if (mobile.startsWith("91") && !mob.matches("^[+0-9]{13}$")) {
				errorMsg = "Please enter valid mobile number [e.g: 919999999999], Max limit[12 digits].";
			} else if (!mob.matches("^[+0-9]{8,15}$") && !mobile.startsWith("+91")) {
				errorMsg = "Please enter valid mobile number [e.g: 123456789], Max limit[14 digits].";
			} else {
				valid = "true";
			}
//                if (!mob.matches("^[0-9]{12}$")) {
//                    errorMsg = "Mobile number is not valid.";
//                } else {
//                    valid = "true";
//                }
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	// DOR validation
	public HashMap<String, String> DOR(String dor) {
		String valid = "false";
		String errorMsg = "";
		String fdor = "";
		values.clear();
		if ((dor.equals("")) || (dor == null)) {
			errorMsg = "Date of Retirement can not be blank.";
		} else {
			try {
				if (dor.matches("[0-9]{2}[-][0-9]{2}[-]([0-9]{4})")) {
					DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					DateFormat formate = new SimpleDateFormat("yyyyMMdd");
					Date date = new Date();
					formatter.setLenient(false);
					String pdate = formatter.format(date);
					Date date1 = formatter.parse(pdate);
					Date date2 = formatter.parse(dor);
					if (!date2.after(date1)) {
						errorMsg = "Date of Retirement Must be greater than present date.";
					} else {
						Date datedr = formatter.parse(dor);
						String date11 = formate.format(datedr);
						fdor = (date11 + "000000Z").trim();
						valid = "true";
						values.put("fdor", fdor);
					}
				} else if (dor.matches("[0-9]{2}[-][a-zA-Z]{2,4}[-]([0-9]{4})")) {
					DateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
					Date datef = format1.parse(dor);
					DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
					String dates = format.format(datef);
					DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					DateFormat formate = new SimpleDateFormat("yyyyMMdd");
					Date date = new Date();
					formatter.setLenient(false);
					String pdate = formatter.format(date);
					Date date1 = formatter.parse(pdate);
					Date date2 = formatter.parse(dates);
					if (!date2.after(date1)) {
						errorMsg = "Date of Retirement Must be greater than present date.";
					} else {
						Date datedr = formatter.parse(dates);
						String date11 = formate.format(datedr);
						fdor = (date11 + "000000Z").trim();
						valid = "true";
						values.put("fdor", fdor);
					}
				} else {
					errorMsg = "Please Enter the Date of Retirement in correct format.";
				}
			} catch (Exception e) {
				errorMsg = "Please Enter the Date of Retirement in correct format.";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	public HashMap<String, String> Uid(String uid, PreviewFormBean previewFormBean) {
		String equivalentmail = uid + "@nic.in";
		// PreviewFormBean previewFormBean=new PreviewFormBean();
		String var1 = previewFormBean.getIdType();
		boolean right = false;
		String valid = "false";
		String errorMsg = "";
		String[] words = uid.split("\\s+");
		if (uid == null || uid.isEmpty()) {
			errorMsg = "Userid can not be blank.";
		} else if (Character.isDigit(uid.charAt(0))) {
			errorMsg = "Userid can not start with a numeric value.";
		} else if (uid.contains("_")) {
			errorMsg = "Userid can not contain underscore[_].";
		} else if (uid.endsWith(".") || uid.endsWith("-") || uid.startsWith(".") || uid.startsWith("-")) {
			errorMsg = "Userid can not start or end with dot[.] and hyphen[-]. ";
		} else if (uid.contains("..") || uid.contains("--")) {
			errorMsg = "Userid can not contain continuous dot[.] or hyphen[-].";
		} else if (uid.length() < 8 || uid.length() > 20) {
			errorMsg = "Userid can not be less than 8 characters or more than 20 characters.";
		} else if (!uid.matches("^[\\w\\-\\.\\+]+$")) {
			errorMsg = "Userid is not in correct format.";
		} else if (words.length > 1) {
			errorMsg = "Userid cannot contain whitespaces.";
		}

		else if (isReservedKeyWord(uid)) {
			System.out.print("valid erro" + isReservedKeyWord(uid));
			errorMsg = "Userid is not allowed.";
		}

		else if (previewFormBean.getIdType().equals("id_name")) {
			System.out.println("INSIDE BULK VALIDATE UID.................................");
			if (!uid.contains(".")) {
				errorMsg = "Userid must contain dot[.] (for Name based account)";
			} /*
				 * else if (isReservedKeyWord(uid)) { errorMsg = "This Userid " + uid +
				 * " is not allowed."; }
				 */ else {
				right = true;
			}
		} else if (bulkEmailBase.getIdType().equals("id_desig")) {
			if (!uid.contains("-")) {
				errorMsg = "Userid must contain hyphen[-] (for Designation/Department based account).";
			} /*
				 * else if (isReservedKeyWord(uid)) { errorMsg = "This Userid " + uid +
				 * " is not allowed."; }
				 */ else {
				right = true;
			}
		} else {
			if (!uid.contains(".") && !uid.contains("-")) {
				errorMsg = "Userid must contain dot[.] or hyphen[-]";
			} else {
				right = true;
			}
		}

		if (right) {
			if (utilityService.uidEmailValidate(uid)) {
				errorMsg = "Login ID (The part before the '@') already exists.";
			} else {
				valid = "true";
			}
		}
		// values.put("newmail", newmail);
		// values.put("checkdbbo", checkdbbo);
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		values.put("equivalentmail", uid + "@nic.in");

		return values;
	}

	public HashMap Mail(String mail, String uid, Set<String> allowedDomain) {
		// List<String> allowedDomain = utilityService.fetchDomains(adminBean.getBo());
		String valid = "false";
		String errorMsg = "";
		String[] words = mail.split("\\s+");
		System.out.println("mail:::::::" + mail);
		if (mail.contains("@")) {
			String[] mle = mail.split("@");
			System.out.println("mleeeeeeeeeeeee" + mle);
			if (mle[0] != null) {
				System.out.println("mle000000000000" + mle[0] + "mle1" + mle[1]);
				String uidGenerated = mle[0];
				String domainGenerated = mle[1];
				uidGenerated = uidGenerated.toLowerCase().trim();
				domainGenerated = domainGenerated.toLowerCase().trim();
				System.out.println("DOMAIN Genrated :: " + domainGenerated);
				System.out.println("Allowed Domains ::: " + allowedDomain + " CHECK :: "
						+ !allowedDomain.contains(domainGenerated));
				try {
					if (mail == null || mail.isEmpty()) {
						errorMsg = "Mail can not be blank.";
					} else if (Character.isDigit(mail.charAt(0))) {
						errorMsg = "Mail cannot start with a numeric value.";
					} else if (mail.startsWith("-") || mail.startsWith(".")) {
						errorMsg = "Mail cannot starts with dot[.] and hyphen[-].";
					} else if (mail.contains("_")) {
						errorMsg = "Mail cannot contain underscore[_].";
					} else if (mail.endsWith(".") || mail.endsWith("-") || mail.startsWith(".")
							|| mail.startsWith("-")) {
						errorMsg = "Mail can not start or end with dot[.] and hyphen[-]. ";
					} else if (mail.contains("..") || mail.contains("--")) {
						errorMsg = "Mail can not contain continuous dot[.] or hyphen[-].";
					} else if (!mail.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
						errorMsg = "Mail is not in correct format.";
					} else if (words.length > 1) {
						errorMsg = "Mail cannot contain whitespaces.";
					} else if (!mail.contains("@")) {
						errorMsg = "Mail is not in correct format.";
					} else if (!allowedDomain.contains(domainGenerated)) {
						errorMsg = "You are not allowed to create the ids for domain (@" + domainGenerated + ")";
					} else {
						try {

							if (isReservedKeyWord(uidGenerated)) {
								errorMsg = "This mail address " + mail + "  not allowed.";
							} else if (domainGenerated.equals("@gov.in") || domainGenerated.equals("@nic.in")) {
								if (!uidGenerated.trim().equals(uid.trim())) {
									errorMsg = "If domain is gov.in then email address (before the @) should be equal to uid"; // Removed
																																// nic.in/
																																// from
																																// the
																																// errorMsg
																																// {

									if (utilityService.allLdapValues(mail) == "") {
										valid = "true";
									} else {
										errorMsg = "Mail already exist.";
									}
								}
							}
						} catch (Exception e) {
							System.out.println(" == " + "e: " + e.getMessage());
							errorMsg = "Some error occurred.";
						}
					}
					values.put("valid", valid);
					values.put("errorMsg", errorMsg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				errorMsg = "Some error occurred.";
				values.put("valid", "false");
				values.put("errorMsg", errorMsg);
			}
		} else {
			errorMsg = "Mail is not in correct format.";
			values.put("valid", "false");
			values.put("errorMsg", errorMsg);
		}
		return values;
	}

	public HashMap<String, String> onlyUID(String uid) {
		System.out.println(" inside onlyuid function uid is " + uid);
		String valid = "false";
		String errorMsg = "";
		boolean right = true;
		if (uid == null || uid.equals("")) {
			System.out.println(" uid not null ");
			right = false;
			errorMsg = "Userid can not be blank.";
		} else if (!(uid + "@nic.in").matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) // else if added
																										// by pr on
																										// 3rdapr18
		{
			right = false;
			errorMsg = "Userid is Invalid.";
		} else if (Character.isDigit(uid.charAt(0))) {
			System.out.println(" uid is digit ");
			right = false;
			errorMsg = "Userid can not start with a numeric value.";
		} else if (uid.contains("_")) {
			System.out.println(" uid contains _ ");
			right = false;
			errorMsg = "Userid can not contain underscore[_].";
		} else if (uid.endsWith(".") || uid.endsWith("-") || uid.startsWith(".") || uid.startsWith("-")) {
			System.out.println(" uid ends with .");
			right = false;
			errorMsg = "Userid can not start or end with dot[.] and hyphen[-]. ";
		} else {
			System.out.println(" uid fine ");
			boolean uflag = false;
			for (int l = 0; l < uid.length(); l++) {
				System.out.println(" inside for ");
				char a = uid.charAt(l);
				if (a == '.') {
					System.out.println(" inside a is dot");
					char b = uid.charAt(l + 1);
					char c = '-';
					if ((a == b) || (b == c)) {
						System.out.println(" inside a=b ");
						uflag = true;
						valid = "false";
					}
				}
				if (a == '-') {
					System.out.println(" inside a is  - ");
					char b = uid.charAt(l + 1);
					char c = '.';
					if ((a == b) || (b == c)) {
						System.out.println(" inside a=b ");
						uflag = true;
						valid = "false";
					}
				}
			}
			System.out.println(" uid fine uflag is " + uflag);
			if (uflag) {
				right = false;
				errorMsg = "Userid contains continuous dot[.] or hyphen[-].";
			}
		}
		if (uid.length() < 8 || uid.length() > 20) {
			System.out.println(" uid length is " + uid.length());
			right = false;
			errorMsg = "Userid can not be less than 8 characters or more than 20 characters.";
		}
		System.out.println(
				" inside onlyuid function at the end valid value is " + valid + " error msg value is " + errorMsg);
		if (right) {
			valid = "true";
		} else {
			valid = "false";
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	// below function added by pr on 9thmar18
	public HashMap<String, String> onlyMail(String mail) {
		System.out.println(" inside onlyuid function mail is " + mail);
		String valid = "false";
		String errorMsg = "";
		boolean rightm = true;
		if (mail == null || mail.equals("")) {
			rightm = false;
			errorMsg = "Mail can not be blank.";
		} else if (!mail.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) // else if added by pr on
																							// 4thapr18
		{
			rightm = false;
			errorMsg = "Mail is Invalid";
		} else if (Character.isDigit(mail.charAt(0))) {
			rightm = false;
			errorMsg = "Mail cannot start with a numeric value.";
		} else if (mail.startsWith("-") || mail.startsWith(".")) {
			rightm = false;
			errorMsg = "Mail cannot starts with dot[.] and hyphen[-].";
		} else if (mail.contains("_")) {
			rightm = false;
			errorMsg = "Mail cannot contain underscore[_].";
		} else {
			for (int i1 = 0; i1 < mail.length(); i1++) {
				if (Character.isWhitespace(mail.charAt(i1))) {
					rightm = false;
					errorMsg = "Mail cannot contain whitespaces.";
				}
			}
		}
		if (rightm) {
			valid = "true";
		} else {
			valid = "false";
		}
		System.out.println(
				" inside onlymail function at the end valid value is " + valid + " error msg value is " + errorMsg);
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	public HashMap<String, String> DOB(String dob) {
		String valid = "false";
		String errorMsg = "";
		String fdob = "";
		values.clear();
		if ((dob.equals("")) || (dob == null)) {
			valid = "true";
			// errorMsg = "Date of Birth can not be blank.";
		} else {
			try {
				if (dob.matches("[0-9]{2}[-][0-9]{2}[-]([0-9]{4})")) {
					Date date = new Date();
					DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					DateFormat formate = new SimpleDateFormat("yyyyMMdd");
					formatter.setLenient(false);
					Calendar c = Calendar.getInstance();
					c.setTime(date);
					c.add(1, -18);
					Date newDate = c.getTime();
					String byear = formatter.format(newDate);
					String pdate = formatter.format(date);
					Date date1 = formatter.parse(pdate);
					Date date2 = formatter.parse(dob);
					Date date3 = formatter.parse(byear);
					if (date2.after(date1)) {
						errorMsg = "Date of Birth cannot be greater than the present date.";
					} else if (date2.after(date3)) {
						errorMsg = "Date of Birth is not Valid.";
					} else {
						Date datedr = formatter.parse(dob);
						fdob = formate.format(datedr);
						fdob = (fdob + "000000Z").trim();
						valid = "true";
						values.put("fdob", fdob);
					}
				} else if (dob.matches("[0-9]{2}[-][a-zA-Z]{2,4}[-]([0-9]{4})")) {
					DateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
					Date datef = format1.parse(dob);
					DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
					String dates = format.format(datef);
					Date date = new Date();
					DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					DateFormat formate = new SimpleDateFormat("yyyyMMdd");
					formatter.setLenient(false);
					Calendar c = Calendar.getInstance();
					c.setTime(date);
					c.add(1, -18);
					Date newDate = c.getTime();
					String byear = formatter.format(newDate);
					String pdate = formatter.format(date);
					Date date1 = formatter.parse(pdate);
					Date date2 = formatter.parse(dates);
					Date date3 = formatter.parse(byear);
					if (date2.after(date1)) {
						errorMsg = "Date of Birth cannot be greater than the present date.";
					} else if (date2.after(date3)) {
						errorMsg = "Date of Birth cannot be greater than the present date.";
					} else {
						Date datedr = formatter.parse(dates);
						fdob = formate.format(datedr);
						valid = "true";
						values.put("fdob", fdob);
					}
				} else {
					errorMsg = "Please Enter the Date of Birth in correct format.";
				}
			} catch (Exception e) {
				errorMsg = "Please Enter the Date of Birth in correct format.";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	// EMPLOYEE NUMBER

	public HashMap<String, String> EMPNUMBER(String empnumber) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (empnumber != null && !empnumber.equals("")) {
			try {
				if (!empnumber.matches("^[a-zA-Z0-9\\s]{2,12}$")) {
					int value = new BigDecimal(empnumber).setScale(0, RoundingMode.HALF_UP).intValue();
					empnumber = Integer.toString(value);
					if (!empnumber.matches("^[0-9]{2,8}$")) {
						errorMsg = "Employee number not in correct format.";
					} else {
						valid = "true";
					}
				} else {
					valid = "true";
				}
			} catch (Exception e) {
				// (please check this sunny)
				// System.out.println(ServletActionContext.getRequest().getSession().getId() + "
				// == " + "Exception in EMPLOYEE NUMBER exxxx ::::::::::::::::" + e);
				errorMsg = "Employee number not in correct format.";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	// Email validation

	public HashMap<String, String> Email(String email) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (email == null || email.equals("")) {
			errorMsg = "Email can not be blank.";
		} else {
			if (!email.matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {
				errorMsg = "Enter Email in correct format.";
			} else {
				valid = "true";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}

	// Address
	public HashMap<String, String> Address(String address) {
		String valid = "false";
		String errorMsg = "";
		values.clear();
		if (address == null || address.equals("")) {
			errorMsg = "Address can not be blank.";
		} else {
			if (!address.matches("^[a-zA-Z#0-9\\s,.\\-\\/\\(\\)]{2,100}$")) {
				errorMsg = "Enter Address in correct format.";
			} else {
				valid = "true";
			}
		}
		values.put("valid", valid);
		values.put("errorMsg", errorMsg);
		return values;
	}
}
