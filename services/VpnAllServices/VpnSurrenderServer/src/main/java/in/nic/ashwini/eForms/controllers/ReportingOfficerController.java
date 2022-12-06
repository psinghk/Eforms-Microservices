package in.nic.ashwini.eForms.controllers;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.VpnBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.VpnBaseRepo;
import in.nic.ashwini.eForms.services.VpnPushApi;
import in.nic.ashwini.eForms.services.VpnService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final VpnService vpnService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	boolean isHod, isHog;
	VpnPushApi vpnPushApi = new VpnPushApi();

	@Autowired
	VpnBaseRepo vpnBaseRepo;

	@Autowired
	public ReportingOfficerController(VpnService vpnService, ResponseBean responseBean, Util utilityService) {
		super();
		this.vpnService = vpnService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam String remarks) {
		responseBean.setRequestType("Approval of request by Reporting Officer");
		String formType = Constants.VPN_SURRENDER_FORM_KEYWORD;;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		VpnBase vpnBase = vpnService.preview(regNumber);
		List<String> aliases = utilityService.aliases(email);
		String uid = utilityService.fetchUid(email);
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, vpnBase.getHodMobile(), vpnBase.getHodName(),"ro");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, vpnBase.getHodMobile(),
				vpnBase.getHodName(),"ro" ,regNumber);

		isHod = utilityService.isHod(uid);
		isHog = utilityService.isHog(uid);
		
		if (vpnBase.getEmployment().equalsIgnoreCase("Others")
				&& vpnBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& vpnBase.getState().equalsIgnoreCase("MAHARASHTRA") && vpnBase.getCity().equalsIgnoreCase("Pune")
				&& (vpnBase.getAddress().toLowerCase().contains("ndc")
						|| vpnBase.getAddress().toLowerCase().contains("national data center"))) {

			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			toWhom = "Coordinator";
			daEmail = Constants.VPN_COORD_EMAIL;

		}else if (isHod || isHog) {
			toWhom = "VPN Admin";
			recipientType = Constants.STATUS_DA_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			daEmail = Constants.VPN_MAILADMIN_EMAIL;
		} else {
			boolean isRoCoordinator = false;
			
			if (utilityService.isNicEmployee(vpnBase.getEmail())) {
				// checking user ro(login user) nic emp or not
				if (utilityService.isNicEmployee(email)) {
					// for RabbitMQ
					vpnPushApi.callVpnWebService();

					toWhom = "VPN Admin";
					recipientType = Constants.STATUS_DA_TYPE;
					nextStatus = Constants.STATUS_MAILADMIN_PENDING;
					daEmail = Constants.VPN_MAILADMIN_EMAIL;
				} else {
					String coordinatorEmail = fetchEmpCoordsAtCAForVPN(regNumber);
					if (!coordinatorEmail.equalsIgnoreCase("")) {
						for (String alias : aliases) {
							if (coordinatorEmail.contains(alias)) {
								isRoCoordinator = true;
								break;
							}
						}
						if (!isRoCoordinator) {
							nextStatus = Constants.STATUS_COORDINATOR_PENDING;
							daEmail = coordinatorEmail;
							toWhom = "Coordinator";
						} else {
							nextStatus = Constants.STATUS_MAILADMIN_PENDING;
							daEmail = Constants.VPN_MAILADMIN_EMAIL;
							toWhom = "Admin";
						}
					} else {
						nextStatus = Constants.STATUS_SUPPORT_PENDING;
						daEmail = Constants.VPN_MAILADMIN_EMAIL;
						toWhom = "VPN Support";

					}

				}

			} else {
				String coordinatorEmail = fetchEmpCoordsAtCAForVPN(regNumber);
				if (!coordinatorEmail.equalsIgnoreCase("")) {
					for (String alias : aliases) {
						if (coordinatorEmail.contains(alias)) {
							isRoCoordinator = true;
							break;
						}
					}
					if (!isRoCoordinator) {
						nextStatus = Constants.STATUS_COORDINATOR_PENDING;
						daEmail = coordinatorEmail;
						toWhom = "Coordinator";
					} else {
						// for RabbitMQ
						vpnPushApi.callVpnWebService();
						nextStatus = Constants.STATUS_MAILADMIN_PENDING;
						daEmail = Constants.VPN_MAILADMIN_EMAIL;
						toWhom = "Admin";
					}
				} else {
					nextStatus = Constants.STATUS_SUPPORT_PENDING;
					daEmail = Constants.VPN_MAILADMIN_EMAIL;
					toWhom = "VPN Support";

				}

			}

		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom + "(" + daEmail
					+ ")");
			responseBean.setStatus("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom
					+ "(" + daEmail + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = Constants.VPN_SURRENDER_FORM_KEYWORD;;
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		VpnBase vpnBase = vpnService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, vpnBase.getHodMobile(), vpnBase.getHodName(),"ro");

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, vpnBase.getHodMobile(),
				vpnBase.getHodName(), "ro",regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_CA_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_CA_REJECTED);
		finalAuditTrack.setToEmail("");

		if (vpnService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} rejected successfully.", regNumber);
			responseBean.setStatus(regNumber + " rejected successfully");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber("");
		}

		return responseBean;
	}

	public String fetchEmpCoordsAtCAForVPN(String registrationNo) {
		log.debug("getting coordinator Email from vpn base");
		VpnBase vpnBase = vpnBaseRepo.findByRegistrationNo(registrationNo);
		String coordEmail = "";
		coordEmail = vpnBase.getCoordinatorEmail();
		if (!coordEmail.isEmpty()) {
			return coordEmail;
		} else {
			return "";
		}
	}

	

}
