package in.nic.ashwini.eForms.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.NknBulkEmailBase;
import in.nic.ashwini.eForms.entities.BulkUsers;
import in.nic.ashwini.eForms.models.AdminBean;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.MobileAndName;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.OrganizationDto;
import in.nic.ashwini.eForms.models.Po;
import in.nic.ashwini.eForms.models.Po1;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.services.AdminService;
import in.nic.ashwini.eForms.services.NknBulkEmailService;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin")
@Validated
@RestController
public class AdminController {

	private final NknBulkEmailService nknBulkEmailService;
	private final ResponseBean responseBean;
	private final Util utilityService;
	//private BulkUsers bulkUsers;
	private final AdminService adminService;

	@Autowired
	public AdminController(NknBulkEmailService nknBulkEmailService, ResponseBean responseBean, Util utilityService,AdminService adminService) {
		super();
		this.nknBulkEmailService = nknBulkEmailService;
		this.responseBean = responseBean;
		this.utilityService = utilityService;
		this.adminService = adminService;
	}

	@RequestMapping(value = "/approve")
	public ResponseBean approve(@Valid @RequestBody AdminBean adminBean,@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam  String remarks) {
		responseBean.setRequestType("Completion of request by Admin");
		String formType = "nkn_bulk";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		Map<String, Object> map = validateRequest(adminBean, regNumber, ip,email);
		adminService.approveMethod(adminBean,regNumber);

		String daEmail = "";
		String recipientType = "";

		if (mobileAndName != null) {
			status = initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName());
			finalAuditTrack = initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName(), regNumber);
			
		} else {
			status = initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support");
			finalAuditTrack = initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support",
					regNumber);
		
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(Constants.STATUS_COMPLETED);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(Constants.STATUS_COMPLETED);
		finalAuditTrack.setToEmail(daEmail);

		if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") completed Successfully.");
			responseBean.setStatus("Application (" + regNumber + ") completed Successfully.");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}
	
	
	public Map<String, Object> validateRequest(@Valid @RequestBody AdminBean adminBean,@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email) {
		
		List<BulkUsers> bulkUsers=nknBulkEmailService.fetchBulkUserTrack(regNumber);
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> finalmap = new HashMap<>();
		OrganizationDto organizationDetails = new OrganizationDto();
		
		
		for (BulkUsers bulkUsers1 : bulkUsers) {
		String primaryEmail = bulkUsers1.getUid()+ "@" + adminBean.getDomain();
		String mailEquiv = bulkUsers1.getUid() + "@nic.in";
		if (adminBean.getDescription() == null || !adminBean.getDescription().equals("")) {
			log.debug("Selected Description should be ");
			map.put("descriptionError", "Selected Description should be ");
		}
		List<Po1> listPo1 = utilityService.fetchPos("o=nic.in,dc=nic,dc=in");
		List<String> listPo = new ArrayList<String>();
		for ( Po1 obj : listPo1) {
			listPo.addAll(obj.getPo());
		}
		if (adminBean.getPo() == null || !listPo.contains(adminBean.getPo())) {
			log.debug("Selected Po of Birth should be ");
			map.put("poError", "Selected Po should be ");
		}
		
		
		List<Po>listBo1=utilityService.fetchBos("o=nic.in,dc=nic,dc=in");
		List<String>listBo=new ArrayList<String>();
		for(Po poObj:listBo1)
		{
			listBo.addAll(poObj.getBo());
		}
		//!listBo.contains(adminBean.getBo()).contains(adminBean.getBo()))
		if (adminBean.getPo() == null || adminBean.getBo() == null || !listBo.contains(adminBean.getBo())) {
			log.debug("Selected Bo of Birth should be ");
			map.put("boError", "Selected Bo should be ");
		}
		if (adminBean.getPo() == null || adminBean.getBo() == null ||adminBean.getDomain() == null || !utilityService.fetchDomains(adminBean.getBo()).contains(adminBean.getDomain())) {
			log.debug("Selected Domain should be ");
			map.put("domainError", "Selected Domain should be ");
		}
	
		if (adminBean.getStatRemarks() == null || !utilityService.dobValidation(adminBean.getStatRemarks()).equals("")) {
			log.debug("Selected StatRemarks should be ");
			map.put("statRemarksError", "Selected StatRemarks should be ");
		}
		}
		
		return map;
	}
	

	@RequestMapping(value = "/reject")
	public ResponseBean reject(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @NotEmpty @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Rejection of request by Admin.");
		String formType = "nkn_bulk";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		List<BulkUsers> bulkUsers=null;
		
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		if (mobileAndName != null) {
			status = initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(),
					mobileAndName.getName());
			finalAuditTrack = initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(), regNumber);
			bulkUsers = initializeBulkUserTable(ip, email, formType, remarks, "", "iNOC Support",
					regNumber);
		} else {
			status = initializeStatusTable(ip, email, formType, remarks, "", "iNOC Support");
			finalAuditTrack = initializeFinalAuditTrackTable(ip, email, formType, remarks, "", "iNOC Support", regNumber);
			bulkUsers = initializeBulkUserTable(ip, email, formType, remarks, "", "iNOC Support",
					regNumber);
		}
		
		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.MAIL_ADMIN_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.MAIL_ADMIN_REJECTED);
		finalAuditTrack.setToEmail("");

		if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	@RequestMapping(value = "/forwardToDa")
	public ResponseBean forwardToDa(@RequestParam @NotEmpty String regNumber,
			@RequestParam("clientIp") @NotEmpty String ip, @RequestParam("email") @NotEmpty String email,
			@RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Admin is forwarding the request to DA");
		String adminRemarks = "Forwarded by Admin " + email + " to Delegated Admin.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "nkn_bulk";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		NknBulkEmailBase nknBulkEmailBase = nknBulkEmailService.preview(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(nknBulkEmailBase, OrganizationBean.class);
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		LocalDateTime currentTime = null;
		status = new Status();
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		if (mobileAndName != null) {
			status.setSenderName(mobileAndName.getName());
			status.setSenderMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminName(mobileAndName.getName());
		} else {
			status.setSenderName("iNOC Support");
			status.setSenderMobile("");
			finalAuditTrack.setAdminMobile("");
			finalAuditTrack.setAdminName("iNOC Support");
		}

		status.setFormType(formType);
		status.setIp(ip);
		status.setSenderIp(ip);
		status.setOnholdStatus("n");
		status.setFinalId("");
		status.setRemarks(adminRemarks);
		status.setSubmissionType("forwarded_m_d");
		status.setSenderType(Constants.STATUS_ADMIN_TYPE);
		status.setSender(email);
		status.setSenderEmail(email);
		currentTime = LocalDateTime.now();
		status.setCreatedon(currentTime);
		status.setSenderDatetime(currentTime);

		finalAuditTrack.setAdminIp(ip);
		finalAuditTrack.setAdminRemarks(adminRemarks);
		finalAuditTrack.setAdminEmail(email);
		finalAuditTrack.setAdminDatetime(currentTime);
		finalAuditTrack.setToDatetime(currentTime);

		Set<String> da = utilityService.fetchDAs(org);

		if (da != null && da.size() > 0) {
			toWhom = "Delegated Admin";
			recipientType = Constants.STATUS_DA_TYPE;
			nextStatus = Constants.STATUS_DA_PENDING;
			daEmail = String.join(",", da);

			status.setRegistrationNo(regNumber.toUpperCase());
			status.setRecipientType(recipientType);
			status.setStatus(nextStatus);
			status.setRecipient(daEmail);

			finalAuditTrack.setStatus(nextStatus);
			finalAuditTrack.setToEmail(daEmail);

			if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "(" + daEmail
						+ ")");
				responseBean.setStatus("Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "("
						+ daEmail + ")");
				responseBean.setRegNumber(regNumber);
			} else {
				log.debug("Something went wrong. Please try again after sometime.");
				responseBean.setStatus("Something went wrong. Please try again after sometime.");
				responseBean.setRegNumber(regNumber);
			}
		} else {
			log.debug("There are no DAs for this request(Organization Details)");
			responseBean.setStatus("There are no DAs for this request(Organization Details)");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}

	@RequestMapping(value = "/revert")
	public ResponseBean pull(@RequestParam @NotEmpty String regNumber, @RequestParam("clientIp") @NotEmpty String ip,
			@RequestParam("email") @NotEmpty String email, @RequestParam @Pattern(regexp = "^[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+.[a-zA-z0-9]{2,4}$", message = "Invalid characters in remarks.") String remarks) {
		responseBean.setRequestType("Admin is reverting the request to support.");
		String adminRemarks = "Reverted by admin " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			adminRemarks += "~User Remarks - " + remarks;
		}
		String formType = "nkn_bulk";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = null;
		if (!utilityService.isSupportEmail(email)) {
			mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		}
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		LocalDateTime currentTime = null;
		status = new Status();
		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		if (mobileAndName != null) {
			status.setSenderName(mobileAndName.getName());
			status.setSenderMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminMobile(mobileAndName.getMobile());
			finalAuditTrack.setAdminName(mobileAndName.getName());
		} else {
			status.setSenderName("iNOC Support");
			status.setSenderMobile("");
			finalAuditTrack.setAdminMobile("");
			finalAuditTrack.setAdminName("iNOC Support");
		}

		status.setFormType(formType);
		status.setIp(ip);
		status.setSenderIp(ip);
		status.setOnholdStatus("n");
		status.setFinalId("");
		status.setRemarks(adminRemarks);
		status.setSubmissionType("reverted_m_s");
		status.setSenderType(Constants.STATUS_ADMIN_TYPE);
		status.setSender(email);
		status.setSenderEmail(email);
		currentTime = LocalDateTime.now();
		status.setCreatedon(currentTime);
		status.setSenderDatetime(currentTime);

		finalAuditTrack = utilityService.fetchFinalAuditTrack(regNumber);
		finalAuditTrack.setSupportIp(null);
		finalAuditTrack.setSupportRemarks(null);
		finalAuditTrack.setSupportMobile(null);
		finalAuditTrack.setSupportName(null);
		finalAuditTrack.setSupportEmail(null);
		finalAuditTrack.setSupportDatetime(null);
		finalAuditTrack.setToDatetime(currentTime);

		toWhom = "Support";
		daEmail = Constants.MAILADMIN_EMAIL;
		recipientType = Constants.STATUS_SUPPORT_TYPE;
		nextStatus = Constants.STATUS_SUPPORT_PENDING;

		status.setRegistrationNo(regNumber.toUpperCase());
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (nknBulkEmailService.updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			log.info("Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "(" + daEmail + ")");
			responseBean.setStatus(
					"Application (" + regNumber + ") has been pulled Successfully to " + toWhom + "(" + daEmail + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
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
		status.setSenderType(Constants.STATUS_ADMIN_TYPE);
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
		finalAuditTrack.setAdminIp(ip);
		finalAuditTrack.setAdminRemarks(remarks);
		finalAuditTrack.setAdminMobile(mobile);
		finalAuditTrack.setAdminName(name);
		finalAuditTrack.setAdminEmail(email);
		LocalDateTime currentTime = LocalDateTime.now();
		finalAuditTrack.setAdminDatetime(currentTime);
		finalAuditTrack.setToDatetime(currentTime);
		return finalAuditTrack;
	}
	private List<BulkUsers> initializeBulkUserTable(String ip,String email, String formType, String remarks,
			String mobile, String name, String regNumber) {
		List<BulkUsers> bulkUsers = nknBulkEmailService.fetchBulkUserTrack(regNumber);
		for (BulkUsers bulkUsers1 : bulkUsers) {
			bulkUsers1.setIsrejected("y");
			bulkUsers1.setRejectremarks(remarks);
			bulkUsers1.setRejectedby(email);
			bulkUsers1.setMobile(mobile);
			LocalDateTime currentTime = LocalDateTime.now();
			
		}
		//````bulkUsers.setUpdatedon(currentTime);
		return bulkUsers;
	}

}
