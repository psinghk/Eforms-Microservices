package in.nic.ashwini.eForms.controllers;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.models.NextHopBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.UploadMultipleFilesBean;
import in.nic.ashwini.eForms.services.MobileServiceUser;
import in.nic.ashwini.eForms.utils.Util;

@Validated
@RequestMapping("/user")
@RestController
public class UserController {

	private final ResponseBean responseBean;
	private MobileServiceUser mobileServiceUser;

	@Autowired
	public UserController(MobileServiceUser mobileServiceUser, ResponseBean responseBean, Util utilityService) {
		super();
		this.mobileServiceUser = mobileServiceUser;
		this.responseBean = responseBean;

	}

	@RequestMapping(value = "/OtpGenerate_newmobile")
	public ResponseBean OtpGenerateNewmobile(@RequestParam("mobile") @NotEmpty String mobile,
			@RequestParam("newMobile") @NotEmpty String newMobile,
			@RequestParam("countryCode") @NotEmpty String countryCode) {

		return mobileServiceUser.OtpGenerateNewmobile(mobile, newMobile, countryCode, responseBean);

	}

	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {

		return mobileServiceUser.submitRequest(previewFormBean, ip, email, submissionType, responseBean);
	}

	@RequestMapping(value = "/validateRequest")
	public Map<String, Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean) {

		return mobileServiceUser.validateRequest(previewFormBean);
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {

		return mobileServiceUser.approve(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {

		return mobileServiceUser.reject(regNumber, ip, email, remarks, responseBean);

	}

	@PostMapping("/validate")
	public Map<String, Object> validate(@RequestParam("countryCode") @NotEmpty String countryCode,
			@RequestParam("newMobile") @NotEmpty @Pattern(regexp = "^[+0-9]{10}$", message = "invalid mobile number. ") String newMobile,
			@RequestParam("otp") @NotEmpty String otp) {

		return mobileServiceUser.validate(countryCode, newMobile, otp);
	}

	@GetMapping("/fetchNextHop")
	public NextHopBean findNextHop(@Valid @RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {

		return mobileServiceUser.findNextHop(previewFormBean, ip, email, submissionType);
	}
	
	
	
	
	
	
	
	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip, @RequestParam String remarks,
			@RequestParam("email") @NotEmpty String email,@Valid @ModelAttribute("uploadfiles") UploadMultipleFilesBean uploadfiles) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");
		
		return mobileServiceUser.approve(regNumber, ip,remarks, email, uploadfiles,responseBean);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
