package in.nic.ashwini.eForms.controllers;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.services.RelayServiceRO;


@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	
	private final ResponseBean responseBean;
	private final RelayServiceRO relayServiceRO;

	@Autowired
	public ReportingOfficerController(ResponseBean responseBean, RelayServiceRO relayServiceRO) {
		super();
		this.responseBean = responseBean;
		this.relayServiceRO = relayServiceRO;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam  String remarks) {
		
		return relayServiceRO.approve(regNumber, ip, email, remarks,responseBean);
		
	}
	
	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		
		return relayServiceRO.reject(regNumber, ip, email, remarks, responseBean);
		
	}
	
	
}
