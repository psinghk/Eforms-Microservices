package in.nic.eform.ldap.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
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
import javax.xml.parsers.ParserConfigurationException;
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
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import in.nic.eform.ldap.bean.ForwardBean;
import in.nic.eform.ldap.bean.LdapFormBean;
import in.nic.eform.ldap.bean.UploadMultipleFilesBean;
import in.nic.eform.ldap.service.LdapGlobalCheck;
import in.nic.eform.ldap.service.LdapService;
import in.nic.eform.utility.GetRealIp;
import in.nic.eform.validation.RegistrationNo;
import in.nic.eform.validation.UsernameEmail;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

// specific service name should be as @RequestMapping at controller level Eg. @RequestMapping("v1/ldap-reg")
@RestController
@Validated
public class LdapController {

	private static final Logger log = LoggerFactory.getLogger(LdapController.class);
	@Autowired
	private LdapService ldapService;
	@Autowired
	private GetRealIp getRealIp;
	@Autowired
	private LdapGlobalCheck check;

	// *******************start of submission********************
	// Method for Open Preview just after Form Submit

	// Retun type should be a json string only
	// Signature should be like "/submitrequest"
	@RequestMapping(value = "/ldapSubmissionPreview")
	public Map<String, Object> submissionPreview(@Valid @RequestBody LdapFormBean ldapFormBean,
			BindingResult bindingResult, HttpServletRequest request) {

		// logger framework self printing Date Time
		// Change info like "Submitting user request ... "
		// why request.getRemoteHost() ??
		log.info("Enter submissionPreview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		HashMap<String, Object> errors = new HashMap<>();

		// Call the service, & response from there will be a json only
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		if (!errors.isEmpty()) {
			return errors;
		}
		Map<String, Object> ldapBeanList = null;
		ldapBeanList = ldapService.submissionPreview(ldapFormBean);

		// No need of this log
		log.info("Exit submissionPreview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + " ldapBeanList::" + ldapBeanList);
		return ldapBeanList;
	}

//		//Method to view RO details if we pass form details

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

	// Comment: Please change @RequestMapping as "/getrodetail" also change method
	// name
	// Method to view RO details after submit Preview
	@RequestMapping(value = "/ldapSubmissionRoDetails")
	public Map<String, Object> submissionRoDetails(HttpServletRequest request) {

		// Logger and return type; as stated earlier....

		log.info("Enter submissionRoDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		Map<String, Object> ldapBeanList = null;
		ldapBeanList = ldapService.submissionRoDetails();
		log.info("Exit submissionRoDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + "  ldapBeanList:::" + ldapBeanList);
		return ldapBeanList;
	}

	// generate refNO and insert into base tbl
	@RequestMapping(value = "/ldapGenerateRefNo")
	public Map<String, Object> generateRefNo(@Valid @RequestBody LdapFormBean ldapFormBean, BindingResult bindingResult,
			HttpServletRequest request) {

		// Logger and return type; as stated earlier....

		log.info("Enter generateRefNo method at info " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		HashMap<String, Object> errors = new HashMap<>();

		// shift it to service layer....
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		if (!errors.isEmpty()) {
			return errors;
		}
		Map<String, Object> ldapBeanList = null;
		Map<String, Object> map = new HashMap<String, Object>();
		ldapBeanList = ldapService.generateRefNo(ldapFormBean);
		log.info("Exit  generateRefNo method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + "  LdapBeanList::::" + ldapBeanList);
		return ldapBeanList;
	}

	// why it is different; it should be part of user form submission.
	// update telnet in base tbl
	@RequestMapping(value = "/ldapSubmissionTelnet")
	public Map<String, Object> submissionTelnetDtl(@RequestParam("regno") @RegistrationNo String regno,
			@RequestParam("telnet") String telnet, HttpServletRequest request) {
		log.info("Enter  submissionTelnetDtl method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		Map<String, Object> ldapBeanList = null;
		Map<String, Object> map = new HashMap<String, Object>();
		ldapBeanList = ldapService.submissionTelnetDtl(telnet, regno);
		log.info("Exit submissionTelnetDtl method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + " LdapBeanList:::" + ldapBeanList);
		return ldapBeanList;
	}

	// This activity is the part of user Req. submission only
	// So save the data in Base, final_audit_track & status table at the same time.
	// complete form submission
	@RequestMapping(value = "/ldapFormSubmission")
	public Map<String, Object> formSubmission(@RequestParam("regno") @RegistrationNo String regno,
			@RequestParam("check") String check, @RequestParam("formtype") String formtype,
			@RequestParam("consent") String consent, @RequestParam("email") String email, HttpServletRequest request) {
		log.info("Enter formSubmission method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		Map<String, Object> ldapBeanList = null;
		Map<String, Object> map = new HashMap<String, Object>();
		ldapBeanList = ldapService.consent(regno, check, formtype, consent, email);
		log.info("Exit formSubmission method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		return ldapBeanList;
	}

	// onblur "verify_url" in ldap.js---1512
	@RequestMapping(value = "/ldapVerifyUrl")
	public String verifyUrl(@RequestParam("app_url") String app_url, HttpServletRequest request)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParserConfigurationException,
			SAXException, IOException, SQLException {

		log.info("Enter verifyUrl method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		String s = "", status = "";
		URL url = new URL(app_url);
		String domain = url.getHost();
		s = ldapService.audit(domain);
		// s = "Under Audit";
		if (s.contains("Under Audit")) {
			status = "noaudit";

		} else if (s.contains("Not Audited")) {
			status = "noaudit";
		} else {
			status = "audit";
		}
		log.info("Exit verifyUrl method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + " status " + status);
		return status;
	}

	@RequestMapping(value = "/ldapPreview")
	public Map<String, Object> preview(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty String uemail,
			HttpServletRequest request) {
		log.info("Enter preview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		Map<String, Object> map = new HashMap<String, Object>();
		// move validation at service layer
		boolean status = ldapService.validateRefNo(regno);
		if (status) {
			map.put("msg", "Invalid Registration Number-(" + regno + ") ");
			return map;
		}
		map = ldapService.preview(regno, role, uemail);
		log.info("Exit preview method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + "  map:::" + map);
		return map;
	}

	// No need this API
	@RequestMapping(value = "/ldapEditPreview")
	public Map<String, Object> EditpreviewDetails(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty String uemail,
			HttpServletRequest request) {
		log.info("Enter EditpreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		Map<String, Object> map = new HashMap<String, Object>();
		boolean status = ldapService.validateRefNo(regno);
		if (status) {
			map.put("msg", "Invalid Registration Number-(" + regno + ") ");
			return map;
		}
		map = ldapService.EditpreviewDetails(regno, role, uemail);
		log.info("Exit EditpreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + "  map:::" + map);
		return map;
	}

	// merge this API with submission API ...
	@RequestMapping(value = "/ldapUpdatePreview")
	public Map<String, Object> UpdatePreviewDetails(@Valid @RequestBody LdapFormBean ldapFormBean,
			BindingResult bindingResult, HttpServletRequest request) {
		log.info("Enter UpdatePreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		HashMap<String, Object> errors = new HashMap<>();
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		if (!errors.isEmpty()) {
			return errors;
		}
		Map<String, Object> ldapBeanList = null;
		Map<String, Object> map = new HashMap<String, Object>();
		ldapBeanList = ldapService.UpdatePreviewDetails(ldapFormBean);
		log.info("Exit UpdatePreviewDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + "  ldapBeanList:::" + ldapBeanList);
		return ldapBeanList;
	}

	// ***************start of forward/approve***************//
	@RequestMapping(value = "/ldapApprove")
	public Map<String, Object> approve(@Valid @RequestBody ForwardBean forwardBean, BindingResult bindingResult,
			HttpServletRequest request) {

		log.info("Enter approve method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());

		// Move to service layer
		HashMap<String, Object> errors = new HashMap<>();
		Map<String, String> forwardToData = null;
		Map<String, Object> map = new HashMap<String, Object>();
		bindingResult.getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		if (!errors.isEmpty()) {
			return errors;
		}
		boolean status = ldapService.validateRefNo(forwardBean.getRef_num());
		if (status) {
			map.put("msg", "Invalid Registration Number-(" + forwardBean.getRef_num() + ") ");
			return map;
		}

		// We will Again verify the Forward-Action
		forwardToData = ldapService.approve(forwardBean);
		if (!forwardToData.get("nextlevel").equals("Completed")) {
			map.put("status", "Application Approved and Forwarded Successfully to the " + forwardToData.get("nextlevel")
					+ "(" + forwardToData.get("toemail") + ")");
			map.put("Registration No.", forwardBean.getRef_num());
		} else {
			map.put("status", "Status Updated Successfully");
			map.put("Registration No.", forwardBean.getRef_num());
		}
		log.info("Exit approve method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + "  map:::" + map);
		return map;
	}
	// ***************End of forward/approve***************//

	// ************************Start of Track User****************************

	@RequestMapping(value = "/ldapTrackDetails")
	public HashMap<String, Object> fetchTrackDetails(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			HttpServletRequest request) {

		// Log and return as stated earlier....

		log.info("Enter fetchTrackDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		HashMap<String, Object> map = ldapService.fetchTrackDetails(regno);

		log.info("Exit fetchTrackDetails method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + "  map:::" + map);
		return map;
	}

	@RequestMapping(value = "/ldapPutOnHold")
	public Map<String, Object> putOnHold(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("role") @NotEmpty String role, @RequestParam("statRemarks") String statRemarks,
			@RequestParam("type") @NotEmpty String type, @RequestParam("email") @NotEmpty String email,
			HttpServletRequest request) {

		// There are 5 variable in RequestParam; can any alternative like in body .....

		// Log and return as stated earlier....

		log.info("Enter putOnHold method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		Map<String, Object> map = new HashMap<>();
		map = ldapService.putOnHold(regno, type, role, statRemarks);
		log.info("Exit putOnHold method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + "  map:::" + map);
		return map;
	}

	@RequestMapping(value = "/ldapRaiseQuery")
	public Map<String, Object> raiseQuery(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty @UsernameEmail String uemail,
			@RequestParam("choose_recp") String choose_recp, @RequestParam("to_email") @NotEmpty String to_email,
			@RequestParam("statRemarks") String statRemarks, HttpServletRequest request) {

		// Log and return as stated earlier....

		log.info("Enter raiseQuery method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		Map<String, Object> map = new HashMap<>();
		map = ldapService.raiseQuery(regno, role, uemail, choose_recp, to_email, statRemarks);
		log.info("Exit raiseQuery method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + "  map:::" + map);
		return map;
	}

	public ArrayList<String> fetchRecps(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("role") @NotEmpty String role, @RequestParam("uemail") @NotEmpty @UsernameEmail String uemail,
			HttpServletRequest request) {

		// Log and return as stated earlier....

		log.info("Enter fetchRecps method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		ArrayList<String> map = new ArrayList<>();
		map = ldapService.fetchRecps(regno, role);
		log.info("Exit fetchRecps method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + " map:::" + map);
		return map;
	}


	@RequestMapping(value = "/ldapReject")
	public HashMap<String, String> reject(@RequestParam("regno") @NotEmpty @RegistrationNo String regno,
			@RequestParam("role") @NotBlank String role, @RequestParam("statRemarks") String statRemarks,
			@RequestParam("check") @NotBlank String check, @RequestParam("email") @NotBlank @Email String email,
			@RequestParam("mobile") @NotBlank String mobile, @RequestParam("name") @NotBlank String name,
			HttpServletRequest request) {

		// There are 7 variable in RequestParam; can any alternative like in body .....

		// Log and return as stated earlier....

		log.info("Enter reject method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		HashMap<String, String> map = new HashMap<>();
		boolean status = ldapService.reject(regno, role, statRemarks, check, email, mobile, name);
		if (status) {
			map.put("status", "Application Rejected Successfully !");
			map.put("Registration No", regno);
		} else {
			map.put("status", "Application could not be rejected!!!");
			map.put("Registration No", regno);
		}
		log.info("Exit reject method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + " map:::" + map);
		return map;
	}

	@RequestMapping(value = "/ldapGenerateFormPdf", method = RequestMethod.GET)
	public void generatePdf(@RequestParam("regno") @RegistrationNo String regno, HttpServletResponse response,
			HttpServletRequest request) throws IllegalAccessException, InvocationTargetException {
		log.info("Enter generatePdf method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		
		// Shift it to service layer ....
		
		JasperPrint jasperPrint = null;
		try {
			jasperPrint = ldapService.generatePdf(regno);
			if (jasperPrint != null) {
				OutputStream out = response.getOutputStream();
				// response.setContentType("application/x-download");
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", String.format("inline; filename=\"" + regno + ".pdf\""));
				response.addHeader("status", "success");
				JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			} else {
				response.addHeader("status", "Failed");
			}

			log.info("Exit generatePdf method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
					+ request.getRemoteHost() + " response:::" + response);
		} catch (JRException | IOException e) {
			response.addHeader("status", e.getMessage());
		}
	}

	// ****************Start of Upload Docs*****************************
	
	// rename URL with some more meaningful
	@RequestMapping(value = "/ldapSaveDdcx", method = RequestMethod.POST)
	public HashMap<String, Object> saveDocx(@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles,
			BindingResult bindingResult, HttpServletRequest request) {
		log.info("Enter saveDocx method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
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
		if (!err.isEmpty())
			return err;
		ArrayList<UploadMultipleFilesBean> status = ldapService.saveDocx(uploadfiles);
		for (UploadMultipleFilesBean filebean : status) {
			map.put(filebean.getDoc(), filebean.getStatus());
		}
		log.info("Exit saveDocx method at " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost() + " map:::" + map);
		return map;

	}

	@RequestMapping(value = "/ldapDownload")
	public void downloadresource(@RequestParam("filename") @NotEmpty String filename, HttpServletRequest request,
			HttpServletResponse response) {
		log.info("Enter downloadresource method at  " + new Date() + " from ip " + getRealIp.getRealIp(request) + " "
				+ request.getRemoteHost());
		response = ldapService.downloadFiles(filename, response);
	}
	// ***************End of download docs***************//

}
