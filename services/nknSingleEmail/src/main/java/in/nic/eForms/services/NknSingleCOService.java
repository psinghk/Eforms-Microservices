package in.nic.eForms.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;

import in.nic.eForms.controllers.AdminController;
import in.nic.eForms.entities.GeneratePdf;
import in.nic.eForms.entities.NknSingleBase;
import in.nic.eForms.entities.NknSingleEmpCoord;
import in.nic.eForms.entities.NknSingleSha;
import in.nic.eForms.models.ErrorResponseForOrganizationValidationDto;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.GeneratePdfBean;
import in.nic.eForms.models.HodDetailsDto;
import in.nic.eForms.models.MobileAndName;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.OrganizationDto;
import in.nic.eForms.models.PreviewFormBean;
import in.nic.eForms.models.ProfileDto;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.models.UserForCreate;
import in.nic.eForms.models.UserForSearch;
import in.nic.eForms.repositories.GeneratePdfRepository;
import in.nic.eForms.repositories.NknSingleBaseRepo;
import in.nic.eForms.repositories.NknSingleEmpCoordRepo;
import in.nic.eForms.repositories.NknSingleShaRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
@Slf4j
@Service
public class NknSingleCOService {
	private final NknSingleBaseRepo nknSingleBaseRepo;
	private final NknSingleEmpCoordRepo nknSingleEmpCoordRepo;
	private final Util utilityService;

	@Autowired
	public NknSingleCOService(NknSingleBaseRepo nknSingleBaseRepo, NknSingleEmpCoordRepo nknSingleEmpCoordRepo, Util utilityService) {
		super();
		this.nknSingleBaseRepo = nknSingleBaseRepo;
		this.utilityService = utilityService;
		this.nknSingleEmpCoordRepo = nknSingleEmpCoordRepo;

	}
	
	public NknSingleBase fetchDetails(String regNo) {
		return nknSingleBaseRepo.findByRegistrationNo(regNo);
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

	
	public Set<String> findBo(NknSingleBase nknSingleBase) {
		Set<String> bos = new HashSet<>();
		if (nknSingleBase.getEmployment().trim().equalsIgnoreCase("central")
				|| nknSingleBase.getEmployment().trim().equalsIgnoreCase("ut")) {

			bos = nknSingleEmpCoordRepo.fetchByMinistry(nknSingleBase.getEmployment(), nknSingleBase.getMinistry(),
					nknSingleBase.getDepartment());
		} else if (nknSingleBase.getEmployment().trim().equalsIgnoreCase("state")) {
			bos = nknSingleEmpCoordRepo.fetchByState(nknSingleBase.getEmployment(), nknSingleBase.getState(),
					nknSingleBase.getDepartment());
		} else {
			bos = nknSingleEmpCoordRepo.fetchByOrg(nknSingleBase.getEmployment(), nknSingleBase.getOrganization());
		}
		return bos;
	}

	public ResponseBean approveCO(String regNumber, String ip, String email, String remarks,
			ResponseBean responseBean) {
		responseBean.setRequestType("Approval of request by NIC Coordinator");
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		NknSingleBase nknSingleBase = fetchDetails(regNumber);
		ModelMapper modelMapper = new ModelMapper();
		OrganizationBean org = modelMapper.map(nknSingleBase, OrganizationBean.class);
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),
				"coordinator");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(),"coordinator", regNumber);

		String preferred_email1 = "";
		String preferred_email2 = "";

		preferred_email1 = nknSingleBase.getPreferred_email1();
		preferred_email2 = nknSingleBase.getPreferred_email2();

		Set<String> bo = findBo(nknSingleBase);
		Set<String> domains = new HashSet<>();
		for (String boId : bo) {
			System.out.println("Bo id ::: " + boId);
			List<String> domain = utilityService.findDomains(boId);
            if (domain.size() > 0)
            {
                domains.addAll(domain);
            }
		}
		Set<String> da = utilityService.fetchDAs(org);
		daEmail = String.join(",", da);
		if (da != null && da.size() > 0) {

			if (daEmail.equalsIgnoreCase("kaushal.shailender@nic.in")) {

				String pref1_domain = preferred_email1.split("@")[1].trim().toLowerCase();
				String pref2_domain = preferred_email2.split("@")[1].trim().toLowerCase();
				if (domains.size() > 0) {
					if (domains.contains(pref1_domain) && domains.contains(pref2_domain)) {
						toWhom = "Delegated Admin";
						recipientType = Constants.STATUS_DA_TYPE;
						nextStatus = Constants.STATUS_DA_PENDING;
						daEmail = String.join(",", da);
					} else {
						nextStatus = Constants.STATUS_MAILADMIN_PENDING;
						toWhom = "Admin";
						daEmail = "support@gov.in";
						recipientType = Constants.STATUS_ADMIN_TYPE;
					}
				} else {
					toWhom = "Delegated Admin";
					recipientType = Constants.STATUS_DA_TYPE;
					nextStatus = Constants.STATUS_DA_PENDING;
					daEmail = String.join(",", da);
				}

			} else if (org.getEmployment().equalsIgnoreCase("State") && org.getState().equalsIgnoreCase("Punjab")) {

				String pref1_domain = preferred_email1.split("@")[1].trim().toLowerCase();
				String pref2_domain = preferred_email2.split("@")[1].trim().toLowerCase();

				if (domains.size() > 0) {
					if (domains.contains(pref1_domain) && domains.contains(pref2_domain)) {
						toWhom = "Delegated Admin";
						recipientType = Constants.STATUS_DA_TYPE;
						nextStatus = Constants.STATUS_DA_PENDING;
						daEmail = String.join(",", da);
					} else {
						nextStatus = Constants.STATUS_MAILADMIN_PENDING;
						toWhom = "Admin";
						daEmail = "support@gov.in";
						recipientType = Constants.STATUS_ADMIN_TYPE;
					}
				} else {
					toWhom = "Delegated Admin";
					recipientType = Constants.STATUS_DA_TYPE;
					nextStatus = Constants.STATUS_DA_PENDING;
					daEmail = String.join(",", da);
				}

			} else {
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				toWhom = "Admin";
				daEmail = "support@gov.in";
				recipientType = Constants.STATUS_ADMIN_TYPE;
			}

		} else {
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
			toWhom = "Support";
			daEmail = "support@gov.in";
			recipientType = Constants.STATUS_SUPPORT_TYPE;
		}

		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);

		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			 log.info("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom + "(" + daEmail + ")");
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

	public ResponseBean rejectCO(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),
				"coordinator");

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, mobileAndName.getMobile(),
				mobileAndName.getName(),"coordinator", regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_COORDINATOR_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_COORDINATOR_REJECTED);
		finalAuditTrack.setToEmail("");

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
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

	public ResponseBean pullCO(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Coordinator is reverting the request to support.");
		String coordRemarks = "Reverted by coordinator " + email + " to iNOC support.";
		if (!remarks.isEmpty()) {
			coordRemarks += "~User Remarks - " + remarks;
		}
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		MobileAndName mobileAndName = utilityService.fetchMobileAndNameFromLdap(email);

		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";
		status = utilityService.initializeStatusTable(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"coordinator");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTableForReverting(ip, email, formType, remarks, mobileAndName.getMobile(), mobileAndName.getName(),"coordinator", regNumber);
		status.setSubmissionType("reverted_c_s");

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

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			 log.info("Application (" + regNumber + ") has been pulled Successfully to " +
			 toWhom + "(" + daEmail + ")");
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
}
