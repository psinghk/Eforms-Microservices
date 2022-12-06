package in.nic.eform.imappop.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import in.nic.eform.imappop.bean.ForwardBean;
import in.nic.eform.imappop.bean.PreviewFormBean;
import in.nic.eform.imappop.bean.SubmissionFormBean;
import in.nic.eform.imappop.bean.UploadMultipleFilesBean;
import in.nic.eform.imappop.service.GlobalCheck;
import in.nic.eform.imappop.service.ImappopService;
import in.nic.eform.utility.GetProfileInfo;
import in.nic.eform.utility.GetRealIp;
import in.nic.eform.validation.Regid;
import in.nic.eform.validation.RegistrationNo;
import in.nic.eform.validation.UsernameEmail;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;


@RestController
@Validated
public class ImappopController {
	private static final Logger log = LoggerFactory.getLogger(ImappopController.class);
	@Autowired
	ImappopService imappopService;
	@Autowired
	GetProfileInfo getProfileInfo;
	@Autowired
	GetRealIp getRealIp;
	@Autowired
	GlobalCheck check;
		
		@RequestMapping(value = "/download")
		public void downloadresource(@RequestParam("filename") @NotEmpty String filename,
				HttpServletRequest request, HttpServletResponse response){
			System.out.println("downloading file " + filename);
			response=imappopService.downloadFiles(filename, response);
		}

	
	
	
	
	@RequestMapping(value = "/approve")
	public Map<String, Object> approve(@Valid @RequestBody ForwardBean forwardBean, BindingResult bindingResult) {
		HashMap<String, Object> errors = new HashMap<>();
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		if (!errors.isEmpty()) {
			return errors;
		}
		Map<String, String> forwardToData = null;
		Map<String, Object> map = new HashMap<String, Object>();
		boolean status = imappopService.validateRefNo(forwardBean.getRef_num());
		if (status) {
			map.put("msg", "Invalid Registration Number-(" + forwardBean.getRef_num() + ") ");
			return map;
		}

		System.out.println("status:::::::::" + status);
		forwardToData = imappopService.approve(forwardBean);
		if (!forwardToData.get("nextlevel").equals("Completed")) {
			map.put("msg", "Application Approved and Forwarded Successfully to the " + forwardToData.get("nextlevel")
					+ "(" + forwardToData.get("toemail") + ")");
			map.put("regNo", forwardBean.getRef_num());
		} else {
			map.put("msg", "Status Updated Successfully");
			map.put("regNo", forwardBean.getRef_num());
		}

		return map;
	}
	
	
	
	@RequestMapping(value = "/generatePdf", method = RequestMethod.GET)
	public void generateForm(@RequestParam("regid") @Regid String regid, HttpServletResponse response) throws IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		try {
			jasperPrint = imappopService.generateFormPdf(regid);
			if (jasperPrint != null) {
				OutputStream out = response.getOutputStream();
				//response.setContentType("application/x-download");
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", String.format("inline; filename=\"" + regid + ".pdf\""));
				response.addHeader("status", "success");
				JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			} else {
				response.addHeader("status", "Failed");
			}
		} catch (JRException | IOException e) {
			response.addHeader("status", e.getMessage());
		}
	}
	
	
	

	@RequestMapping(value = "/preview")
	public Map<String, Object> preview(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty String uemail) {
		 Map<String, Object> map = new HashMap<String, Object>();
		 boolean status=imappopService.validateRefNo(regno);
		  if(status) {
       	  map.put("msg", "Invalid Registration Number-("+regno+") ");
       	return map;
		  }
		 map= imappopService.preview(regno,role,uemail);
		return map;
	}
	
	@RequestMapping(value = "/EditPreview")
	public Map<String, Object> EditpreviewDetails(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty String uemail) {
		 Map<String, Object> map = new HashMap<String, Object>();
		 boolean status=imappopService.validateRefNo(regno);
		  if(status) {
       	  map.put("msg", "Invalid Registration Number-("+regno+") ");
       	return map;
		  }
		 map= imappopService.EditpreviewDetails(regno,role,uemail);
		return map;
	}
	
	@RequestMapping(value = "/UpdatePreviewDetails")
	public Map<String, Object>  UpdatePreviewDetails(@Valid @RequestBody PreviewFormBean previewFormBean,BindingResult bindingResult)  {
		HashMap<String, Object> errors = new HashMap<>();
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		if(!errors.isEmpty()) {
			return errors ;
			}
		 Map<String, Object> ldapBeanList = null;
		 Map<String, Object> map = new HashMap<String, Object>();
		 ldapBeanList=imappopService.UpdatePreviewDetails(previewFormBean);
		return ldapBeanList;
	}
	

	@RequestMapping(value = "/putOnHold")
	public Map<String, Object> putOnHold(@RequestParam("regno") @NotEmpty @RegistrationNo String regno, @RequestParam("role") @NotEmpty String role, @RequestParam("statRemarks") String statRemarks, @RequestParam("type") @NotEmpty String type,@RequestParam("email") @NotEmpty String email) {
		Map<String, Object> map = new HashMap<>(); 
		map=imappopService.putOnHold(regno, type, role, statRemarks);
		 return map;
	}
	
	
	@RequestMapping(value = "/raiseQuery")
	public Map<String, Object> raiseQuery(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("role") @NotEmpty String role,@RequestParam ("uemail") @NotEmpty @UsernameEmail String uemail, @RequestParam ("choose_recp") String choose_recp, @RequestParam ("to_email") @NotEmpty String to_email, @RequestParam ("statRemarks")String statRemarks){
		Map<String, Object> map = new HashMap<>();
		map=imappopService.raiseQuery(regno, role, uemail,choose_recp,to_email,statRemarks);
		return map;
	}
	
	
	public ArrayList<String>  fetchRecps(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,@RequestParam("role") @NotEmpty String role,@RequestParam ("uemail") @NotEmpty @UsernameEmail String uemail) {
		ArrayList<String> map= new ArrayList<>();
		map=imappopService.fetchRecps(regno,role);
		return map;
	}
	
	
	

	@RequestMapping(value = "/reject")
	public HashMap<String,String> reject(@RequestParam("regno") @NotEmpty @RegistrationNo String regno, @RequestParam("role") @NotBlank String role, @RequestParam("statRemarks") String statRemarks, @RequestParam("check") @NotBlank String check,@RequestParam("email") @NotBlank @Email String email,@RequestParam("mobile") @NotBlank String mobile,@RequestParam("name") @NotBlank String name) {
		HashMap<String,String> map = new HashMap<>(); 
		boolean status = imappopService.reject(regno, role, statRemarks, check, email,mobile,name);
		 if(status)
		 {
			 map.put("status", "Application Rejected Successfully !");
			 map.put("Registration No", regno);
		 }else {
			 map.put("status", "Application could not be rejected!!!");
			 map.put("Registration No", regno);
		 }
		 return map;
	}
	
	
	
	@RequestMapping(value = "/imappoptab1",consumes = MediaType.APPLICATION_JSON_VALUE)
	public HashMap<String, Object> imappoptab1(@Valid @RequestBody SubmissionFormBean submissionFormBean,BindingResult bindingResult,HttpServletRequest request) throws IllegalAccessException, InvocationTargetException{
		log.trace("Entering imappoptab1 method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		log.debug("Entering imappoptab1 method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		log.info("Entering imappoptab1 method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		log.warn("Entering imappoptab1 method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		log.error("Entering imappoptab1 method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> errors = new HashMap<>();
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);

		});
		if (!errors.isEmpty()) {
			return errors;
		}
		
//		String clientIp = getRealIp.getRealIp(request);
//		System.out.println("hitting from "+clientIp);
		//profile API
		HashMap<String, Object> info = getProfileInfo.getInfo(submissionFormBean.getApplicant_email());
		//EO profile API
		BeanUtils.populate(submissionFormBean, info);
		
		map.put("status", submissionFormBean);
		return map;
		
		
	}
	
	
	@RequestMapping(value = "/imappoptab2",consumes = MediaType.APPLICATION_JSON_VALUE )
	public Map<String, Object>  imappoptab2(@Valid @RequestBody SubmissionFormBean submissionFormBean,BindingResult bindingResult,HttpServletRequest request) throws IllegalAccessException, InvocationTargetException  {
		log.info("Entering imappoptab2 method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		HashMap<String, Object> errors = new HashMap<>();
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		if(!errors.isEmpty()) {
			return errors ;
			}
		String clientIp = getRealIp.getRealIp(request);
		submissionFormBean.setUserip(clientIp);
		System.out.println("hitting from "+clientIp+"---state---"+submissionFormBean.getState());
		Map<String, Object> ldapBeanList = null;
		
		ldapBeanList=imappopService.imappoptab2(submissionFormBean);
		return ldapBeanList;
	}
	
	@RequestMapping(value = "/imappoptab")
	public Map<String, Object>  ldap_tab()  {
		 Map<String, Object> ldapBeanList = null;
		 ldapBeanList=imappopService.imappop();
		return ldapBeanList;
	}
	
	

	
	@RequestMapping(value = "/consent")
	public Map<String, Object>  consent(@RequestParam("regno") @Regid String regno,@RequestParam("check") String check,@RequestParam("formtype") String formtype, @RequestParam("consent") String consent, @RequestParam("email") String email)  {
		 Map<String, Object> imapBeanList = null;
		 System.out.println("Entering controller");
		 Map<String, Object> map = new HashMap<String, Object>();
		 imapBeanList=imappopService.consent(regno,check,formtype,consent,email);
		return imapBeanList;
	}
	
	

	@RequestMapping(value = "/fetchTrackDetails")
	public HashMap<String, Object> fetchTrackDetails(@RequestParam("regno") @NotEmpty @RegistrationNo String regno) {
		return imappopService.fetchTrackDetails(regno);
	}
	
	@RequestMapping(value = "/fetchTrackByRole")
	public Map<String, Object> fetchTrackByRole(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("forward") String forward, @RequestParam("trole") String trole,
			@RequestParam("srole") String srole) {
		return imappopService.fetchTrackByRole(regno, forward, trole, srole);
	}
	
	@RequestMapping(value = "/savedocx", method = RequestMethod.POST)
	public HashMap<String, Object> saveDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles,
			BindingResult bindingResult) {
		HashMap<String, Object> map = new HashMap<>();
		HashMap<String, Object> errors = new HashMap<>();
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		if (!errors.isEmpty()) {
			map.put("errorlist", errors);
			return map;
		}
	HashMap<String, Object> err = check.checkUploadedFiles(uploadfiles);
	if(!err.isEmpty())
	return err;
		ArrayList<UploadMultipleFilesBean> status = imappopService.saveDocx(uploadfiles);
		for (UploadMultipleFilesBean filebean : status) {
			map.put(filebean.getDoc(), filebean.getStatus());
		}
		return map;

	}
	
	@RequestMapping(value = "/viewDocx", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, Object> viewDocx(@RequestParam("regid") @NotEmpty @RegistrationNo String regid,
			@RequestParam("role") @NotEmpty String role) {
		System.out.println("Entering viewDocx Controller---------"+role);
		HashMap<String, Object> map = new HashMap<>();
		Map<String, Map<String, String>> status = imappopService.viewDocx(regid, role);
		map.put("filestobedownloaded", status);
		return map;
	}
}
