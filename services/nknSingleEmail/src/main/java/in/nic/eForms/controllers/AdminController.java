package in.nic.eForms.controllers;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.eForms.entities.NknSingleBase;
import in.nic.eForms.models.AdminFormBean;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.MobileAndName;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.UserForCreate;
import in.nic.eForms.services.NknSingleAdminService;
import in.nic.eForms.services.NknSingleService;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;


@RequestMapping("/admin")
@Validated
@RestController
public class AdminController {

	private final NknSingleAdminService nknSingleAdminService;
	private final ResponseBean responseBean;

	@Autowired
	public AdminController(NknSingleAdminService nknSingleAdminService, ResponseBean responseBean) {
		super();
		this.nknSingleAdminService = nknSingleAdminService;
		this.responseBean = responseBean;
	}

	
//	@RequestMapping(value = "/approve")
//	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,@RequestParam("po") @NotEmpty String po,@RequestParam("bo") @NotEmpty String bo,@RequestParam("domain") @NotEmpty  String domain,
//			@RequestParam("email") @NotEmpty String email,@RequestParam("finalId") @NotEmpty String finalId,@RequestParam("primaryId") @NotEmpty String primaryId, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) throws ParseException {
//		responseBean.setRequestType("Completion of request by Admin");
//		return nknSingleAdminService.approveAdmin(regNumber, ip, po, bo, domain, email, finalId, primaryId, remarks, responseBean);
//	}
	
	@RequestMapping(value = "/validate")
	public Map<String,Object> validate(@Valid @ModelAttribute AdminFormBean adminBean) {
		return nknSingleAdminService.validate(adminBean);
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@Valid @ModelAttribute AdminFormBean adminFormBean,@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty String remarks) throws ParseException {
		responseBean.setRequestType("Completion of request by Admin");
		return nknSingleAdminService.approveAdmin(regNumber, ip, adminFormBean.getPo(), adminFormBean.getBo(), adminFormBean.getDomain(), email, adminFormBean.getFinalId(), adminFormBean.getPrimaryId(), remarks, responseBean);
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return nknSingleAdminService.rejectAdmin(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return nknSingleAdminService.forwardToDaAdmin(regNumber, ip, email, remarks, responseBean);
	}

	
	@RequestMapping(value = "/revert")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		return nknSingleAdminService.pullAdmin(regNumber, ip, email, remarks, responseBean);
	}

}
