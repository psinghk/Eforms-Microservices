package in.nic.eform.relayip.controller;

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
import in.nic.eform.relayip.bean.ForwardBean;
import in.nic.eform.relayip.bean.RelayIPFormBean;
import in.nic.eform.relayip.bean.UploadMultipleFilesBean;
import in.nic.eform.relayip.service.RelayIPGlobalCheck;
import in.nic.eform.relayip.service.RelayIPService;
import in.nic.eform.utility.GetRealIp;
import in.nic.eform.validation.RegistrationNo;
import in.nic.eform.validation.UsernameEmail;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@RestController
@Validated
public class RelayIPController {
	private static final Logger log = LoggerFactory.getLogger(RelayIPController.class);
	@Autowired
	GetRealIp getRealIp;
	@Autowired
	RelayIPService relayIPService;
	@Autowired
	RelayIPGlobalCheck check;
	
	
	//********************************Start of Submission******************************
	//Form submit and open preview
		@RequestMapping(value = "/relayIPsubmissionPreview")
		public Map<String, Object>  relaysubmissionPreview(@Valid @RequestBody RelayIPFormBean relayIPFormBean,BindingResult bindingResult,HttpServletRequest request)  {
			log.info("Enter relaysubmissionPreview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
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
			 ldapBeanList=relayIPService.submissionPreview(relayIPFormBean);
			 log.info("Exit relaysubmissionPreview method at info " + new Date() +" ldapBeanList::::"+ldapBeanList);
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
			@RequestMapping(value = "/relayIPSubmissionRoDetails")
			public Map<String, Object>  relaySubmissionRoDetails(HttpServletRequest request)  {
				log.info("Enter relaySubmissionRoDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
				 Map<String, Object> ldapBeanList = null;
				 ldapBeanList=relayIPService.submissionRoDetails();
				 log.info("Exit relaySubmissionRoDetails method at info " + new Date() +" ldapBeanList::::"+ldapBeanList);
				return ldapBeanList;
			}

		//generate refNO and insert into base tbl	
		@RequestMapping(value = "/relayIPGenerateRefNo")
		public Map<String, Object>  relayGenerateRefNo(@Valid @RequestBody RelayIPFormBean relayIPFormBean,BindingResult bindingResult,HttpServletRequest request)  {
			log.info("Enter relayGenerateRefNo method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
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
			 ldapBeanList=relayIPService.generateRefNo(relayIPFormBean);
			 log.info("Exit relayGenerateRefNo method at info " + new Date() +" ldapBeanList::::"+ldapBeanList);
			return ldapBeanList;
		}
		
		
		//complete form submission
		@RequestMapping(value = "/relayIPFormSubmission")
		public Map<String, Object>  relayFormSubmission(@RequestParam("regno") @RegistrationNo String regno,@RequestParam("check") String check,@RequestParam("formtype") String formtype, @RequestParam("consent") String consent, @RequestParam("email") String email,HttpServletRequest request)  {
			log.info("Enter relayFormSubmission method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost()); 
			Map<String, Object> ldapBeanList = null;
			 ldapBeanList=relayIPService.consent(regno,check,formtype,consent,email);
			log.info("Exit relayFormSubmission method at info " + new Date() +" ldapBeanList::::"+ldapBeanList);
			return ldapBeanList;
		}
	//********************************End of Submission********************************
	
	
	
	
	//********************************Start of Preview(after submission)******************
		@RequestMapping(value = "/relayIPPreview")
		public Map<String, Object> preview(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty String uemail,HttpServletRequest request) {
			log.info("Enter preview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
					+ request.getRemoteHost());
			Map<String, Object> map = new HashMap<String, Object>();
			 boolean status=relayIPService.validateRefNo(regno);
			  if(status) {
	       	  map.put("msg", "Invalid Registration Number-("+regno+") ");
	       	return map;
			  }
			 map= relayIPService.preview(regno,role,uemail);
			 log.info("Exit preview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
						+ request.getRemoteHost()+"  map:::"+map);
			return map;
		}
		
		@RequestMapping(value = "/relayIPEditPreview")
		public Map<String, Object> EditpreviewDetails(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty String uemail,HttpServletRequest request) {
			 log.info("Enter EditpreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
						+ request.getRemoteHost()); 
			Map<String, Object> map = new HashMap<String, Object>();
			 boolean status=relayIPService.validateRefNo(regno);
			  if(status) {
	       	  map.put("msg", "Invalid Registration Number-("+regno+") ");
	       	return map;
			  }
			 map= relayIPService.EditpreviewDetails(regno,role,uemail);
			 log.info("Exit EditpreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
						+ request.getRemoteHost()+"  map:::"+map); 
			return map;
		}
		
		@RequestMapping(value = "/relayIPUpdatePreview")
		public Map<String, Object>  UpdatePreviewDetails(@Valid @RequestBody RelayIPFormBean relayIPFormBean,BindingResult bindingResult,HttpServletRequest request)  {
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
			 ldapBeanList=relayIPService.UpdatePreviewDetails(relayIPFormBean);
			 log.info("Exit UpdatePreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
						+ request.getRemoteHost()+"  ldapBeanList:::"+ldapBeanList); 
			return ldapBeanList;
		}
	//********************************End of Preview(after submission)********************
	
	
	
	
	//********************************Start of Approve/Forward******************************
		@RequestMapping(value = "/relayIPApprove")
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
			 boolean status=relayIPService.validateRefNo(forwardBean.getRef_num());
			  if(status) {
	       	  map.put("msg", "Invalid Registration Number-("+forwardBean.getRef_num()+") ");
	       	return map;
			  }
			 forwardToData= relayIPService.approve(forwardBean);
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
	@RequestMapping(value = "/relayIPReject")
	public HashMap<String,String> reject(@RequestParam("regno") @NotEmpty @RegistrationNo String regno, @RequestParam("role") @NotBlank String role, @RequestParam("statRemarks") String statRemarks, @RequestParam("check") @NotBlank String check,@RequestParam("email") @NotBlank @Email String email,@RequestParam("mobile") @NotBlank String mobile,@RequestParam("name") @NotBlank String name,HttpServletRequest request) {
		log.info("Enter reject method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());

		HashMap<String,String> map = new HashMap<>(); 
		boolean status = relayIPService.reject(regno, role, statRemarks, check, email,mobile,name);
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
	@RequestMapping(value = "/relayIPTrackDetails")
	public HashMap<String, Object> fetchTrackDetails(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,HttpServletRequest request) {
		log.info("Enter fetchTrackDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
		HashMap<String, Object> map=relayIPService.fetchTrackDetails(regno);
		log.info("Enter fetchTrackDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost()+" map:::"+map);
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
	@RequestMapping(value = "/relayIPRaiseQuery")
	public Map<String, Object> raiseQuery(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("role") @NotEmpty String role,@RequestParam ("uemail") @NotEmpty @UsernameEmail String uemail, @RequestParam ("choose_recp") String choose_recp, @RequestParam ("to_email") @NotEmpty String to_email, @RequestParam ("statRemarks")String statRemarks,HttpServletRequest request){
		log.info("Enter raiseQuery method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());

		Map<String, Object> map = new HashMap<>();
		map=relayIPService.raiseQuery(regno, role, uemail,choose_recp,to_email,statRemarks);
		log.info("Exit raiseQuery method at " + new Date() + " map:::"+map);
		return map;
	}
	
	
	public ArrayList<String>  fetchRecps(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,@RequestParam("role") @NotEmpty String role,@RequestParam ("uemail") @NotEmpty @UsernameEmail String uemail) {
		log.info("Enter fetchRecps method at " + new Date());
		ArrayList<String> map= new ArrayList<>();
		map=relayIPService.fetchRecps(regno,role);
		log.info("Exit fetchRecps method at " + new Date() + " map:::"+map);
		return map;
	}
	//********************************End of Raise Query******************************************
	
	
	
	//********************************Start of Put On/Off Hold****************************************
	@RequestMapping(value = "/relayIPPutOnHold")
	public Map<String, Object> putOnHold(@RequestParam("regno") @NotEmpty @RegistrationNo String regno, @RequestParam("role") @NotEmpty String role, @RequestParam("statRemarks") String statRemarks, @RequestParam("type") @NotEmpty String type,@RequestParam("email") @NotEmpty String email,HttpServletRequest request) {
		log.info("Enter putOnHold method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
		Map<String, Object> map = new HashMap<>(); 
		map=relayIPService.putOnHold(regno, type, role, statRemarks);
		log.info("Exit putOnHold method at info " + new Date() +" map::::"+map);
		 return map;
	}
	//********************************End of Put On/Off Hold******************************************
	
	
	
	//********************************Start of Generate PDF******************************
	@RequestMapping(value = "/relayIPGenerateFormPdf", method = RequestMethod.GET)
	public void generateFormPdf(@RequestParam("regno") @RegistrationNo String regno, HttpServletResponse response) {
		log.info("Enter generateFormPdf method at " + new Date() + " regno:::::::: " +regno);
		JasperPrint jasperPrint = null;
		try {
			jasperPrint = relayIPService.generatePdf(regno);
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
	@RequestMapping(value = "/relayIPSavedocx", method = RequestMethod.POST)
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
		ArrayList<UploadMultipleFilesBean> status = relayIPService.saveDocx(uploadfiles);
		for (UploadMultipleFilesBean filebean : status) {
			map.put(filebean.getDoc(), filebean.getStatus());
		}
		log.info("Exit saveDocx method at info " + new Date() +" map::::"+map);
		return map;

	}
	//********************************End of Upload PDF********************************
	
	
	
	//********************************Start of Download PDF******************************
	@RequestMapping(value = "/relayIPDownload")
	public void downloadresource(@RequestParam("filename") @NotEmpty String filename,
			HttpServletRequest request, HttpServletResponse response){
		log.info("Enter LdapIPController: downloadresource method at info " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost()+" filename::::"+filename);
		response=relayIPService.downloadFiles(filename, response);
		log.info("Exit LdapIPController: downloadresource method at info " + new Date() +" response::::"+response);
	}
	//********************************End of Download PDF********************************
	
	
	
	//********************************Start of View PDF******************************
	@RequestMapping(value = "/relayIPViewDocx", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, Object> viewDocx(@RequestParam("regid") @NotEmpty @RegistrationNo String regid,
			@RequestParam("role") @NotEmpty String role,HttpServletRequest request) {
		log.info("Enter ViewPDFController: viewDocx method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "+ request.getRemoteHost());
		HashMap<String, Object> map = new HashMap<>();
		Map<String, Map<String, String>> status = relayIPService.viewDocx(regid, role);
		map.put("filestobedownloaded", status);
		log.info("Exit viewDocx method at info " + new Date() +" map::::"+map);
		return map;
	}
	//********************************End of View PDF********************************
}
