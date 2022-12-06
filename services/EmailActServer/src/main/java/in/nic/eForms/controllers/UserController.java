package in.nic.eForms.controllers;

import java.io.IOException;

import java.text.ParseException;
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
import in.nic.eForms.models.ResponseData;
import in.nic.eForms.models.ValidateFormBean;
import in.nic.eForms.services.EmailActUserService;

@Validated
@RequestMapping("/user")
@RestController
public class UserController {
	private final EmailActUserService emailActServiceUser;
	private final ResponseBean responseBean;
	

	@Autowired
	public UserController(EmailActUserService emailActServiceUser, ResponseBean responseBean ) {
		super();
		this.emailActServiceUser = emailActServiceUser;
		this.responseBean = responseBean;
	}

	@RequestMapping(value = "/submitRequest")
	public ResponseBean submitRequest(@ModelAttribute PreviewFormBean previewFormBean,@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,@RequestParam("submissionType") @NotEmpty String submissionType) {

		responseBean.setRequestType("Submission of request");
		return emailActServiceUser.submitRequest(previewFormBean, ip, email, submissionType);
	}

	@RequestMapping(value = "/validateRequest")
	public Map<String, Object> validateRequest(@Valid @ModelAttribute PreviewFormBean previewFormBean ,String email) {

		return emailActServiceUser.validateRequest(previewFormBean,email);
	}
	
	@RequestMapping(value = "/validate") 
	public Map<Object, Object> validate(@Valid @ModelAttribute ValidateFormBean validateFormBean) {

 		return emailActServiceUser.validate(validateFormBean);
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, String remarks) {
		responseBean.setRequestType("Cancellation of request by user.");

		return emailActServiceUser.reject(regNumber, ip, email, remarks, responseBean);
	}
	@RequestMapping(value = "/manualupload")
	public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request) throws IOException {

		return emailActServiceUser.manualupload(manualUploadBean, request, responseBean);
	}
	@RequestMapping(value = "/checkExistPreferredEmail")
	public Map<String, Object> checkExistPreferredEmail1(@RequestParam("preferredEmail") String preferredEmail){

		return emailActServiceUser.checkExistPreferredEmail1(preferredEmail);
	}	
}
