package in.nic.eform.utility;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class GetProfileInfo {
	
public HashMap<String, Object> getInfo(String email) {
		
		HashMap<String, Object> map=new HashMap<>();
		map.put("applicant_email", "satya.nhq@nic.in");
		map.put("applicant_name", "satyabrata sharma");
		map.put("applicant_mobile", "8750239848");
		map.put("min", "Electronics and Information technology");
		map.put("user_employment", "Central");
		map.put("dept", "NIC Support Outsourced");
		map.put("other_dept", "");
		map.put("org", "Ministry of Electronics");
		map.put("hod_email", "tiwari.ashwini@nic.in");
		map.put("hod_name", "Ashwini tiwari");
		map.put("hod_mobile", "8877665566");
		map.put("hod_tel", "");
		map.put("ca_design", "Software Engg");
		map.put("applicant_designation", "Software Developer");
		return map;
		
	}

public HashMap<String, Object> fetchprofile(){
	
	HashMap<String, Object> profileList=new HashMap<String, Object>();
	profileList.put("applicant_name", "Satyabrata Sharma");
	profileList.put("applicant_email", "satya.nhq@nic.in");
	profileList.put("applicant_mobile", "+918750239848");
	profileList.put("applicant_ophone", "011-22902455");
	profileList.put("applicant_rphone", "");
	profileList.put("applicant_designation", "Software Developer");
	profileList.put("applicant_code", "110022");
	profileList.put("applicant_ofcAddess", "NKN 4th Floor Shastri Park NewDelhi");
	profileList.put("applicant_posting_city", "Central");
	profileList.put("applicant_posting_state", "Delhi");
	profileList.put("applicant_pincode", "110031");
	profileList.put("min", "Electronics and Information Technology");
	profileList.put("dept", "NIC Support Outsourced");
	profileList.put("user_employment", "Central");
	profileList.put("other_dept", "");
	profileList.put("org", "");
	profileList.put("state", "Delhi");
	profileList.put("state_dept", "");
	profileList.put("applicant_addstate", "DELHI");
	
	profileList.put("hod_name", "Ms Meenaxi Indolia");
	profileList.put("hod_email", "meenaxi.nhq@nic.in");
	profileList.put("hod_mobile", "+919958910444");
	profileList.put("hod_telephone", "123-12345678");
	profileList.put("hod_designation", "engg");
	
	
	profileList.put("ca_name", "Ms Meenaxi Indolia");
	profileList.put("ca_email", "meenaxi.nhq@nic.in");
	profileList.put("ca_mobile", "+919958910444");
	profileList.put("ca_telephone", "123-12345678");
	profileList.put("ca_design", "engg");
	
	
	profileList.put("under_sec_name", "");
	profileList.put("under_sec_email", "");
	profileList.put("under_sec_mobile", "");
	profileList.put("under_sec_tel", "");
	profileList.put("under_sec_desig", "");
	
	return profileList;
}
	
	
	
	public HashMap<String, Object> fetchprofile1(String mail) {

		HashMap<String, Object> profileList = new HashMap<String, Object>();
		if (mail.equals("shweta.nhq@nic.in")) {
			
			profileList.put("applicant_name", "Shweta Singh");
			profileList.put("applicant_email", "shweta.nhq@nic.in");
			profileList.put("applicant_mobile", "+919810554625");
			profileList.put("applicant_ophone", "011-22902455");
			profileList.put("applicant_rphone", "");
			profileList.put("applicant_designation", "Software Developer");
			profileList.put("applicant_code", "110022");
			profileList.put("applicant_ofcAddess", "NKN 4th Floor Shastri Park NewDelhi");
			profileList.put("applicant_posting_city", "Central");
			profileList.put("applicant_posting_state", "Delhi");
			profileList.put("applicant_pincode", "110031");
			profileList.put("applicant_ministry", "Electronics and Information Technology");
			profileList.put("applicant_department", "NIC Support Outsourced");
			profileList.put("applicant_employment", "Central");
			profileList.put("applicant_other_department", "");
			profileList.put("applicant_organization", "");
			profileList.put("applicant_stateCode", "");
			profileList.put("applicant_state_dept", "");
			profileList.put("applicant_state", "DELHI");

			profileList.put("hod_name", "Ms Meenaxi Indolia");
			profileList.put("hod_email", "meenaxi.nhq@nic.in");
			profileList.put("hod_mobile", "+919958910444");
			profileList.put("hod_telephone", "123-12345678");
			profileList.put("hod_designation", "engg");

			profileList.put("ca_name", "Ms Meenaxi Indolia");
			profileList.put("ca_email", "meenaxi.nhq@nic.in");
			profileList.put("ca_mobile", "+919958910444");
			profileList.put("ca_telephone", "123-12345678");
			profileList.put("ca_designation", "engg");

			profileList.put("under_sec_name", "");
			profileList.put("under_sec_email", "");
			profileList.put("under_sec_mobile", "");
			profileList.put("under_sec_tel", "");
			profileList.put("under_sec_designation", "");
			
		} else if (mail.equals("preeti.nhq@nic.in")) {
			
			profileList.put("applicant_name", "preeti raheja");
			profileList.put("applicant_email", "preeti.nhq@nic.in");
			profileList.put("applicant_mobile", "+919810469060");
			profileList.put("applicant_ophone", "011-24305654");
			profileList.put("applicant_rphone", "");

			profileList.put("applicant_designation", "Ms");
			profileList.put("applicant_code", "110022");
			profileList.put("applicant_ofcAddess", "plot no - 11");
			profileList.put("applicant_posting_city", "Central");
			profileList.put("applicant_posting_state", "DELHI");
			profileList.put("applicant_pincode", "110075");
			profileList.put("applicant_ministry", "Railways,Railnet");
			profileList.put("applicant_department", "Department of Railways,Railnet");
			profileList.put("applicant_employment", "Central");
			profileList.put("applicant_other_department", "");
			profileList.put("applicant_organization", "");
			profileList.put("applicant_stateCode", "");
			profileList.put("applicant_state_dept", "");
			profileList.put("applicant_addstate", "DELHI");

			profileList.put("hod_name", "Ms Meenaxi Indolia");
			profileList.put("hod_email", "meenaxi.nhq@nic.in");
			profileList.put("hod_mobile", "+919958910444");
			profileList.put("hod_telephone", "123-12345678");
			profileList.put("hod_designation", "engg");

			profileList.put("ca_name", "Ms Meenaxi Indolia");
			profileList.put("ca_email", "meenaxi.nhq@nic.in");
			profileList.put("ca_mobile", "+919958910444");
			profileList.put("ca_telephone", "123-12345678");
			profileList.put("ca_designation", "engg");

			profileList.put("under_sec_name", "");
			profileList.put("under_sec_email", "");
			profileList.put("under_sec_mobile", "");
			profileList.put("under_sec_tel", "");
			profileList.put("under_sec_designation", "");
			
		} else if (mail.equals("meenaxi.nhq@nic.in")) {
			profileList.put("applicant_name", "Ms Meenaxi Indolia");
			profileList.put("applicant_email", "meenaxi.nhq@nic.in");
			profileList.put("applicant_mobile", "+919958910444");
			profileList.put("applicant_ophone", "123-12345678");
			profileList.put("applicant_rphone", "");

			profileList.put("applicant_designation", "Software Developer");
			profileList.put("applicant_code", "110022");
			profileList.put("applicant_ofcAddess", "cgo complex new delhi");
			profileList.put("applicant_posting_city", "East");
			profileList.put("applicant_posting_state", "DELHI");
			profileList.put("applicant_pincode", "110075");
			profileList.put("applicant_ministry", "Electronics and Information Technology");
			profileList.put("applicant_department", "National Informatics Centre");
			profileList.put("applicant_employment", "Central");
			profileList.put("applicant_other_department", "");
			profileList.put("applicant_organization", "");
			profileList.put("applicant_stateCode", "");
			profileList.put("applicant_state_dept", "");
			profileList.put("applicant_addstate", "DELHI");

			profileList.put("hod_name", "Mr Ashwini Kumar Tiwari");
			profileList.put("hod_email", "tiwari.ashwini@nic.in");
			profileList.put("hod_mobile", "+919953126961");
			profileList.put("hod_telephone", "123-12345678");
			profileList.put("hod_designation", "Scientist-C");

			profileList.put("ca_name", "Mr Ashwini Kumar Tiwari");
			profileList.put("ca_email", "tiwari.ashwini@nic.in");
			profileList.put("ca_mobile", "+919953126961");
			profileList.put("ca_telephone", "123-12345678");
			profileList.put("ca_designation", "Scientist-C");

			profileList.put("under_sec_name", "");
			profileList.put("under_sec_email", "");
			profileList.put("under_sec_mobile", "");
			profileList.put("under_sec_tel", "");
			profileList.put("under_sec_designation", "");
			
		} else if (mail.equals("tiwari.ashwini@nic.in")) {
			profileList.put("applicant_name", "Mr Ashwini Tiwari");
			profileList.put("applicant_email", "tiwari.ashwini@nic.in");
			profileList.put("applicant_mobile", "+919953126961");
			profileList.put("applicant_ophone", "011-24305839");
			profileList.put("applicant_rphone", "011-24305839");

			profileList.put("applicant_designation", "SO");
			profileList.put("applicant_code", "6110");
			profileList.put("applicant_ofcAddess", "CGO COMPLEX");
			profileList.put("applicant_posting_city", "Shimla");
			profileList.put("applicant_posting_state", "HIMACHAL PRADESH");
			profileList.put("applicant_pincode", "171002");
			profileList.put("applicant_ministry", "Earth and Sciences");
			profileList.put("applicant_department", "Indian Metrological Department");
			profileList.put("applicant_employment", "Central");
			profileList.put("applicant_other_department", "");
			profileList.put("applicant_organization", "");
			profileList.put("applicant_stateCode", "");
			profileList.put("applicant_state_dept", "");
			profileList.put("applicant_addstate", "DELHI");

			profileList.put("hod_name", "Mr Gyan Prakash Gupta");
			profileList.put("hod_email", "gyan.prakashgupta@nic.in");
			profileList.put("hod_mobile", "+919993669650");
			profileList.put("hod_telephone", "123-12345678");
			profileList.put("hod_designation", "Technical Assistant A");

			profileList.put("ca_name", "Mr Gyan Prakash Gupta");
			profileList.put("ca_email", "gyan.prakashgupta@nic.in");
			profileList.put("ca_mobile", "+919993669650");
			profileList.put("ca_telephone", "123-12345678");
			profileList.put("ca_designation", "Technical Assistant A");

			profileList.put("under_sec_name", "");
			profileList.put("under_sec_email", "");
			profileList.put("under_sec_mobile", "");
			profileList.put("under_sec_tel", "");
			profileList.put("under_sec_designation", "");
		}
		return profileList;
	}

}
