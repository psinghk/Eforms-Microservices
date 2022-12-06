package in.nic.eform.validations;

import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import in.nic.eform.bean.UploadMultipleFilesBean;

@Component
public class GlobalCheck {

	public HashMap<String, Object> checkUploadedFiles(UploadMultipleFilesBean uploadfiles){
		HashMap<String, Object> map=new HashMap<>();
		if(uploadfiles.getInfile().get(0).isEmpty()) 
			map.put("file", "you need at least one file to upload");
		return map;
	}
	
	public HashMap<String, Object> checkSingleFiles(MultipartFile file){
		HashMap<String, Object> map=new HashMap<>();
		if(file.isEmpty()) 
			map.put("file", "Please upload file");
		return map;
	}
	

	public HashMap<String, Object> checkCountOfUploadedFiles(UploadMultipleFilesBean uploadfiles){
		HashMap<String, Object> map=new HashMap<>();
		if(uploadfiles.getInfile().size()<=5) { 
			map.put("file", "Can not upload more than 5 files");
		}
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
	
	
//	public static void main(String[] args) {
//		String val="IMAPPOP-FORM202006020001";
//		String regex="[A-Z]+-[A-Z]+[0-9]+";
//		System.out.println(val.matches(regex));
//	}
	
}
