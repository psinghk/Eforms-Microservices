package in.nic.eform.imappop.service;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import in.nic.eform.imappop.bean.UploadMultipleFilesBean;


@Component
public class GlobalCheck {

	public HashMap<String, Object> checkUploadedFiles(UploadMultipleFilesBean uploadfiles){
		HashMap<String, Object> map=new HashMap<>();
		System.out.println(":::::"+uploadfiles.getRole());
		String regex="[A-Z]+-[A-Z]+[0-9]+";
		if(uploadfiles.getRegistrationno()=="")
			map.put("regid", "Registration id cannot be empty");
		else if (!uploadfiles.getRegistrationno().matches(regex)) 
			map.put("regid", "Wrong format Registration id");
		if(uploadfiles.getInfile().get(0).isEmpty()) 
			map.put("file", "you need at least one file to upload");
		
		return map;
	}
	
	public HashMap<String, Object> checkViewDocxParams(String regid,String role) {
		HashMap<String, Object> map=new HashMap<>();
		String regex="[A-Z]+-[A-Z]+[0-9]+";
		if(regid=="")
			map.put("status", "Registration id cannot be empty");
		else if (!regid.matches(regex)) 
			map.put("status", "Wrong format Registration id");
		
		if(role=="")
			map.put("role", "Role Cannot be left blank");
		return map;
		
	}
	
	

	
}
