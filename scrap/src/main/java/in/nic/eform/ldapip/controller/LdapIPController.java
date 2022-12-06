package in.nic.eform.ldapip.controller;

import java.io.IOException;
import java.io.OutputStream;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import in.nic.eform.ldapip.bean.ForwardBean;
import in.nic.eform.ldapip.bean.UploadMultipleFilesBean;
import in.nic.eform.ldapip.bean.LdapIPFormBean;
import in.nic.eform.ldapip.service.LdapIPGlobalCheck;
import in.nic.eform.ldapip.service.LdapIPService;
import in.nic.eform.utility.GetRealIp;
import in.nic.eform.validation.RegistrationNo;
import in.nic.eform.validation.UsernameEmail;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;


@RestController
@Validated
public class LdapIPController {
	private static final Logger log = LoggerFactory.getLogger(LdapIPController.class);
	@Autowired
	GetRealIp getRealIp;
	@Autowired
	LdapIPService ldapIPService;
	@Autowired
	LdapIPGlobalCheck check;
	
	
	//********************************Start of Submission******************************
	//Form submit and open preview
		@RequestMapping(value = "/ldapIPSubmissionPreview")
		public Map<String, Object>  ldapSubmissionPreview(@Valid @RequestBody LdapIPFormBean ldapIPFormBean,BindingResult bindingResult,HttpServletRequest request)  {
			log.info("Enter ldapSubmissionPreview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
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
			 ldapBeanList=ldapIPService.submissionPreview(ldapIPFormBean);
			 log.info("Exit ldapSubmissionPreview  method at info " + new Date() +" ldapBeanList::::"+ldapBeanList);
			return ldapBeanList;
		}
		
//		//to view RO details if we pass form details
		
//		@RequestMapping(value = "/submissionRoDetails")
//		public Map<String, Object>  submissionRoDetails(@Valid @RequestBody SubmissionFormBean submissionBean,BindingResult bindingResult)  {
//			HashMap<String, Object> errors = new HashMap<>();
//			bindingResult.getAllErrors().forEach((error) -> {
//				String fieldName = ((FieldError) error).getField();
//				String errorMessage = error.getDefaultMessage();
//				errors.put(fieldName, errorMessage);
//			});
//			if(!errors.isEmpty()) {
//				return errors ;
//				}
//			 Map<String, Object> ldapBeanList = null;
//			 Map<String, Object> map = new HashMap<String, Object>();
//			 ldapBeanList=submissionService.ldap_tab(submissionBean);
//			return ldapBeanList;
//		}
		
		//to view RO details if form details not pass
			@RequestMapping(value = "/ldapIPSubmissionRoDetails")
			public Map<String, Object>  ldapSubmissionRoDetails(HttpServletRequest request)  {
				log.info("Enter ldapSubmissionRoDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
				 Map<String, Object> ldapBeanList = null;
				 ldapBeanList=ldapIPService.submissionRoDetails();
				 log.info("Exit ldapSubmissionRoDetails method at " + new Date() + " ldapBeanList " +ldapBeanList);
				return ldapBeanList;
			}

		//generate refNO and insert into base tbl	
		@RequestMapping(value = "/ldapIPGenerateRefNo")
		public Map<String, Object>  ldapGenerateRefNo(@Valid @RequestBody LdapIPFormBean ldapIPFormBean,BindingResult bindingResult,HttpServletRequest request)  {
			log.info("Enter  ldapGenerateRefNo method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
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
			 ldapBeanList=ldapIPService.generateRefNo(ldapIPFormBean);
			 log.info("Exit ldapGenerateRefNo method at " + new Date() + " ldapBeanList " +ldapBeanList);
			return ldapBeanList;
		}
		
		
		//complete form submission
		@RequestMapping(value = "/ldapIPFormSubmission")
		public Map<String, Object>  ldapFormSubmission(@RequestParam("regno") @RegistrationNo String regno,@RequestParam("check") String check,@RequestParam("formtype") String formtype, @RequestParam("consent") String consent, @RequestParam("email") String email,HttpServletRequest request)  {
			log.info("Enter  ldapFormSubmission method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
			Map<String, Object> ldapBeanList = null;
			 ldapBeanList=ldapIPService.consent(regno,check,formtype,consent,email);
			 log.info("Exit ldapFormSubmission method at " + new Date() + " ldapBeanList " +ldapBeanList);
			return ldapBeanList;
		}
		
		
	//********************************End of Submission********************************
	
	
	
	
	//********************************Start of Preview(after submission)******************
		@RequestMapping(value = "/ldapIPPreview")
		public Map<String, Object> preview(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty String uemail,HttpServletRequest request) {
			log.info("Enter preview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
					+ request.getRemoteHost());
			Map<String, Object> map = new HashMap<String, Object>();
			 boolean status=ldapIPService.validateRefNo(regno);
			  if(status) {
	       	  map.put("msg", "Invalid Registration Number-("+regno+") ");
	       	return map;
			  }
			 map= ldapIPService.preview(regno,role,uemail);
			 log.info("Exit preview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
						+ request.getRemoteHost()+"  map:::"+map);
			return map;
		}
		
		@RequestMapping(value = "/ldapIPEditPreview")
		public Map<String, Object> EditpreviewDetails(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty String uemail,HttpServletRequest request) {
			 log.info("Enter EditpreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
						+ request.getRemoteHost()); 
			Map<String, Object> map = new HashMap<String, Object>();
			 boolean status=ldapIPService.validateRefNo(regno);
			  if(status) {
	       	  map.put("msg", "Invalid Registration Number-("+regno+") ");
	       	return map;
			  }
			 map= ldapIPService.EditpreviewDetails(regno,role,uemail);
			 log.info("Exit  EditpreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
						+ request.getRemoteHost()+"  map:::"+map); 
			return map;
		}
		
		@RequestMapping(value = "/ldapIPUpdatePreview")
		public Map<String, Object>  UpdatePreviewDetails(@Valid @RequestBody LdapIPFormBean ldapIPFormBean,BindingResult bindingResult,HttpServletRequest request)  {
			 log.info("Enter UpdatePreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
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
			 Map<String, Object> ldapBeanList = null;
			 Map<String, Object> map = new HashMap<String, Object>();
			 ldapBeanList=ldapIPService.UpdatePreviewDetails(ldapIPFormBean);
			 log.info("Exit UpdatePreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
						+ request.getRemoteHost()+"  ldapBeanList:::"+ldapBeanList); 
			return ldapBeanList;
		}
	//********************************End of Preview(after submission)********************
	
	
	
	
	//********************************Start of Approve/Forward******************************
	@RequestMapping(value = "/ldapIPApprove")
	public Map<String, Object> approve(@Valid @RequestBody ForwardBean forwardBean,BindingResult bindingResult,HttpServletRequest request) {
		log.info("Enter approve method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
		HashMap<String, Object> errors = new HashMap<>();
		 Map<String, String> forwardToData = null;
		 Map<String, Object> map = new HashMap<String, Object>();
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		if(!errors.isEmpty()) {
			return errors ;
			}
		 boolean status=ldapIPService.validateRefNo(forwardBean.getRef_num());
		  if(status) {
       	  map.put("msg", "Invalid Registration Number-("+forwardBean.getRef_num()+") ");
       	return map;
		  }
		 forwardToData= ldapIPService.approve(forwardBean);
              if (!forwardToData.get("nextlevel").equals("Completed")) {
            	  map.put("status", "Application Approved and Forwarded Successfully to the " + forwardToData.get("nextlevel") + "(" + forwardToData.get("toemail") + ")");
            	  map.put("Registration No.", forwardBean.getRef_num());
              } 
              else {
            	  map.put("status", "Request Completed Successfully!!");
            	  map.put("Registration No.", forwardBean.getRef_num());
              }
              log.info("Exit approve method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost()+"  map:::"+map);
		return map;
	}
	//********************************End of Approve/Forward********************************
	
	
	
	
	//********************************Start of Reject***************************************
	@RequestMapping(value = "/ldapIPReject")
	public HashMap<String,String> reject(@RequestParam("regno") @NotEmpty @RegistrationNo String regno, @RequestParam("role") @NotBlank String role, @RequestParam("statRemarks") String statRemarks, @RequestParam("check") @NotBlank String check,@RequestParam("email") @NotBlank @Email String email,@RequestParam("mobile") @NotBlank String mobile,@RequestParam("name") @NotBlank String name,HttpServletRequest request) {
		log.info("Enter reject method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());

		HashMap<String,String> map = new HashMap<>(); 
		boolean status = ldapIPService.reject(regno, role, statRemarks, check, email,mobile,name);
		 if(status)
		 {
			 map.put("status", "Application Rejected Successfully !");
			 map.put("Registration No", regno);
		 }else {
			 map.put("status", "Application could not be rejected!!!");
			 map.put("Registration No", regno);
		 }
		 log.info("Exit reject method at " + new Date() + " map " + map);
		 return map;
	}
	//********************************End of Reject*****************************************
	
	
	
	//********************************Start of Track User****************************************

	@RequestMapping(value = "/ldapIPTrackDetails")
	public HashMap<String, Object> fetchTrackDetails(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,HttpServletRequest request) {
		log.info("Enter fetchTrackDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
		HashMap<String, Object> map=ldapIPService.fetchTrackDetails(regno);
		log.info("Exit fetchTrackDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost()+" map:::"+map);
		return map;
	}
	
//	@RequestMapping(value = "/fetchTrackByRole")
//	public Map<String, Object> fetchTrackByRole(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
//			@RequestParam("forward") String forward, @RequestParam("trole") String trole,
//			@RequestParam("srole") String srole) {
//		return trackUserService.fetchTrackByRole(regno, forward, trole, srole);
//	}
	//********************************End of Track User******************************************
	
	
	
	//********************************Start of Raise Query****************************************
	@RequestMapping(value = "/ldapIPRaiseQuery")
	public Map<String, Object> raiseQuery(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("role") @NotEmpty String role,@RequestParam ("uemail") @NotEmpty @UsernameEmail String uemail, @RequestParam ("choose_recp") String choose_recp, @RequestParam ("to_email") @NotEmpty String to_email, @RequestParam ("statRemarks")String statRemarks,HttpServletRequest request){
		log.info("Enter raiseQuery method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());

		Map<String, Object> map = new HashMap<>();
		map=ldapIPService.raiseQuery(regno, role, uemail,choose_recp,to_email,statRemarks);
		log.info("Exit raiseQuery method at " + new Date() + " map:::"+map);
		return map;
	}
	
	
	public ArrayList<String>  fetchRecps(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,@RequestParam("role") @NotEmpty String role,@RequestParam ("uemail") @NotEmpty @UsernameEmail String uemail) {
		log.info("Enter fetchRecps method at " + new Date());
		ArrayList<String> map= new ArrayList<>();
		map=ldapIPService.fetchRecps(regno,role);
		log.info("Exit fetchRecps method at " + new Date() + " map:::"+map);
		return map;
	}
	//********************************End of Raise Query******************************************
	
	
	
	//********************************Start of Put On/Off Hold****************************************
	@RequestMapping(value = "/ldapIPputOnHold")
	public Map<String, Object> putOnHold(@RequestParam("regno") @NotEmpty @RegistrationNo String regno, @RequestParam("role") @NotEmpty String role, @RequestParam("statRemarks") String statRemarks, @RequestParam("type") @NotEmpty String type,@RequestParam("email") @NotEmpty String email,HttpServletRequest request) {
		log.info("Enter putOnHold method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
		Map<String, Object> map = new HashMap<>(); 
		map=ldapIPService.putOnHold(regno, type, role, statRemarks);
		log.info("Exit  putOnHold method at info " + new Date() +" map::::"+map);
		 return map;
	}
	//********************************End of Put On/Off Hold******************************************
	
	
	//********************************Start of Generate PDF******************************
	@RequestMapping(value = "/ldapIPGenerateFormPdf", method = RequestMethod.GET)
	public void generateFormPdf(@RequestParam("regno") @RegistrationNo String regno, HttpServletResponse response) {
		log.info("Enter generateFormPdf method at " + new Date() + " regno:::::::: " +regno);
		JasperPrint jasperPrint = null;
		try {
			jasperPrint = ldapIPService.generatePdf(regno);
			if (jasperPrint != null) {
				OutputStream out = response.getOutputStream();
				//response.setContentType("application/x-download");
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", String.format("inline; filename=\"" + regno + ".pdf\""));
				response.addHeader("status", "success");
				JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			} else {
				response.addHeader("status", "Failed");
			}
			log.info("Exit generateFormPdf method at " + new Date() + " response:::::::: " +response);
		} catch (JRException | IOException e) {
			response.addHeader("status", e.getMessage());
		}
	}
	//********************************End of Generate PDF********************************
	
	
	
	
	//********************************Start of Upload PDF******************************
	@RequestMapping(value = "/ldapIPSavedocx", method = RequestMethod.POST)
	public HashMap<String, Object> saveDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles,
			BindingResult bindingResult,HttpServletRequest request) {
		log.info("Enter saveDocx method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
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
		ArrayList<UploadMultipleFilesBean> status = ldapIPService.saveDocx(uploadfiles);
		for (UploadMultipleFilesBean filebean : status) {
			map.put(filebean.getDoc(), filebean.getStatus());
		}
		log.info("Exit saveDocx method at info " + new Date() +" map::::"+map);
		return map;

	}
	//********************************End of Upload PDF********************************
	
	
	
	
	//********************************Start of Download PDF******************************
	@RequestMapping(value = "/ldapIPDownload")
	public void downloadresource(@RequestParam("filename") @NotEmpty String filename,
			HttpServletRequest request, HttpServletResponse response){
		log.info("Inside downloadresource method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
		response=ldapIPService.downloadFiles(filename, response);
		log.info("Entering downloadresource method at info " + new Date() +" response::::"+response);
	}
	//********************************End of Download PDF********************************
	
	
	
	
	//********************************Start of View PDF******************************
	@RequestMapping(value = "/ldapIPViewDocx", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, Object> viewDocx(@RequestParam("regid") @NotEmpty @RegistrationNo String regid,
			@RequestParam("role") @NotEmpty String role,HttpServletRequest request) {
		log.info("Enter viewDocx method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
		HashMap<String, Object> map = new HashMap<>();
		Map<String, Map<String, String>> status = ldapIPService.viewDocx(regid, role);
		map.put("filestobedownloaded", status);
		log.info("Exit viewDocx method at info " + new Date() +" map::::"+map);
		return map;
	}
	//********************************End of View PDF********************************

}
