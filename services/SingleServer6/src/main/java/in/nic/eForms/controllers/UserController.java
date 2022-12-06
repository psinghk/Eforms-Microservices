package in.nic.eForms.controllers;

import java.text.ParseException;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.SingleEmailUserService;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	private final SingleEmailUserService singleEmailUserService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	
	@Autowired
	public UserController(SingleEmailUserService singleEmailUserService,Util utilityService,ResponseBean responseBean) {
		super();
		this.singleEmailUserService = singleEmailUserService;
		this.responseBean = responseBean;
		this.utilityService=utilityService;
		
	}


	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) throws ParseException {
		return singleEmailUserService.submitRequest(previewFormBean, ip, email, submissionType,responseBean);
	}

	
	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return singleEmailUserService.approve(regNumber,ip,email,remarks,responseBean);

	}
	

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return singleEmailUserService.reject(regNumber, ip, email, remarks, responseBean);
	}

	
	@RequestMapping(value = "/validateRequest")
	public Map<String,Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean,String email) throws ParseException {
		return singleEmailUserService.validateRequest(previewFormBean,email);
	}
	@RequestMapping(value = "/getDomain")
	public Set<String> getDomain(@Valid @RequestBody String empType,String email) throws ParseException {
		return singleEmailUserService.getDomain(empType,email);
	}
	@RequestMapping(value = "/checkExistPreferredEmail1")
	public String checkExistPreferredEmail1( @RequestParam("preferredEmail1") String preferredEmail1,@RequestParam String idType,String email) throws ParseException {
		System.out.println("email:::::::::::   :::   "+preferredEmail1);
		return singleEmailUserService.allLdapValues(preferredEmail1,idType,email);
	}
	@RequestMapping(value = "/checkExistPreferredEmail2")
	public String checkExistPreferredEmail2( @RequestParam String preferredEmail2,@RequestParam String idType,String email) throws ParseException {
		return singleEmailUserService.allLdapValues2(preferredEmail2,idType,email);
	}
	
		
	


}
