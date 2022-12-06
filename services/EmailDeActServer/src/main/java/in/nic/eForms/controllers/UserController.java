package in.nic.eForms.controllers;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.nic.eForms.models.ManualUploadBean;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.services.EmailDeActUserService;


@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	private final EmailDeActUserService emailDeActUserService;
	private final ResponseBean responseBean;

	@Autowired
	public UserController(EmailDeActUserService emailDeActServiceUser, ResponseBean responseBean) {
		super();
		this.emailDeActUserService = emailDeActServiceUser;
		this.responseBean = responseBean;
	}

	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@RequestBody PreviewFormBean previewFormBean,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam("submissionType") @NotEmpty String submissionType) {

		responseBean.setRequestType("Submission of request");
		return emailDeActUserService.submitRequest(previewFormBean, ip, email, submissionType);
	}

	@RequestMapping(value = "/validateRequest")
	public Map<String, Object> validateRequest(@Valid @RequestBody PreviewFormBean previewFormBean ,String email) {

		return emailDeActUserService.validateRequest(previewFormBean,email);
	}
	
	@RequestMapping(value = "/validate")
	public Map<String, Object> validate(@RequestParam("preferredEmail") @NotEmpty String preferredEmail) {

		return emailDeActUserService.validate(preferredEmail);
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, String remarks) {
		responseBean.setRequestType("Cancellation of request by user.");

		return emailDeActUserService.reject(regNumber, ip, email, remarks, responseBean);
	}
	
	@RequestMapping(value = "/manualupload")
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request) throws IOException {

		return emailDeActUserService.manualupload(manualUploadBean, request, responseBean);
	}
	
	@RequestMapping(value = "/checkExistPreferredEmail")
	public Map<String, Object> checkExistPreferredEmail1(@RequestParam("preferredEmail") String preferredEmail){

		return emailDeActUserService.checkExistPreferredEmail1(preferredEmail);
	}	
	
}
