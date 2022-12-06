package in.nic.ashwini.eForms.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.BulkEmailBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.BulkEmailService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequestMapping("/ro")
@RestController
public class ReportingOfficerController {
	private final BulkEmailService bulkEmailService;
	private final ResponseBean responseBean;
	private final Util utilityService;

	@Autowired
	public ReportingOfficerController(BulkEmailService bulkEmailService, ResponseBean responseBean,
			Util utilityService) {
		super();
		this.bulkEmailService = bulkEmailService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam String remarks) {
		responseBean.setRequestType("Approval of request by Reporting Officer");
		String formType = "bulk";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		BulkEmailBase bulkEmailBase = bulkEmailService.preview(regNumber);
		String dn = utilityService.findDn(bulkEmailBase.getEmail());
		String roDn = utilityService.findDn(email);
		List<String> aliases = utilityService.aliases(email);

		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = initializeStatusTable(ip, email, formType, remarks, bulkEmailBase.getHodMobile(),
				bulkEmailBase.getHodName());
		finalAuditTrack = initializeFinalAuditTrackTable(ip, email, formType, remarks, bulkEmailBase.getHodMobile(),
				bulkEmailBase.getHodName(), regNumber);

		if (utilityService.isNicEmployee(email)) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if (utilityService.isNicEmployee(email) && (bulkEmailBase.getPostingState().equalsIgnoreCase("delhi"))
				&& bulkEmailBase.getEmployment().equalsIgnoreCase("central")
				&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork"))) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} else if ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork"))
				&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
			toWhom = "Support";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
		} /*
			 * else if (bulkEmailBase.getPreferredEmail1().contains("gem.gov.in") &&
			 * bulkEmailBase.getPreferredEmail2().contains("gem.gov.in")) { toWhom =
			 * "Coordinator"; // daEmail = String.join(",", co); recipientType =
			 * Constants.STATUS_COORDINATOR_TYPE; nextStatus =
			 * Constants.STATUS_COORDINATOR_PENDING; }
			 */ else if (bulkEmailBase.getState().equalsIgnoreCase("Maharashtra")
				&& bulkEmailBase.getEmployment().equalsIgnoreCase("DataCentre and Webservices")
				&& bulkEmailBase.getAddress().toLowerCase().contains("ndc")
				|| bulkEmailBase.getAddress().toLowerCase().contains("national data center")
						&& bulkEmailBase.getCity().equalsIgnoreCase("pune")
						&& bulkEmailBase.getEmployment().equalsIgnoreCase("Others")) {
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();
		} else if ((bulkEmailBase.getState().equalsIgnoreCase("Himachal")
				&& bulkEmailBase.getEmployment().equalsIgnoreCase("state")
				|| bulkEmailBase.getPostingState().equalsIgnoreCase("Himachal Pradesh"))
				&& bulkEmailBase.getEmployment().equalsIgnoreCase("Others")) {
			if (bulkEmailBase.getEmpType().equals("emp_regular")) {
				// pending condition
				/*
				 * if() {
				 * 
				 * }esle{
				 * 
				 * }
				 */
			} else {

				toWhom = "Coordinator";
				daEmail = String.join(",");
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
		}

		else if (bulkEmailBase.getEmployment().equalsIgnoreCase("state")
				&& (bulkEmailBase.getState().equalsIgnoreCase("punjab"))) {
			if (bulkEmailBase.getPostingState().equalsIgnoreCase("punjab")) {
				// Fetch coordinators from Punjab nodal officers table based on city
				toWhom = "Coordinator";
				List<String> punjabCoords = utilityService.fetchPunjabCoords(bulkEmailBase.getCity());
				if (punjabCoords.size() > 0) {
					daEmail = String.join(",", punjabCoords);
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				} else {
					// Fetch DA from EmpCoord Table. IF DA Exists
					ModelMapper modelMapper = new ModelMapper();
					OrganizationBean org = modelMapper.map(bulkEmailBase, OrganizationBean.class);

					Set<String> da = utilityService.fetchDAs(org);
					if (da != null && da.size() > 0) {

						if (bulkEmailBase.getEmpType().equals("emp_regular")) {
							// pending condition
							/*
							 * if() {
							 * 
							 * }esle{
							 * 
							 * }
							 */
						} else {

							toWhom = "Coordinator";
							daEmail = String.join(",");
							recipientType = Constants.STATUS_COORDINATOR_TYPE;
							nextStatus = Constants.STATUS_COORDINATOR_PENDING;
						}
					} else {
						toWhom = "Support";
						daEmail = Constants.MAILADMIN_EMAIL;
						recipientType = Constants.STATUS_SUPPORT_TYPE;
						nextStatus = Constants.STATUS_SUPPORT_PENDING;

					}
				}
			}
		}

		else if (bulkEmailBase.getEmployment().equalsIgnoreCase("state")
				&& bulkEmailBase.getState().equalsIgnoreCase("maharastra")
				&& bulkEmailBase.getPostingState().equalsIgnoreCase("maharastra")) {

			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(bulkEmailBase, OrganizationBean.class);

			Set<String> da = utilityService.fetchDAs(org);
			if (da != null && da.size() > 0) {
				if (bulkEmailBase.getEmpType().equals("emp_regular")) {
					// pending condition
					/*
					 * if() {
					 * 
					 * }esle{
					 * 
					 * }
					 */
				} else {

					toWhom = "Coordinator";
					daEmail = String.join(",");
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				}

			} else {
				Set<String> co = utilityService.fetchCoordinators(org);
				if (co != null && co.size() > 0) {
					// peinding cord
					toWhom = "Coordinator";
					daEmail = String.join(",", co);
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				} else {
					// pending support
					toWhom = "Support";
					daEmail = Constants.MAILADMIN_EMAIL;
					recipientType = Constants.STATUS_SUPPORT_TYPE;
					nextStatus = Constants.STATUS_SUPPORT_PENDING;
				}
			}

		}

		else if (bulkEmailBase.getEmployment().equalsIgnoreCase("central")
				&& !bulkEmailBase.getPostingState().equalsIgnoreCase("Delhi")) {
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(bulkEmailBase, OrganizationBean.class);

			Set<String> co = utilityService.fetchCoordinators(org);
			if (co != null && co.size() > 0) {
				toWhom = "Coordinator";
				daEmail = String.join(",");
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			} else {
				// IF
				// coordinator found for dept -other

				if (org.getDepartment().equalsIgnoreCase("other")) {
					toWhom = "Coordinator";
					daEmail = String.join(",");
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				} else {
					toWhom = "Support";
					daEmail = Constants.MAILADMIN_EMAIL;
					recipientType = Constants.STATUS_SUPPORT_TYPE;
					nextStatus = Constants.STATUS_SUPPORT_PENDING;

				}

			}

		}

		else {

			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(bulkEmailBase, OrganizationBean.class);

			Set<String> da = utilityService.fetchDAs(org);
			List<String> co = new ArrayList<String>(utilityService.fetchCoordinators(org));
			if (da != null && da.size() > 0) {
				if (bulkEmailBase.getEmpType().equals("emp_regular")) {
					// pending condition
					/*
					 * if() {
					 * 
					 * }esle{
					 * 
					 * }
					 */
				} else {

					toWhom = "Coordinator";
					daEmail = String.join(",");
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				}
			} else {
				if ((co != null && co.size() > 0)
						&& (email.equals(co.get(0)) || bulkEmailBase.getEmail().equals(co.get(0)))) {
					toWhom = "Support";
					daEmail = Constants.MAILADMIN_EMAIL;
					recipientType = Constants.STATUS_SUPPORT_TYPE;
					nextStatus = Constants.STATUS_SUPPORT_PENDING;
				} else {
					toWhom = "Coordinator";
					daEmail = String.join(",", co);
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				}
			}
		}

		
		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (bulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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
		String formType = "bulk";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		BulkEmailBase bulkEmailBase = bulkEmailService.preview(regNumber);

		status = initializeStatusTable(ip, email, formType, remarks, bulkEmailBase.getHodMobile(),
				bulkEmailBase.getHodName());

		finalAuditTrack = initializeFinalAuditTrackTable(ip, email, formType, remarks, bulkEmailBase.getHodMobile(),
				bulkEmailBase.getHodName(), regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_CA_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_CA_REJECTED);
		finalAuditTrack.setToEmail("");

		if (bulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	private Status initializeStatusTable(String ip, String email, String formType, String remarks, String mobile,
			String name) {
		Status status = new Status();
		status.setFormType(formType);
		status.setIp(ip);
		status.setSenderIp(ip);
		status.setOnholdStatus("n");
		status.setFinalId("");
		status.setRemarks(remarks);
		status.setSenderType(Constants.STATUS_CA_TYPE);
		status.setSender(email);
		status.setSenderEmail(email);
		LocalDateTime currentTime = LocalDateTime.now();
		status.setCreatedon(currentTime);
		status.setSenderDatetime(currentTime);
		status.setSenderName(name);
		status.setSenderMobile(mobile);
		return status;
	}

	private FinalAuditTrack initializeFinalAuditTrackTable(String ip, String email, String formType, String remarks,
			String mobile, String name, String regNumber) {
		FinalAuditTrack finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		finalAuditTrack.setCaIp(ip);
		finalAuditTrack.setCaRemarks(remarks);
		finalAuditTrack.setCaMobile(mobile);
		finalAuditTrack.setCaName(name);
		finalAuditTrack.setCaEmail(email);
		LocalDateTime currentTime = LocalDateTime.now();
		finalAuditTrack.setCaDatetime(currentTime);
		finalAuditTrack.setToDatetime(currentTime);
		return finalAuditTrack;
	}
}
