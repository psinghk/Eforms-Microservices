package in.nic.eForms.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.eForms.models.ManualUploadBean;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.EmailActROService;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final EmailActROService emailActROService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public ReportingOfficerController(EmailActROService emailActROService, ResponseBean responseBean, Util utilityService) {
		super();
		this.emailActROService = emailActROService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}
	
	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam  String remarks,@RequestParam("submissionType") @NotEmpty String submissionType) {
		
		return emailActROService.approveRO(regNumber, ip, email, remarks, submissionType,responseBean);
	}
	
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, String remarks) {
		return emailActROService.rejectRO(regNumber, ip, email, remarks,responseBean);
	}
	
	@RequestMapping(value = "/manualupload")
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request) throws IOException {

		return emailActROService.manualupload(manualUploadBean, request, responseBean);
	}
	
}
