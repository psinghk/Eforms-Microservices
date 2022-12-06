package in.nic.eForms.controllers;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.SingleEmailROService;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final SingleEmailROService singleEmailROService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public ReportingOfficerController(SingleEmailROService singleEmailROService, ResponseBean responseBean, Util utilityService) {
		super();
		this.singleEmailROService = singleEmailROService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}
	
	//@Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.")
	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam  String remarks,@RequestParam("submissionType") @NotEmpty String submissionType) {
		System.out.println("::::::::::ro approve:::::::::::::::::");
		return singleEmailROService.approveRO(regNumber, ip, email, remarks, submissionType,responseBean);
	}
	
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return singleEmailROService.rejectRO(regNumber, ip, email, remarks,responseBean);
	}
	
	
}
