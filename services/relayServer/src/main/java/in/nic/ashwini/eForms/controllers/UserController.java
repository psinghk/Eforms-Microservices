package in.nic.ashwini.eForms.controllers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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

import in.nic.ashwini.eForms.models.ManualUploadBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.ValidateFormBean;
import in.nic.ashwini.eForms.services.RelayServiceUser;
import in.nic.ashwini.eForms.utils.Util;

@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	private final RelayServiceUser relayServiceUser;
	private final ResponseBean responseBean;
	private final Util utilityService;

	// private NextHopBean nextHopBean;

	@Autowired
	public UserController(RelayServiceUser relayServiceUser, ResponseBean responseBean, Util utilityService) {
		super();
		this.relayServiceUser = relayServiceUser;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
		// this.nextHopBean = nextHopBean;
	}

	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@Valid @ModelAttribute PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) throws IOException {

		responseBean.setRequestType("Submission of request");

		return relayServiceUser.submitRequest(previewFormBean, ip, email, submissionType);
	}

	@RequestMapping(value = "/validateRequest")
	public Map<Object, Object> validateRequest(@Valid @ModelAttribute PreviewFormBean previewFormBean) {

		return relayServiceUser.validateRequest(previewFormBean);
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Approval of request by user as it was submitted manually");

		return relayServiceUser.approve(regNumber, ip, email, remarks, responseBean);

	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Cancellation of request by user.");

		return relayServiceUser.reject(regNumber, ip, email, remarks, responseBean);
	}

	@PostMapping("/validate")
	public Map<Object, Object> validate(@Valid @ModelAttribute ValidateFormBean validateFormBean) {
		return relayServiceUser.validate(validateFormBean);
	}


	@GetMapping("/fetchMx")
	public Map<String, String> fetchMx(@RequestParam @NotEmpty String senderId) {

		return relayServiceUser.fetchMx(senderId);
	}
	
	@RequestMapping(value = "/manualupload")
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request) throws IOException {	
		return relayServiceUser.manualupload(manualUploadBean, request, responseBean);
	}

}
