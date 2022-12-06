package in.nic.ashwini.eForms.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestParam;

import in.nic.ashwini.eForms.entities.GeneratePdf;
import in.nic.ashwini.eForms.entities.LdapBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.GeneratePdfBean;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.PreviewFormBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.GeneratePdfRepository;
import in.nic.ashwini.eForms.repositories.LdapBaseRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
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
public class LdapROService {
	private final LdapBaseRepo ldapBaseRepo;
	private final Util utilityService;

	@Autowired
	public LdapROService(LdapBaseRepo ldapBaseRepo,	Util utilityService) {
		super();
		this.ldapBaseRepo = ldapBaseRepo;
		this.utilityService = utilityService;

	}

	public LdapBase fetchDetails(String regNo) {
		return ldapBaseRepo.findByRegistrationNo(regNo);
	}
	
	
	public boolean updateStatusTable(String regNumber, String currentRole, PreviewFormBean previewFormBean) {
		LdapBase ldapBase = ldapBaseRepo.findByRegistrationNo(regNumber);
		if (ldapBase != null) {
			try {
				BeanUtils.copyProperties(previewFormBean, ldapBase);
				LocalDateTime currentTime = LocalDateTime.now();
				//ldapBase.setLastUpdationDateTime(currentTime);
				LdapBase ldapUpdated = ldapBaseRepo.save(ldapBase);
				if (ldapUpdated.getId() > 0) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status,finalAuditTrack);
	}
	
	
	public ResponseBean approve(String regNumber, String ip, String email,  String remarks,ResponseBean responseBean) {
		responseBean.setRequestType("Approval of request by Reporting Officer");
		String formType = "ldap";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		LdapBase ldapBase = fetchDetails(regNumber);
		List<String> aliases = utilityService.aliases(email);
		
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, ldapBase.getHodMobile(),
				ldapBase.getHodName(),"ro");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
				ldapBase.getHodMobile(), ldapBase.getHodName(),"ro", regNumber);

		 if(ldapBase.getEmployment().equalsIgnoreCase("Others") 
					&& ldapBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
					&& ldapBase.getPostingState().equalsIgnoreCase("maharashtra")
					&& ldapBase.getCity().equalsIgnoreCase("pune")
					&& (ldapBase.getAddress().toLowerCase().contains("ndc")
							|| ldapBase.getAddress().toLowerCase().contains("national data center"))) {
				toWhom = "Coordinator";
				daEmail = utilityService.fetchNdcPuneCoord();
				
				if (aliases.contains(daEmail)) {
					recipientType = Constants.STATUS_ADMIN_TYPE;
					nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				} else {
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				}
				//is User NIC Employee 
		}else if (utilityService.isNicEmployee(ldapBase.getEmail())) {
			toWhom = "Admin";
			daEmail = Constants.RAJESH_SINGH_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			//is Ro NIC Employee 
		}else if((utilityService.isNicEmployee(email) && (ldapBase.getPostingState().equalsIgnoreCase("delhi")
				&& ldapBase.getEmployment().equalsIgnoreCase("central")) && (utilityService.isNicOutsourced(ldapBase.getEmail())))) {
			toWhom = "Admin";
			daEmail = Constants.RAJESH_SINGH_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		}else if(utilityService.isNicOutsourced(email) && utilityService.isNicOutsourced(ldapBase.getEmail())) {
			toWhom = "Support";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
		}else {
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(ldapBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			Set<String> co = utilityService.fetchCoordinators(org);
			daEmail = String.join(",", da);
			if(da != null && da.size() > 0) {
				 if (daEmail.contains(ldapBase.getEmail()) || daEmail.contains(email)) {
					 	toWhom = "Admin";
					 	daEmail = Constants.RAJESH_SINGH_EMAIL;
						recipientType = Constants.STATUS_ADMIN_TYPE;
						nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				 }else {
					 	toWhom = "Coordinator";
					 	daEmail = String.join(",", co);
						recipientType = Constants.STATUS_COORDINATOR_TYPE;
						nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				 }
			}else {
				toWhom = "Support";
				daEmail = Constants.SUPPORT_EMAIL;
				recipientType = Constants.STATUS_SUPPORT_TYPE;
				nextStatus = Constants.STATUS_SUPPORT_PENDING;
			}
			if (utilityService.isSupportEmail(daEmail)) {
				toWhom = "Admin";
				daEmail = Constants.RAJESH_SINGH_EMAIL;
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
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
			responseBean.setStatus(
					"Application (" + regNumber + ") Approved and Forwarded Successfully to " + toWhom +"("+ daEmail + ")");
			responseBean.setRegNumber(regNumber);
		} else {
			log.debug("Something went wrong. Please try again after sometime.");
			responseBean.setStatus("Something went wrong. Please try again after sometime.");
			responseBean.setRegNumber(regNumber);
		}
		return responseBean;
	}
	
	
	public ResponseBean reject(String regNumber, String ip, String email,String remarks,ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "ldap";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		LdapBase ldapBase = fetchDetails(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, ldapBase.getHodMobile(), ldapBase.getHodName(),"ro");

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, ldapBase.getHodMobile(), ldapBase.getHodName(),"ro", regNumber);

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
