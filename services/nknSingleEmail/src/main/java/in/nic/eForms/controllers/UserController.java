package in.nic.eForms.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.eForms.entities.NknSingleBase;
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.ValidateFormBean;
import in.nic.eForms.services.NknSingleService;
import in.nic.eForms.services.NknSingleUserService;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	private final NknSingleUserService nknSingleUserService;
	private final ResponseBean responseBean;
	
	@Autowired
	public UserController(NknSingleUserService nknSingleUserService, ResponseBean responseBean) {
		super();
		this.nknSingleUserService = nknSingleUserService;
		this.responseBean = responseBean;
		
	}

	@RequestMapping(value = "/validate")
	public Map<String, Object> validate(@Valid @RequestBody ValidateFormBean validateFormBean) {
		System.out.println("kjGHJGhds");
		return nknSingleUserService.validate(validateFormBean);
	}
	
	@RequestMapping(value = "/validateRequest")
	public Map<String, Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean) throws ParseException {
		return nknSingleUserService.validateRequest(previewFormBean);
	}	
	
	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) throws ParseException {
		return nknSingleUserService.submitRequest(previewFormBean, ip, email, submissionType,responseBean);
	}
	
	@RequestMapping(value = "/preview")
	public Map<String,Object> preview(@RequestParam @NotEmpty String regNumber) {
		return nknSingleUserService.preview(regNumber);
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return nknSingleUserService.approve(regNumber,ip,email,remarks,responseBean);
	}
	

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return nknSingleUserService.reject(regNumber, ip, email, remarks, responseBean);
	}

	@GetMapping("/generatePdf")
	public void generateForm(@RequestParam("regNumber") String regid, HttpServletResponse response)
			throws IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		try {
			jasperPrint = nknSingleUserService.generateFormPdf(regid);
			if (jasperPrint != null) {
				OutputStream out = response.getOutputStream();
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
	
		
}
