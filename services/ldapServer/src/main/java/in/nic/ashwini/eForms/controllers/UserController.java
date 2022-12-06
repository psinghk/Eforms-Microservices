package in.nic.ashwini.eForms.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.models.ManualUploadBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.ValidateFormBean;
import in.nic.ashwini.eForms.services.LdapUserService;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;


@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	private final LdapUserService ldapUserService;
	private final ResponseBean responseBean;
	@Autowired
	public UserController(LdapUserService ldapUserService, ResponseBean responseBean) {
		super();
		this.ldapUserService = ldapUserService;
		this.responseBean = responseBean;
	}

	@RequestMapping(value = "/validate")
	public Map<String,Object> validate(@Valid @ModelAttribute ValidateFormBean validateFormBean) {
		return ldapUserService.validate(validateFormBean);
	}
	
	@RequestMapping(value = "/validateRequest")
	public Map<String,Object> validateRequest(@Valid @ModelAttribute PreviewFormBean previewFormBean) {
		return ldapUserService.validateRequest(previewFormBean);
	}
	
	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@ModelAttribute("previewFormBean") @Valid PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) throws IOException {
		return ldapUserService.submitRequest(previewFormBean, ip, email, submissionType, responseBean);
	}
	
	@RequestMapping(value = "/preview")
	public Map<String,Object> preview(@RequestParam @NotEmpty String regNumber) {
		return ldapUserService.preview(regNumber);
	}
	
	@RequestMapping(value = "/updatePreview")
	public ResponseBean updatePreview(@RequestParam @NotEmpty String regNumber,@ModelAttribute("previewFormBean") @Valid PreviewFormBean previewFormBean) {
		return ldapUserService.updatePreviewDetails(regNumber,previewFormBean,responseBean);
	}
	
	@GetMapping(value = "/download")
	public void userDownloadDocs(@RequestParam("filename") @NotEmpty String filename,
			HttpServletRequest request, HttpServletResponse response){
			response=ldapUserService.downloadFiles(filename, response);
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return ldapUserService.approve(regNumber, ip, email, remarks, responseBean);
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, String remarks) {
		return ldapUserService.reject(regNumber, ip, email, remarks, responseBean);
	}
	
	
	
	@GetMapping("/generatePdf")
	public void generateForm(@RequestParam("regNumber") String regid, HttpServletResponse response)
			throws IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		try {
		//	log.debug("Process of PDF generation starts!!!");
			jasperPrint = ldapUserService.generateFormPdf(regid);
			if (jasperPrint != null) {
				OutputStream out = response.getOutputStream();
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", String.format("inline; filename=\"" + regid + ".pdf\""));
				response.addHeader("status", "success");
				JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			} else {
				//log.debug("Data could not be fetched. jsaperPrint is null");
				response.addHeader("status", "Failed");
			}
		} catch (JRException | IOException e) {
			//log.debug("Exception occured while generating PDF");
			response.addHeader("status", e.getMessage());
		}
	}
	@RequestMapping(value = "/manualupload")
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request) throws IOException {
		
		
		return ldapUserService.manualupload(manualUploadBean, request, responseBean);
	}

}
