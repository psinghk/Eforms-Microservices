package in.nic.ashwini.eForms.exceptions;

import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import in.nic.ashwini.eForms.models.UploadMultipleFilesBean;

@Component
public class GlobalCheck {

	public HashMap<String, Object> checkUploadedFiles(UploadMultipleFilesBean uploadfiles){
		HashMap<String, Object> map=new HashMap<>();
		if(uploadfiles.getInfile().get(0).isEmpty()) 
			map.put("Error", "you need at least one file to upload");
		return map;
	}
	
	public HashMap<String, Object> checkSingleFiles(MultipartFile file){
		HashMap<String, Object> map=new HashMap<>();
		if(file.isEmpty()) 
			map.put("Error", "Please upload file");
		return map;
	}
	

	public HashMap<String, Object> checkCountOfUploadedFiles(UploadMultipleFilesBean uploadfiles){
		HashMap<String, Object> map=new HashMap<>();
		if(uploadfiles.getInfile().size()<=1) { 
			map.put("Error", "Can not upload more than 1 file");
		}
		return map;
	}
	
	public HashMap<String, Object> checkViewDocxParams(String regid,String role) {
		HashMap<String, Object> map=new HashMap<>();
		String regex="[A-Z]+-[A-Z]+[0-9]+";
		if(regid=="")
			map.put("Error", "Registration id cannot be empty");
		else if (!regid.matches(regex)) 
			map.put("Error", "Wrong format Registration id");
		if(role=="")
			map.put("Error", "Role Cannot be left blank");
		return map;
	}
}
