package in.nic.ashwini.eForms.controllers;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.services.RelayServiceSupport;

@Validated
@RequestMapping("/support")
@RestController
public class SupportController {
	private final RelayServiceSupport relayServiceSupport;
	private final ResponseBean responseBean;
	

	@Autowired
	public SupportController(RelayServiceSupport relayServiceSupport, ResponseBean responseBean) {
		super();
		this.relayServiceSupport = relayServiceSupport;
		this.responseBean = responseBean;
		
	}

	@RequestMapping(value = "/forwardToCoordinator")
	public ResponseBean forwardToCoordinator(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam  String remarks,
			@RequestParam @NotEmpty String coordEmail) {
		
		return relayServiceSupport.forwardToCoordinator(regNumber, ip, email, remarks, coordEmail,responseBean);
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		
		return relayServiceSupport.reject(regNumber, ip, email, remarks, responseBean);
	}

//	@RequestMapping(value = "/forwardToDa")
//	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
//			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
//			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks,
//			@RequestParam @NotEmpty String daEmail) {
//		responseBean.setRequestType("Support is forwarding the request to DA");
//		String formType = "relay";
//		Status status = null;
//		FinalAuditTrack finalAuditTrack = null;
//		MobileAndName mobileAndName = null;
//		if (!utilityService.isSupportEmail(email)) {
//			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
//		}
//		String toWhom = "";
//		String recipientType = "";
//		String nextStatus = "";
//
//		if (mobileAndName != null) {
//			status = initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
//					mobileAndName.getName());
//			finalAuditTrack = initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
//					mobileAndName.getName(), regNumber);
//		} else {
//			status = initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support");
//			finalAuditTrack = initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support",
//					regNumber);
//		}
//
//		toWhom = "Delegated Admin";
//		recipientType = Constants.STATUS_DA_TYPE;
//		nextStatus = Constants.STATUS_DA_PENDING;
//
//		status.setRegistrationNo(regNumber);
//		status.setRecipientType(recipientType);
//		status.setStatus(nextStatus);
//		status.setRecipient(daEmail);
//
//		finalAuditTrack.setStatus(nextStatus);
//		finalAuditTrack.setToEmail(daEmail);
//
//		if (relayService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
//			log.info("Application (" + regNumber + ") has been forwarded Successfully to " + toWhom + "(" + daEmail
//					+ ")");
//			responseBean.setStatus("Application (" + regNumber + ") has been forwarded Successfully to " + toWhom + "("
//					+ daEmail + ")");
//			responseBean.setRegNumber(regNumber);
//		} else {
//			log.debug("Something went wrong. Please try again after sometime.");
//			responseBean.setStatus("Something went wrong. Please try again after sometime.");
//			responseBean.setRegNumber(regNumber);
//		}
//		return responseBean;
//	}

	@RequestMapping(value = "/forwardToAdmin")
	public ResponseBean forwardToAdmin(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam String remarks) {

		return relayServiceSupport.forwardToAdmin(regNumber, ip, email, remarks, responseBean);
	}

	@RequestMapping(value = "/pull")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email) {
		
		return relayServiceSupport.pull(regNumber, ip, email, responseBean);
	}

	

}
