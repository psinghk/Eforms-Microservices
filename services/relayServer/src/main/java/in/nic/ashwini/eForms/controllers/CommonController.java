package in.nic.ashwini.eForms.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.services.RelayService;
import in.nic.ashwini.eForms.services.ValidationService;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@Validated
//@RequestMapping("/user")
@RestController
public class CommonController {
	private final RelayService relayService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	private final ValidationService validationService;

	@Autowired
	public CommonController(RelayService relayService, ResponseBean responseBean,ValidationService validationService, Util utilityService) {
		super();
		this.relayService = relayService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
		this.validationService = validationService;
	}

	@GetMapping("/generatePdf")
	public void generateForm(@RequestParam("regNumber") String regid, HttpServletResponse response)
			throws IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		try {
			log.debug("Process of PDF generation starts!!!");
			jasperPrint = relayService.generateFormPdf(regid);
			if (jasperPrint != null) {
				OutputStream out = response.getOutputStream();
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", String.format("inline; filename=\"" + regid + ".pdf\""));
				response.addHeader("status", "success");
				JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			} else {
				log.debug("Data could not be fetched. jsaperPrint is null");
				response.addHeader("status", "Failed");
			}
		} catch (JRException | IOException e) {
			log.debug("Exception occured while generating PDF");
			response.addHeader("status", e.getMessage());
		}
	}

	@RequestMapping(value = "/download")
	public void downloadresource(@RequestParam("filename") @NotEmpty String filename, HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("downloading file {}", filename);
		System.out.println("ssatysssssssss");
		response = relayService.downloadFiles(filename, response);
	}

	@GetMapping(value = "/preview")
	public Map<String, Object> preview(@RequestParam("regNumber") @NotEmpty String regNo,
			@RequestParam("email") @NotEmpty String email) {
		System.out.println("Email::::: "+email);
		Map<String, Object> map = new HashMap<>();
		List<String> aliases = null;
		boolean isEditable = false;

		if (utilityService.isGovEmployee(email)) {
			aliases = utilityService.aliases(email);
		} else {
			aliases = Arrays.asList(email);
		}
		for (String fethcedEmail : aliases) {
			if (utilityService.isEditable(regNo, fethcedEmail)) {
				isEditable = true;
				break;
			}
		}
		map.put("previewDetails", relayService.preview(regNo));
		map.put("isEditable", isEditable);
		return map;
	}

	@RequestMapping(value = "/updateRequest")
	public ResponseBean updateRequest(@RequestParam("regNumber") @NotEmpty String regNo,
			@Valid @ModelAttribute PreviewFormBean previewFormBean) {
		responseBean.setRegNumber(regNo);
		responseBean.setRequestType("Updation of request through preview");
		Map<Object, Object> map = validateRequest(previewFormBean);
		if (map != null) {
			if (relayService.updatePreviewDetails(regNo, previewFormBean)) {
				log.info("Details updated successfully!!!");
				responseBean.setStatus("Details updated successfully!!!");
			} else {
				log.warn("Database threw error. Hence, details could not be updated");
				responseBean.setStatus("Something went wrong and Details could not be updated");
			}
		} else {
			responseBean.setErrors(map);
			responseBean.setStatus("Application could not be updated.");
		}
		return responseBean;
	}

	
	public Map<Object, Object> validateRequest(@Valid PreviewFormBean previewFormBean) {
		Map<Object, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();

		// validations

		if (previewFormBean.getOs().isEmpty()) {
			map.put("os_error",
					"Enter Operating System (Name, Version), [Only characters limit[100],whitespaces,comma(,),hypen(-) allowed]");
		}
		if (previewFormBean.getDivisionName().isEmpty()) {
			map.put("division_error",
					"Enter Name of Division, [characters only limit[50], dot(,),comma(,) whitespaces allowed]");
		}
		if (previewFormBean.getServerLoc().isEmpty()) {
			map.put("serverLocError", "please enter location");
		}
		if (previewFormBean.getAppName().isEmpty()) {
			map.put("appNameError", "please enter application name");
		}
		if (validationService.relayUrlValidation(previewFormBean.getAppUrl())) {
			map.put("appUrlError", "please enter correct url");
		}
		if (previewFormBean.getAppUrl().isEmpty()) {
			map.put("appUrlError", "please enter correct url");
		}

		if (previewFormBean.getRequestFor().equals("req_new")) {

			if (previewFormBean.getAppIp().contains(";")) {
				String[] relayip = previewFormBean.getAppIp().split(";");

				int io = 0;

				for (int i = 0; i < relayip.length; i++) {
					String ip = relayip[i];
					boolean ip_error = validationService.serviceipValidation(ip);
					io = io + 1;
					if (ip_error) {

						map.put("appIp_Error_" + io, "Enter the IP Address [e.g.: 164.100.X.X ]");
					}
				}

			} else {
				boolean ip1_error = validationService.baseipValidation(previewFormBean.getAppIp());
				if (ip1_error == true) {
					log.debug("application ip is not correct");
					map.put("appIp_error_1", "Enter the IP Address [e.g.: 164.100.X.X ]");
				}
			}

			if (previewFormBean.getStagingIp().equals("no")) {

				if (previewFormBean.getSecurityAudit().equalsIgnoreCase("Hardware")) {
					if (previewFormBean.getHardwareCertFile() == null
							|| previewFormBean.getHardwareCertFile().getOriginalFilename().isEmpty()) {
						map.put("hardwareCertError", "Please upload a hardware cert file");
						log.debug("need to upload a Hardware cert file");
					}
					if (previewFormBean.getHardwareCertFile() != null) {
						if (!previewFormBean.getHardwareCertFile().getContentType().equals("application/pdf")) {
							map.put("hardwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}

				if (previewFormBean.getSecurityAudit().equalsIgnoreCase("Software")) {
					if (previewFormBean.getCertFile() == null || previewFormBean.getCertFile().getOriginalFilename().isEmpty()) {
						map.put("softwareCertError", "Please upload a software cert file");
						log.debug("need to upload a Software cert file");
					}

					if (previewFormBean.getCertFile() != null) {
						if (!previewFormBean.getCertFile().getContentType().equals("application/pdf")) {
							map.put("softwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}
			}

			if (previewFormBean.getStagingIp().equals("yes")) {
				log.debug("no need to upload a file");
			}
		}

		if (previewFormBean.getRequestFor().equals("req_new") || previewFormBean.getRequestFor().equals("req_add")
				|| previewFormBean.getRequestFor().equals("req_modify")) {

			if (previewFormBean.getPort().isEmpty()) {

				map.put("portError", "please select a port");

			}

			if (previewFormBean.getPort().equals("465")) {

				if (previewFormBean.getRelayAuthId() == null && previewFormBean.getRelayAuthId().isEmpty()) {

					map.put("authIdError", "please enter an auth id");

				} else {
					boolean auth_id_error = validationService.EmailValidation(previewFormBean.getRelayAuthId());
					if (!utilityService.isEmailAvailable(previewFormBean.getRelayAuthId())) {
						map.put("auth_id_error", "Email address does not exist in ldap");
					} else {
						if (auth_id_error == true) {
							map.put("auth_id_error", "Enter auth id in correct format");
						}
					}
				}
			}
			if (previewFormBean.getSenderId().isEmpty()) {
				map.put("senderIdError", "please enter sender Id");
			}
		}

		if (previewFormBean.getServerLoc().equalsIgnoreCase("Other")) {

			if (previewFormBean.getOtherServerLoc().isEmpty()) {
				map.put("otherlocError", "please enter other server location");
			}
		}

		if (previewFormBean.getRequestFor().equals("req_add") || previewFormBean.getRequestFor().equals("req_modify")) {

			if (previewFormBean.getAppIp().contains(";")) {
				String[] relayip = previewFormBean.getAppIp().split(";");
				int io = 0;
				for (int i = 0; i < relayip.length; i++) {
					String ip = relayip[i];

					boolean ip_error = validationService.serviceipValidation(ip);
					io = io + 1;
					if (ip_error) {
						map.put("appIp_Error_" + io, "Enter the IP Address [e.g.: 164.100.X.X ]");
					}
				}
			} else {
				boolean ip1_error = validationService.baseipValidation(previewFormBean.getAppIp());
				if (ip1_error == true) {
					log.debug("application ip is not correct");
					map.put("appIp_error_1", "Enter the IP Address [e.g.: 164.100.X.X ]");
				}
			}

			if (previewFormBean.getOldAppIp().contains(";")) {
				String[] oldRelayip = previewFormBean.getOldAppIp().split(";");
				int it = 0;
				for (int i = 0; i < oldRelayip.length; i++) {
					String ip = oldRelayip[i];

					boolean ip_error = validationService.serviceipValidation(ip);
					it = it + 1;
					if (ip_error) {

						map.put("OldAppIp_Error_" + it, "Enter the IP Address [e.g.: 164.100.X.X ]");
					}
				}

			} else {
				boolean ip3_error = validationService.baseipValidation(previewFormBean.getOldAppIp());
				if (ip3_error == true) {
					log.debug(" old application ip is not correct");
					map.put("OldAppIp_Error_1", "Enter the IP Address [e.g.: 164.100.X.X ]");
				}
			}

			if (previewFormBean.getStagingIp().equals("no")) {

				if (previewFormBean.getSecurityAudit().equalsIgnoreCase("Hardware")) {

					if (previewFormBean.getHardwareCertFile() == null
							|| previewFormBean.getHardwareCertFile().isEmpty()) {
						map.put("hardwareCertError", "Please upload a hardware cert file");
						log.debug("need to upload a Hardware cert file");
					} else {
						if (!previewFormBean.getHardwareCertFile().getContentType().equals("application/pdf")) {
							map.put("hardwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}
				if (previewFormBean.getSecurityAudit().equalsIgnoreCase("Software")) {
					if (previewFormBean.getCertFile() == null || previewFormBean.getCertFile().isEmpty()) {
						map.put("softwareCertError", "Please upload a software cert file");
						log.debug("need to upload a Software cert file");
					} else {
						if (!previewFormBean.getCertFile().getContentType().equals("application/pdf")) {
							map.put("softwareCertFormatError", "please upload file in pdf format only");
						}
					}
				}
			}

			if (previewFormBean.getStagingIp().equals("yes")) {
				log.debug("no need to upload a file");
			}

		}

		if (previewFormBean.getRequestFor().equals("req_new") || previewFormBean.getRequestFor().equals("req_add")
				|| previewFormBean.getRequestFor().equals("req_modify")) {

			if (previewFormBean.getSenderId().isEmpty()) {
				map.put("senderId_error", "please enter sender id");
			}
			if (previewFormBean.getPointName().isEmpty()) {
				map.put("p_name_error", "Please Enter Applicant Name [characters,dot(.) and whitespace]");
			}

			if (validationService.EmailValidation(previewFormBean.getPointEmail())) {
				map.put("p_email_error", "Please Enter Applicant Email]");
			}
			if (previewFormBean.getPointMobileNumber().isEmpty()) {
				map.put("p_mobile_error", "Please Enter Applicant Mobile [e.g:+919999999999]");
			}
			if (previewFormBean.getPort().equals("465")) {
				if (previewFormBean.getRelayAuthId().isEmpty()) {
					map.put("auth_id_error", "Please Enter Correct Auth Id");
				}
			}
		}

		if (!previewFormBean.getTnc()) {
			log.debug("Terms and condition is not selected.");
			map.put("tncError", "Please select Terms and Condition to proceed.");
		}

		BeanUtils.copyProperties(previewFormBean, organizationDetails);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;

		if (organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
			log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse,
						ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (orgError != null)
				map.put("orgError", orgError);
		}
		if (map.size() > 1) {
			finalmap.put("errors", map);
		}
		return map;
	}
	
	@RequestMapping(value = "/test")
	public String test(@RequestParam String emailsss) {
		//log.debug("downloading file {}", filename);
		System.out.println("ssatysssssssss" + emailsss);
		return "success";
	}

	

}
