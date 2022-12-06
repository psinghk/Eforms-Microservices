package in.nic.eform.controller;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import in.nic.eform.bean.UploadMultipleFilesBean;
import in.nic.eform.customvalidation.Regid;
import in.nic.eform.service.DocsService;
import in.nic.eform.validations.GlobalCheck;

@RestController
@Validated
public class DocsController {
	@Autowired
	GlobalCheck check;
	@Autowired
	DocsService docsService;
	
	
	@PostMapping(path = "/user/uploadDocs")
	public HashMap<String, Object> userUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"user");
		map.put("response", status);
		return map;
	}
	
	
	@PostMapping(path = "/ca/uploadDocs")
	public HashMap<String, Object> caUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"ca");
		map.put("response", status);
		return map;
	}
	
	
	@PostMapping(path = "/support/uploadDocs")
	public HashMap<String, Object> supportUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"support");
		map.put("response", status);
		return map;
	}
	
	
	@PostMapping(path = "/coord/uploadDocs")
	public HashMap<String, Object> coordUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"co");
		map.put("response", status);
		return map;
	}
	
	
	@PostMapping(path = "/admin/uploadDocs")
	public HashMap<String, Object> adminUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"admin");
		map.put("response", status);
		return map;
	}
	
	@GetMapping(path = "/viewDocx")
	public Map<String, Object> viewDocx(@RequestParam("regNumber") @Regid String regNumber,
			@RequestParam("role") @Size(min = 1, message = "Role cannot be empty") String role) {
		Map<String,Object> status = docsService.viewDocx(regNumber, role);
		return status;
	}
	
	
	@PostMapping(value = "/deleteDocx")
	public Map<String, String> deleteDocx(@RequestParam("id") @NotEmpty Long id) {
		Map<String, String> map = docsService.deleteDocx(id);
		return map;
	}
	
	@PostMapping(value = "/updateDocx")
	public HashMap<String, Object> updateDocx(@RequestParam("infile")  MultipartFile infile,@RequestParam("id") @NotEmpty int id) {
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkSingleFiles(infile);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.updateDocx(infile,(long) id);
		for (UploadMultipleFilesBean filebean : status) {
			if(filebean.getStatus().contains("not")) {
				map.put("status",filebean.getStatus());
			}else {
				map.put(filebean.getDoc(), filebean.getStatus());
			}
		}
		return map;
	}
	
	
	@GetMapping(value = "/download")
	public void userDownloadDocs(@RequestParam("filename") @NotEmpty String filename,
			HttpServletRequest request, HttpServletResponse response){
			response=docsService.downloadFiles(filename, response);
	}

}
