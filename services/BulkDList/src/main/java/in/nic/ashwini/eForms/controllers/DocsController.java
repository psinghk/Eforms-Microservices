package in.nic.ashwini.eForms.controllers;

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

import in.nic.ashwini.eForms.exceptions.GlobalCheck;
import in.nic.ashwini.eForms.models.UploadMultipleFilesBean;
import in.nic.ashwini.eForms.services.DocsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
public class DocsController {
	@Autowired
	GlobalCheck check;
	@Autowired
	DocsService docsService;
	
	
	@PostMapping(path = "/user/uploadDocs")
	public HashMap<String, Object> userUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		log.info("Doc upload by user {}"+uploadfiles);
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"user");
		for (UploadMultipleFilesBean filebean : status) {
			if(!filebean.getStatus().contains("success") || !filebean.getStatus().contains("failed") ) {
				map.put("status",filebean.getStatus());
				log.info("Status of Doc uploaded by user {}"+status);
			}else {
				map.put(filebean.getDoc(), filebean.getStatus());
				log.info("Status of Doc uploaded by user {}"+status);
			}
		}
		return map;
	}
	
	
	@PostMapping(path = "/ca/uploadDocs")
	public HashMap<String, Object> caUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		log.info("Doc upload by ca {}"+uploadfiles);
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"ca");
		for (UploadMultipleFilesBean filebean : status) {
			if(!filebean.getStatus().contains("success") || !filebean.getStatus().contains("failed") ) {
				map.put("status",filebean.getStatus());
				log.info("Status of Doc uploaded by Reporting Officer {}"+status);
			}else {
				map.put(filebean.getDoc(), filebean.getStatus());
				log.info("Status of Doc uploaded by Reporting Officer {}"+status);
			}
		}
		return map;
	}
	
	
	@PostMapping(path = "/support/uploadDocs")
	public HashMap<String, Object> supportUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		log.info("Doc upload by support {}"+uploadfiles);
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"support");
		for (UploadMultipleFilesBean filebean : status) {
			if(!filebean.getStatus().contains("success") || !filebean.getStatus().contains("failed") ) {
				map.put("status",filebean.getStatus());
				log.info("Status of Doc uploaded by support {}"+status);
			}else {
				map.put(filebean.getDoc(), filebean.getStatus());
				log.info("Status of Doc uploaded by support {}"+status);
			}
		}
		return map;
	}
	
	
	@PostMapping(path = "/coord/uploadDocs")
	public HashMap<String, Object> coordUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		log.info("Doc upload by coord {}"+uploadfiles);
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"co");
		for (UploadMultipleFilesBean filebean : status) {
			if(!filebean.getStatus().contains("success") || !filebean.getStatus().contains("failed") ) {
				map.put("status",filebean.getStatus());
				log.info("Status of Doc uploaded by coordinator {}"+status);
			}else {
				map.put(filebean.getDoc(), filebean.getStatus());
				log.info("Status of Doc uploaded by coordinator {}"+status);
			}
		}
		return map;
	}
	
	
	@PostMapping(path = "/admin/uploadDocs")
	public HashMap<String, Object> adminUploadDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		log.info("Doc upload by admin {}"+uploadfiles);
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.uploadDocx(uploadfiles,"admin");
		for (UploadMultipleFilesBean filebean : status) {
			if(!filebean.getStatus().contains("success") || !filebean.getStatus().contains("failed") ) {
				map.put("status",filebean.getStatus());
				log.info("Status of Doc uploaded by admin {}"+status);
			}else {
				map.put(filebean.getDoc(), filebean.getStatus());
				log.info("Status of Doc uploaded by admin {}"+status);
			}
		}
		return map;
	}
	
	@GetMapping(path = "/viewDocx")
	public HashMap<String, Object> viewDocx(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("role") @Size(min = 1, message = "Role cannot be empty") String role) {
		log.info("Uploaded doc of {}"+regNumber);
		HashMap<String, Object> map = new HashMap<>();
		Map<String, Map<String, String>> status = docsService.viewDocx(regNumber, role);
		map.put("view_files", status);
		return map;
	}
	
	
	@PostMapping(value = "/deleteDocx")
	public Map<String, String> deleteDocx(@RequestParam("id") @NotEmpty Long id) {
		log.info("Delete doc of {}"+id);
		Map<String, String> map = docsService.deleteDocx(id);
		return map;
	}
	
	@PostMapping(value = "/updateDocx")
	public HashMap<String, Object> updateDocx(@RequestParam("infile")  MultipartFile infile,@RequestParam("id") @NotEmpty int id) {
		log.info("Doc update of {}"+id);
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> err = check.checkSingleFiles(infile);
			if(!err.isEmpty())
				return err;
		ArrayList<UploadMultipleFilesBean> status = docsService.updateDocx(infile,(long) id);
		for (UploadMultipleFilesBean filebean : status) {
			if(filebean.getStatus().contains("not")) {
				map.put("status",filebean.getStatus());
				log.info("Status of update doc {}"+status);
			}else {
				map.put(filebean.getDoc(), filebean.getStatus());
				log.info("Status of update doc {}"+status);
			}
		}
		return map;
	}
	
	
	@GetMapping(value = "/download")
	public void userDownloadDocs(@RequestParam("filename") @NotEmpty String filename,
			HttpServletRequest request, HttpServletResponse response){
		    log.info("Download doc of {}"+filename);
			response=docsService.downloadFiles(filename, response);
	}

}
