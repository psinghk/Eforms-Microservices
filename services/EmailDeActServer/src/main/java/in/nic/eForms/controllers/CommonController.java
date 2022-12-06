package in.nic.eForms.controllers;

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
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.UserForSearch;
import in.nic.eForms.services.EmailDeActService;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@Validated
@RestController
public class CommonController {
	private final EmailDeActService emailDeActService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public CommonController(EmailDeActService emailDeActService, ResponseBean responseBean, Util utilityService) {
		super();
		this.emailDeActService = emailDeActService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	
	@GetMapping("/generatePdf")
	public void generateForm(@RequestParam("regNumber") String regid, HttpServletResponse response)
			throws IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		try {
			log.debug("Process of PDF generation starts!!!");
			jasperPrint = emailDeActService.generateFormPdf(regid);
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
		response = emailDeActService.downloadFiles(filename, response);
	}

	@GetMapping(value = "/preview")
	public Map<String, Object> preview(@RequestParam("regNumber") @NotEmpty String regNo,
			@RequestParam("email") @NotEmpty String email) {
		log.info("Preview Email Activation form by {}",email);
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
		map.put("previewDetails", emailDeActService.preview(regNo));
		map.put("isEditable", isEditable);
		return map;
	}

	@RequestMapping(value = "/updateRequest")
	public ResponseBean updateRequest(@RequestParam("regNumber") @NotEmpty String regNo,
			@Valid @RequestBody PreviewFormBean previewFormBean) {
		
		responseBean.setErrors(null);
		responseBean.setRegNumber(null);
		responseBean.setRequestType(null);
		responseBean.setStatus(null);
		responseBean.setRegNumber(regNo);
		responseBean.setRequestType("Updation of request through preview");
		Map<String, Object> map = validateRequest(previewFormBean);
		if (map.isEmpty()) {
			if (emailDeActService.updatePreviewDetails(regNo, previewFormBean)) {
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

//	private Map<String, Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean) {
//		Map<String, Object> map = new HashMap<>();
//		OrganizationDto organizationDetails = new OrganizationDto();
//
//		BeanUtils.copyProperties(previewFormBean, organizationDetails);
//		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
//		ObjectMapper mapper = new ObjectMapper();
//		ErrorResponseForOrganizationValidationDto orgError = null;
//		
//		if (organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
//			log.debug("Errors in Organization");
//			try {
//				orgError = mapper.readValue(organizationValidationResponse, ErrorResponseForOrganizationValidationDto.class);
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			if (orgError != null)
//				map.put("orgError", orgError);
//		}
//		return map;
//	}
	
	 public Map<String, Object> validateRequest(PreviewFormBean previewFormBean) {
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> finalmap = new HashMap<>();
			OrganizationDto organizationDetails = new OrganizationDto();
			
			//ProfileDto profile = utilityService.fetchProfileByEmailInBean(email);
			String preferredEmail = previewFormBean.getPreferredEmail();

			if (!previewFormBean.getTnc()) {
				log.debug("Terms and condition is not selected.");
				map.put("tncError", "Please select Terms and Condition to proceed.");
			}
			
			if(!previewFormBean.getPreferredEmail().isEmpty()) {
				
				UserForSearch CompleteLdapData = featchStatusFromLdap(previewFormBean.getPreferredEmail());
			    if(CompleteLdapData==null) {
			    	map.put("emailError", "Please enter Government email address only");
			    }
			    else {
				String userStatus = CompleteLdapData.getUserInetStatus();
				String mailStatus =CompleteLdapData.getUserMailStatus(); 	
				System.out.println("userStatus :"+userStatus);
				
				if(!userStatus.equalsIgnoreCase("active") && !mailStatus.equalsIgnoreCase("active") ) {
					log.debug("Email is not for DeActivation:");
					map.put("emailError", "Please Enter active email for DeActivation: ");
				}
			    }
			}
			else {
				map.put("emailError", "Please Enter Email For DeActivation");
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
		
	 public UserForSearch featchStatusFromLdap(String mail) {
			
			return utilityService.findByMail(mail);
	         
		}
}
