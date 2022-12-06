package in.nic.ashwini.eForms.controllers;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.DnsBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.DnsService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final DnsService dnsService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public ReportingOfficerController(DnsService dnsService, ResponseBean responseBean, Util utilityService) {
		super();
		this.dnsService = dnsService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		log.info("Appriving registration no {} at RO by {}.",regNumber,email);
		responseBean.setRequestType("Approval of request by Reporting Officer");
		String formType = "dns";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		DnsBase dnsBase = dnsService.preview(regNumber);
		String dn = utilityService.findDn(dnsBase.getEmail());
		String roDn = utilityService.findDn(email);
		List<String> aliases = utilityService.aliases(email);

		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, dnsBase.getHodMobile(), dnsBase.getHodName(),"ro");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, dnsBase.getHodMobile(),
				dnsBase.getHodName(),"ro", regNumber);

		if (!dnsBase.getReqOtherRecord().equalsIgnoreCase("mx")) {
			if (utilityService.isNicEmployee(dnsBase.getEmail())
					|| (utilityService.isNicEmployee(email) && (dnsBase.getPostingState().equalsIgnoreCase("delhi")
							&& dnsBase.getEmployment().equalsIgnoreCase("central")
							&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork"))))) {
				toWhom = "Admin";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				log.info("Registration no. {} applicant is central employee and posted at Delhi and either -Nic Support Outsourced- OR NKN Forwarded to Admin", regNumber);
			} else if ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork"))
					&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
				toWhom = "Support";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_SUPPORT_TYPE;
				nextStatus = Constants.STATUS_SUPPORT_PENDING;
				log.info("Registration no. {} applicant & RO is either Nic Support Outsourced OR NKN Forwarded to Support", regNumber);
			} else if (dnsBase.getEmployment().equalsIgnoreCase("Others")
					&& dnsBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
					&& dnsBase.getPostingState().equalsIgnoreCase("maharashtra")
					&& dnsBase.getCity().equalsIgnoreCase("pune") && (dnsBase.getAddress().toLowerCase().contains("ndc")
							|| dnsBase.getAddress().toLowerCase().contains("national data center"))) {
				toWhom = "Coordinator";
				daEmail = utilityService.fetchNdcPuneCoord();

				if (aliases.contains(daEmail)) {
					recipientType = Constants.STATUS_ADMIN_TYPE;
					nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				} else {
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				}
				
				log.info("For the tag :mx: Registration no. {} applicant ORG:{} Posting:{} is Forwarded to Co-Ordinator",dnsBase.getOrganization(), dnsBase.getPostingState(), regNumber);
				
			} else {

				ModelMapper modelMapper = new ModelMapper();
				OrganizationBean org = modelMapper.map(dnsBase, OrganizationBean.class);

				Set<String> co = utilityService.fetchCoordinators(org);
				if (co != null && co.size() > 0) {
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
				log.info("For the tag :mx: Registration no. {} is Forwarded to {} pending as {}",regNumber, toWhom, recipientType);
			}
		} else {
			if (dnsBase.getEmployment().equalsIgnoreCase("Others")
					&& dnsBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
					&& dnsBase.getPostingState().equalsIgnoreCase("maharashtra")
					&& dnsBase.getCity().equalsIgnoreCase("pune") && (dnsBase.getAddress().toLowerCase().contains("ndc")
							|| dnsBase.getAddress().toLowerCase().contains("national data center"))) {
				toWhom = "Coordinator";
				daEmail = utilityService.fetchNdcPuneCoord();
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				log.info("Registration no. {} applicant ORG:{} Posting:{} is Forwarded to Co-Ordinator",dnsBase.getOrganization(), dnsBase.getPostingState(), regNumber);
			} else {

				ModelMapper modelMapper = new ModelMapper();
				OrganizationBean org = modelMapper.map(dnsBase, OrganizationBean.class);

				Set<String> co = utilityService.fetchCoordinators(org);
				if (co != null && co.size() > 0) {
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
				log.info("Registration no. {} is Forwarded to {} pending as {}",regNumber, toWhom, recipientType);
			}
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
		log.info("Rejecting registration no {} at RO by {}.",regNumber,email);
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "dns";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		DnsBase dnsBase = dnsService.preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, dnsBase.getHodMobile(),
				dnsBase.getHodName(),"ro");

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, dnsBase.getHodMobile(),
				dnsBase.getHodName(),"ro", regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_CA_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_CA_REJECTED);
		finalAuditTrack.setToEmail("");

		if (dnsService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

}
