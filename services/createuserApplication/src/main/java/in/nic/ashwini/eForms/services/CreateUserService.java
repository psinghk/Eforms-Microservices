package in.nic.ashwini.eForms.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.dto.UserBean;
import in.nic.ashwini.eForms.entities.Sms;
import in.nic.ashwini.eForms.repositories.SmsRepo;

@Service
public class CreateUserService {

	@Value("${fileBasePath}")
	private String EXTERNAL_FILE_PATH;
	
	@Autowired
	SmsRepo smsrepo;


	public ResponseEntity<Object> getFile() throws FileNotFoundException {
		File f = new File(EXTERNAL_FILE_PATH + "/createuserformat.csv");

		Map<String, Object> map = new HashMap<>();

		if (f.exists()) {

			InputStreamResource file = new InputStreamResource(new FileInputStream(f));
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + f.getName())
					.contentType(MediaType.parseMediaType("application/csv")).body(file);

		} else {
			map.put("status", "File not found");
			return ResponseEntity.ok().body(map);
		}
	}
	
	
	public boolean checkValue(String po,String bo) {
		 
		
		
		return true;
	}
	
	
	public List<Map<String, Object>> validations(UserBean userbean, long recordnumber) {
		List<Map<String, Object>> listerr = new ArrayList<>();
		Map<String, Object> err = new HashMap<>();
		if (!userbean.getFirstName().matches("^[a-zA-Z ]*$")) {
			err.put("firstname", "Entered Firstname not matched in row number " + recordnumber);

		}
		if (!userbean.getLastName().matches("^[a-zA-Z ]*$")) {
			err.put("lastname", "Entered lastname not matched  in row number " + recordnumber);

		}
		if (!userbean.getMobile().matches("^[+0-9]{10,13}$")) {
			err.put("mobile", "Entered mobile number not matched  in row number " + recordnumber);

		}
		if (!userbean.getEmail().toLowerCase().matches("^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$")) {

			err.put("email", "Entered emailid not matched  in row number " + recordnumber);
		}
		listerr.add(err);

		return listerr;
	}
	
	public List<Map<String, Object>> validationsLdap(UserBean userbean, long recordnumber) {
		List<Map<String, Object>> listerr = new ArrayList<>();
		Map<String, Object> err = new HashMap<>();
		//
		listerr.add(err);

		return listerr;
	}
	
	
	

	
	public String messageSms(String bodn ,String mail,String pass,String sendSms,String uid,String mid) {
		
	    String smstemplate = "";
        try {
            StringBuilder sb2 = new StringBuilder();
            if (bodn.contains("railnet.gov.in")) {
                sb2.append("nicmail.railnet.gov.in or mail.gov.in");
                sb2.append("\n");
                sb2.append("Email activation 19.10.15");
                sb2.append("\n");
                sb2.append("Login Id/Email Address ").append(mail);
                sb2.append("\n");
                sb2.append("Passwd ").append(pass);
                sb2.append("\n");
                sb2.append("contact support@gov.in/1800111555");
                sb2.append("NICSI");
                smstemplate = "1107162392833695431";
            } else if (bodn.contains("beenet.in") || bodn.contains("nkn-mailmigration") || bodn.contains("dmrc.org")) { // dmrc added on 30th may 2017
                sb2.append("https://mail.gov.in");
                sb2.append("\n");
                sb2.append("Login Id/Email Address ").append(mail);
                sb2.append("\n");
                sb2.append("Passwd ").append(pass);
                sb2.append("\n");
                sb2.append("contact support@gov.in/1800111555");
                sb2.append("\n");
                sb2.append("NICSI");
                smstemplate = "1107162392866409612";
            } else if (bodn.contains("csir.res.in")) {
                sb2.append("mail.gov.in");
                sb2.append("\n");
                sb2.append("Login Id/Email Address ").append(mail);
                sb2.append("\n");
                sb2.append("Passwd ").append(pass);
                sb2.append("\n");
                sb2.append("contact support@gov.in/1800111555");
                sb2.append("\n");
                sb2.append("NICSI");
                smstemplate = "1107162392862688582";
            } else if (bodn.contains("sail.in") || bodn.contains("Sail.in")) {
               
                sb2.append("Email details of email.gov.in:");
                sb2.append("\n");
                sb2.append("email: ");
                sb2.append(mail);
                sb2.append("\n");
                sb2.append("pwd: ");
                sb2.append(pass);
                sb2.append("\n");
                sb2.append("Assistance: support(at)gov(dot)in, Voice:1800111555");
                sb2.append("\n");
                sb2.append("NICSI");
                smstemplate = "1107162392889167640";
                System.out.println("mohit user password start :::::::::::" + sb2);
            } else {
                sb2.append("Email details of email.gov.in:");
                sb2.append("\n");
                sb2.append("email: ");
                sb2.append(mail);
                sb2.append("\n");
                sb2.append("pwd: ");
                sb2.append(pass);
                sb2.append("\n");
                sb2.append("Assistance: support(at)gov(dot)in, Voice:1800111555");
                sb2.append("\n");
                sb2.append("NICSI");
                smstemplate = "1107162392889167640";
                System.out.println("mohit user password start 963 ceate.java :::::::::::" + sb2);
            }
            System.out.println("mohit user password 965 ceate.java end :::::::::::" + sb2);
            boolean flag = false;
            if (sendSms != null && !sendSms.equals("")) {
                if (sendSms.equals("y")) {
                    flag = true;  
                }
            }

            if (flag) 
            {
            Sms sms=new Sms();
            	sms.setUid(uid);
            	sms.setMid(mid);
            	smsrepo.save(sms);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CREATEUSER: " + "e after message: " + e.getMessage());
        }
		return "";
	}

}
