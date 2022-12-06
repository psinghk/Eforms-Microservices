package in.nic.ashwini.eForms.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.WifiBase;
import in.nic.ashwini.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.models.UpdateFormBean;
import in.nic.ashwini.eForms.services.WifiService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final WifiService wifiService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public ReportingOfficerController(WifiService wifiService, ResponseBean responseBean, Util utilityService) {
		super();
		this.wifiService = wifiService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, String remarks) {
		responseBean.setRequestType("Approval of request by Reporting Officer");
		String formType = "wifi";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		WifiBase wifiBase = wifiService.preview(regNumber);
		String dn = utilityService.findDn(wifiBase.getEmail());
		String roDn = utilityService.findDn(email);
		List<String> aliases = utilityService.aliases(email);
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, wifiBase.getHodMobile(),
				wifiBase.getHodName(),"ro" ,regNumber);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, wifiBase.getHodMobile(), wifiBase.getHodName(),"ro");
		
		if (wifiBase.getEmployment().equalsIgnoreCase("state")
				&& wifiBase.getPostingState().equalsIgnoreCase("Assam")) {
			toWhom = "Coordinator";
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		}else if (wifiBase.getEmployment().equalsIgnoreCase("State") && wifiBase.getState().equalsIgnoreCase("punjab")
				&& wifiBase.getPostingState().equalsIgnoreCase("punjab")) {
			toWhom = "Coordinator";
			List<String> punjabCoords = utilityService.fetchPunjabCoords(wifiBase.getCity());
			daEmail = String.join(",", punjabCoords);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if (utilityService.isNicEmployee(email) && (wifiBase.getPostingState().equalsIgnoreCase("delhi")
				&& wifiBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(wifiBase.getEmail()) || utilityService.isNicEmployee(email)) {
			toWhom = "Admin";
			daEmail = Constants.NKN_SUPPORT_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork"))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "Wifi Support";
			daEmail = Constants.NKN_SUPPORT_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
		} else if (wifiBase.getEmployment().equalsIgnoreCase("Others")
				&& wifiBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& wifiBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& wifiBase.getCity().equalsIgnoreCase("pune") && (wifiBase.getAddress().toLowerCase().contains("ndc")
						|| wifiBase.getAddress().toLowerCase().contains("national data center"))) {
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();

			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
		}  else {

			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(wifiBase, OrganizationBean.class);

			Set<String> da = utilityService.fetchDAs(org);
			Set<String> co = utilityService.fetchCoordinators(org);
			if ((da != null && !da.isEmpty()) || (co != null && !co.isEmpty())) {
				toWhom = "Coordinator";
				daEmail = String.join(",", co);
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			} else {
				toWhom = "Support";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_SUPPORT_TYPE;
				nextStatus = Constants.STATUS_SUPPORT_PENDING;
			}
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (wifiService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom + "(" + daEmail
					+ ")");
			responseBean.setStatus("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom
					+ "(" + daEmail + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug(Constants.ERROR_MESSAGE);
			responseBean.setStatus(Constants.ERROR_MESSAGE);
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @NotEmpty String remarks) {
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "wifi";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		WifiBase wifiBase = wifiService.preview(regNumber);
		
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, wifiBase.getHodMobile(),
				wifiBase.getHodName(), "ro",regNumber);
	
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, wifiBase.getHodMobile(), wifiBase.getHodName(),"ro");
		
		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_CA_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_CA_REJECTED);
		finalAuditTrack.setToEmail("");

		if (wifiService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("{} rejected successfully.", regNumber);
			responseBean.setStatus(regNumber + " rejected successfully");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug(Constants.ERROR_MESSAGE);
			responseBean.setStatus(Constants.ERROR_MESSAGE);
			responseBean.setRegNumber("");
		}

		return responseBean;
	}
	

	
}
