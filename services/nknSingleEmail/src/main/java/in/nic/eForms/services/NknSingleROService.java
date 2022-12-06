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
public class NknSingleROService {
	private final NknSingleBaseRepo nknSingleBaseRepo;
	private final NknSingleShaRepo nknSingleShaRepo;
	private final NknSingleEmpCoordRepo nknSingleEmpCoordRepo;
	private final Util utilityService;

	@Autowired
	public NknSingleROService(NknSingleBaseRepo nknSingleBaseRepo,
			NknSingleShaRepo nknSingleShaRepo, NknSingleEmpCoordRepo nknSingleEmpCoordRepo, Util utilityService) {
		super();
		this.nknSingleBaseRepo = nknSingleBaseRepo;
		this.utilityService = utilityService;
		this.nknSingleShaRepo = nknSingleShaRepo;
		this.nknSingleEmpCoordRepo = nknSingleEmpCoordRepo;

	}


	public NknSingleBase fetchDetails(String regNo) {
		return nknSingleBaseRepo.findByRegistrationNo(regNo);
	}


	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}


//	public Set<String> findBo(NknSingleBase nknSingleBase) {
//		Set<String> bos = new HashSet<>();
//		if (nknSingleBase.getEmployment().trim().equalsIgnoreCase("central")
//				|| nknSingleBase.getEmployment().trim().equalsIgnoreCase("ut")) {
//
//			bos = nknSingleEmpCoordRepo.fetchByMinistry(nknSingleBase.getEmployment(), nknSingleBase.getMinistry(),
//					nknSingleBase.getDepartment());
//		} else if (nknSingleBase.getEmployment().trim().equalsIgnoreCase("state")) {
//			bos = nknSingleEmpCoordRepo.fetchByState(nknSingleBase.getEmployment(), nknSingleBase.getState(),
//					nknSingleBase.getDepartment());
//		} else {
//			bos = nknSingleEmpCoordRepo.fetchByOrg(nknSingleBase.getEmployment(), nknSingleBase.getOrganization());
//		}
//		return bos;
//	}
	
	public Set<String> findBo(NknSingleBase nknSingleBase) {
		System.out.println("::::::::::findBo:::::::::::::::::");
		List<String> bo = null;
		if (nknSingleBase.getEmployment().trim().equalsIgnoreCase("central")
				|| nknSingleBase.getEmployment().trim().equalsIgnoreCase("ut")) {
			System.out.println("::::::::::findBo1:::::::::::::::::");
			bo = utilityService.fetchBoByMinistry(nknSingleBase.getEmployment(), nknSingleBase.getMinistry(),
					nknSingleBase.getDepartment());
			System.out.println("::::::::::findBo1::::::::bo:::::::::"+bo);
		} else if (nknSingleBase.getEmployment().trim().equalsIgnoreCase("state")) {
			bo = utilityService.fetchBoByState(nknSingleBase.getEmployment(), nknSingleBase.getState(),
					nknSingleBase.getDepartment());
			
		} else {
			bo = utilityService.fetchBoByOrg(nknSingleBase.getEmployment(), nknSingleBase.getOrganization());
		}
		System.out.println("bo::::::::"+bo);
		Set<String> bos = new HashSet<String>(bo);
		System.out.println("bos::::::::"+bos);
		return bos;
	}


	public ResponseBean approveRO(String regNumber, String ip, String email, String remarks, String submissionType,
			ResponseBean responseBean) {
		System.out.println("::::::::::approveRO service:::::::::::::::::");
		responseBean.setRequestType("Approval of request by Reporting Officer");
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		NknSingleBase nknSingleBase = fetchDetails(regNumber);
		Boolean isEmailAvailable = utilityService.isGovEmployee(email);
		System.out.println("::::::::::isEmailAvailable:::::::::::::::::"+isEmailAvailable);
		List<String> aliases = utilityService.aliases(email);
		System.out.println("::::::::::aliases:::::::::::::::::"+aliases);
		String daEmail = "";
		String coEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, nknSingleBase.getHodMobile(),
				nknSingleBase.getHodName(), "ro");
		System.out.println("::::::::::status:::::::::::::::::"+status);
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, nknSingleBase.getHodMobile(),
				nknSingleBase.getHodName(),"ro", regNumber);
		System.out.println("::::::::::finalAuditTrack:::::::::::::::::"+finalAuditTrack);
		String preferred_email1 = nknSingleBase.getPreferred_email1();
		System.out.println("::::::::::preferred_email1:::::::::::::::::"+preferred_email1);
		String preferred_email2 = nknSingleBase.getPreferred_email2();
		System.out.println("::::::::::preferred_email2:::::::::::::::::"+preferred_email2);
		Set<String> bo = findBo(nknSingleBase);
		System.out.println("::::::::::bo1:::::::::::::::::"+bo);
		
		Set<String> domains = new HashSet<>();
		for (String boId : bo) {
			System.out.println("Bo id ::: " + boId);
			 List<String> domain = utilityService.findDomains(boId);
			 System.out.println("::::::::::domain:::::::::::::::::"+domain);
	            if (domain.size() > 0)
	            {
	                domains.addAll(domain);
	                System.out.println("::::::::::domains:::::::::::::::::"+domains);
	            }
		}

		if (submissionType.equalsIgnoreCase("manual") && !isEmailAvailable) {
			System.out.println("::::::::::1:::::::::::::::::");
			
			toWhom = "US";
			daEmail = nknSingleBase.getUnder_sec_email();
			recipientType = Constants.STATUS_US_TYPE;
			nextStatus = Constants.STATUS_US_PENDING;
			NknSingleSha sha = createSHA(regNumber, formType, email, nknSingleBase.getUnder_sec_email());

		} else if (nknSingleBase.getEmployment().equalsIgnoreCase("Others")
				&& nknSingleBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& nknSingleBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& nknSingleBase.getCity().equalsIgnoreCase("pune")
				&& (nknSingleBase.getAddress().toLowerCase().contains("ndc")
						|| nknSingleBase.getAddress().toLowerCase().contains("national data center"))) {
			
			System.out.println("::::::::::2:::::::::::::::::");
			
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();

			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}

		} else if (preferred_email1.contains("gem.gov.in") && preferred_email2.contains("gem.gov.in")) {
			
			System.out.println("::::::::::3::::::::::::::::");
			
			String pref1_domain = preferred_email1.split("@")[1].trim().toLowerCase();
			String pref2_domain = preferred_email2.split("@")[1].trim().toLowerCase();
			if (domains.size() > 0) {
				if (domains.contains(pref1_domain) && domains.contains(pref2_domain)) {
					nextStatus = Constants.STATUS_DA_PENDING;
					toWhom = "Delegated Admin";
					daEmail = utilityService.fetchGemDAForFreeAccounts();
				} else {
					nextStatus = "coordinator_pending";
					toWhom = "Coordinator";
					daEmail = utilityService.fetchGemDAForFreeAccounts();
				}
			} else {
				nextStatus = Constants.STATUS_DA_PENDING;
				toWhom = "Delegated Admin";
				daEmail =utilityService.fetchGemDAForFreeAccounts();
			}

		} else if (utilityService.isNicEmployee(nknSingleBase.getEmail())) {
			
			System.out.println("::::::::::4:::::::::::::::::");
			toWhom = "Admin";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;

		} else if ((utilityService.isNicEmployee(email))
				&& (nknSingleBase.getPostingState().equalsIgnoreCase("delhi")
						&& nknSingleBase.getEmployment().equalsIgnoreCase("central"))
				&& (utilityService.isNicOutsourced(nknSingleBase.getEmail()))) {
			System.out.println("::::::::::5:::::::::::::::::");
			toWhom = "Admin";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;

		} else if (utilityService.isNicOutsourced(nknSingleBase.getEmail())
				&& (utilityService.isNicOutsourced(email))) {
			
			System.out.println("::::::::::5:::::::::::::::::");
			toWhom = "Support";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
		} else {
			
			System.out.println("::::::::::6:::::::::::::::::");
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(nknSingleBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			
			System.out.println("::::::::::6:::::::::da::::::::"+da);
			
			Set<String> co = utilityService.fetchCoordinators(org);
			
			System.out.println("::::::::::6:::::::::co::::::::"+co);
			
			daEmail = String.join(",", da);
			
			coEmail = String.join(",", co);
			System.out.println("::::::::::6:::::::::daEmail::::::::"+daEmail);
			
			if (da != null && da.size() > 0) {
				if (daEmail.equals("support@nic.in") || daEmail.equals("support@gov.in")
						|| daEmail.equals("support@dummy.nic.in") || daEmail.equals("support@nkn.in")) {
					
					System.out.println("::::::::::7:::::::::::::::::");
					toWhom = "Support";
					daEmail = Constants.SUPPORT_EMAIL;
					recipientType = Constants.STATUS_SUPPORT_TYPE;
					nextStatus = Constants.STATUS_SUPPORT_PENDING;
				} else {
					if (!daEmail.isEmpty()) {
						
						System.out.println("::::::::::8:::::::::::::::::");
						String pref1_domain = preferred_email1.split("@")[1].trim().toLowerCase();
						String pref2_domain = preferred_email2.split("@")[1].trim().toLowerCase();
						System.out.println("::::::::::8::::::::::::::::pref1_domain:"+pref1_domain);
						System.out.println("::::::::::8::::::::::::::::pref1_domain:"+pref2_domain);
						System.out.println("::::::::::8::::::::::::::::domains:"+domains);
						if (domains.size() > 0) {
							if (domains.contains(pref1_domain) && domains.contains(pref2_domain)) {
								System.out.println("::::::::::enter1:::::::::::::::");
								nextStatus = Constants.STATUS_DA_PENDING;
								toWhom = "Delegated Admin";
								daEmail = String.join(",", da);
							} else {
								System.out.println("::::::::::enter2:::::::::::::::");
								nextStatus = Constants.STATUS_COORDINATOR_PENDING;
								toWhom = "Coordinator";
								daEmail = String.join(",", da);
								System.out.println("::::::::::enter3:::::::::::::::"+daEmail);
							}
						} else {
							System.out.println("::::::::::enter4:::::::::::::::");
							nextStatus = Constants.STATUS_DA_PENDING;
							toWhom = "Delegated Admin";
							daEmail = String.join(",", da);
						}
					} else {
						
						System.out.println("::::::::::9:::::::::::::::::");
						nextStatus = Constants.STATUS_SUPPORT_PENDING;
						toWhom = "Support";
						daEmail = "support@nic.in";
					}
				}
			} else if (co != null && co.size() > 0) {
				System.out.println("::::::::::10:::::::::::::::::");
				 coEmail = String.join(",", co);
				
				System.out.println("::::::::::10::::::::::coEmail:::::::"+coEmail);
				
				if (daEmail == null || daEmail.isEmpty()) {
					daEmail = "support@gov.in";
					nextStatus = Constants.STATUS_SUPPORT_PENDING;
					toWhom = "Support";
				} else if (daEmail.contains(email)) {
					nextStatus = "mail-admin_pending";
					toWhom = "Admin";
					daEmail = "support@gov.in";
				} else {
					nextStatus = "coordinator_pending";
					toWhom = "Coordinator";
					daEmail = String.join(",", co);
				}
			}
		}
		status.setRegistrationNo(regNumber);
		status.setRecipientType(recipientType);
		status.setStatus(nextStatus);
		status.setRecipient(daEmail);
		finalAuditTrack.setStatus(nextStatus);
		finalAuditTrack.setToEmail(daEmail);

		if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
			 log.info("Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom +"("+ daEmail + ")");
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

	@Transactional
	public NknSingleSha createSHA(String regNumber, String formType, String email, String sec_email) {
		NknSingleSha nknSingleSha = new NknSingleSha();
		String shValue = DigestUtils.shaHex(regNumber + System.currentTimeMillis() + email);
		nknSingleSha.setRegistrationNo(regNumber);
		nknSingleSha.setForm_type(formType);
		nknSingleSha.setRo_email(email);
		nknSingleSha.setUs_email(sec_email);
		nknSingleSha.setUs_email(shValue);
		return nknSingleShaRepo.save(nknSingleSha);
	}
	
	public ResponseBean rejectRO(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "nkn";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		NknSingleBase nknSingleBase = fetchDetails(regNumber);

		status =  utilityService.initializeStatusTable(ip, email, formType, remarks, nknSingleBase.getHodMobile(),
				nknSingleBase.getHodName(), "ro");

		finalAuditTrack =  utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, nknSingleBase.getHodMobile(),
				nknSingleBase.getHodName(), "ro", regNumber);

		status.setRegistrationNo(regNumber);
		status.setRecipientType("");
		status.setStatus(Constants.STATUS_CA_REJECTED);
		status.setRecipient("");

		finalAuditTrack.setStatus(Constants.STATUS_CA_REJECTED);
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

	
}
