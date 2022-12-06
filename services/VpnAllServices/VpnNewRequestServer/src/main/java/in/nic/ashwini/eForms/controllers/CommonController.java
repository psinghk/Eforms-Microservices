
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
import in.nic.ashwini.eForms.services.VpnService;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@Validated
@RestController
public class CommonController {
	private final VpnService vpnService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public CommonController(VpnService vpnService, ResponseBean responseBean, Util utilityService) {
		super();
		this.vpnService = vpnService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@GetMapping("/generatePdf")
	public void generateForm(@RequestParam("regNumber") String regid, HttpServletResponse response)
			throws IllegalAccessException, InvocationTargetException {
		JasperPrint jasperPrint = null;
		try {
			log.debug("Process of PDF generation starts!!!");
			jasperPrint = vpnService.generateFormPdf(regid);
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
		response = vpnService.downloadFiles(filename, response);
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
		map.put("previewDetails", vpnService.preview(regNo));
		map.put("previewEntriesDetails", vpnService.previewFormDetails(regNo));
		map.put("isEditable", isEditable);
		return map;
	}

	@RequestMapping(value = "/updateRequest")
	public ResponseBean updateRequest(@RequestParam("regNumber") @NotEmpty String regNo,
			@Valid @RequestBody PreviewFormBean previewFormBean) {
		responseBean.setRegNumber(regNo);
		responseBean.setRequestType("Updation of request through preview");
		Map<String, Object> map = validateRequest(previewFormBean);
		if (map != null) {
			if (vpnService.updatePreviewDetails(regNo, previewFormBean)) {
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

	private Map<String, Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean) {
		Map<String, Object> map = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();

		if (!previewFormBean.getTnc()) {
			log.debug("Terms and condition is not selected.");
			map.put("tnc", "Please select Terms and Condition to proceed.");
		}

		BeanUtils.copyProperties(previewFormBean, organizationDetails);
		String organizationValidationResponse = utilityService.validateOrganization(organizationDetails);
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponseForOrganizationValidationDto orgError = null;
		
		if (organizationValidationResponse != null && !organizationValidationResponse.isEmpty()) {
			log.debug("Errors in Organization");
			try {
				orgError = mapper.readValue(organizationValidationResponse, ErrorResponseForOrganizationValidationDto.class);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (orgError != null)
				map.put("orgError", orgError);
		}
		return map;
	}
}
