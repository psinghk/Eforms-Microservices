package in.nic.eForms.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import in.nic.eForms.entities.EmailDeActBase;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.ManualUploadBean;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.repositories.EmailDeActBaseRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class EmailDeActROService {
	private final EmailDeActBaseRepo emailDeActBaseRepo;
	private final Util utilityService;

	@Value("${fileBasePath}")
	 private String fileBasePath;
	
	@Autowired
	public EmailDeActROService(EmailDeActBaseRepo emailDeActBaseRepo,
			  Util utilityService) {
		super();
		this.emailDeActBaseRepo = emailDeActBaseRepo;
		this.utilityService = utilityService;
	}

	public EmailDeActBase fetchDetails(String regNo) {
		return emailDeActBaseRepo.findByRegistrationNo(regNo);
	}

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

	public Set<String> findBo(EmailDeActBase emailDeActBase) {
		List<String> bo = null;
		if (emailDeActBase.getEmployment().trim().equalsIgnoreCase("central")
				|| emailDeActBase.getEmployment().trim().equalsIgnoreCase("ut")) {

			bo = utilityService.fetchBoByMinistry(emailDeActBase.getEmployment(), emailDeActBase.getMinistry(),
					emailDeActBase.getDepartment());
		} else if (emailDeActBase.getEmployment().trim().equalsIgnoreCase("state")) {
			bo = utilityService.fetchBoByState(emailDeActBase.getEmployment(), emailDeActBase.getState(),
					emailDeActBase.getDepartment());
		} else {
			bo = utilityService.fetchBoByOrg(emailDeActBase.getEmployment(), emailDeActBase.getOrganization());
		}
		Set<String> bos = new HashSet<String>(bo);
		return bos;
	}


	public ResponseBean approveRO(String regNumber, String ip, String email, String remarks, String submissionType,
			ResponseBean responseBean) {
		
		responseBean.setRequestType("Approval of request by Reporting Officer");
		log.info("Email DeActivation Form Approve by {}",email);	
		String formType = "emaildeactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		EmailDeActBase emailDeActBase = fetchDetails(regNumber);
		String dn = utilityService.findDn(emailDeActBase.getEmail());
		Boolean isEmailAvailable = utilityService.isGovEmployee(email);
		System.out.println("isEmailAvailable::::"+isEmailAvailable);
		List<String> aliases = utilityService.aliases(email);
		System.out.println("::aliases::"+aliases);
		String daEmail = "";
		String coEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, emailDeActBase.getHodMobile(),
				emailDeActBase.getHodName(), "ro");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, emailDeActBase.getHodMobile(),
				emailDeActBase.getHodName(),"ro", regNumber);

		String preferred_email = emailDeActBase.getPreferredEmail();
		
			Set<String> bo = findBo(emailDeActBase);
		System.out.println("bo::"+bo);
		
		Set<String> domains = new HashSet<>();
		for (String boId : bo) {
			System.out.println("Bo id ::: " + boId);
			 List<String> domain = utilityService.findDomains(boId);
			 System.out.println("::::::::::domain:::::::::::::::::"+domain);
	            if (domain.size() > 0)
	            {
	                domains.addAll(domain);
	            }
		}

		if (submissionType.equalsIgnoreCase("manual") && !isEmailAvailable) {
			toWhom = "US";
			daEmail = emailDeActBase.getUnderSecEmail();
			recipientType = Constants.STATUS_US_TYPE;
			nextStatus = Constants.STATUS_US_PENDING;

		} else if (emailDeActBase.getEmployment().equalsIgnoreCase("Others")
				&& emailDeActBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& emailDeActBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& emailDeActBase.getCity().equalsIgnoreCase("pune")
				&& (emailDeActBase.getAddress().toLowerCase().contains("ndc")
						|| emailDeActBase.getAddress().toLowerCase().contains("national data center"))) {
			
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();

			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}

		} else if (preferred_email.contains("gem.gov.in")) {
			
			String pref1_domain = preferred_email.split("@")[1].trim().toLowerCase();
			if (domains.size() > 0) {
				if (domains.contains(pref1_domain)) {
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
			
		}  else if (utilityService.isNicEmployee(emailDeActBase.getEmail())) {
			
			toWhom = "Admin";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		} 
		
//		else if ((utilityService.isNicEmployee(email))
//				&& (emailDeActBase.getPostingState().equalsIgnoreCase("delhi")
//						&& emailDeActBase.getEmployment().equalsIgnoreCase("central"))
//				&& (utilityService.isNicOutsourced(emailDeActBase.getEmail()))) {
//			System.out.println("::::::::::5:::::::::::::::::");
//			toWhom = "Admin";
//			daEmail = Constants.SUPPORT_EMAIL;
//			recipientType = Constants.STATUS_ADMIN_TYPE;                // relevent code
//			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
//
//		} 
		OrganizationBean orgbean=new OrganizationBean();
		if (utilityService.isNicEmployee(email) && (emailDeActBase.getPostingState().equalsIgnoreCase("delhi")
				&& emailDeActBase.getEmployment().equalsIgnoreCase("central")
				)) {   // 
			
			toWhom = "Admin";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			
		}
	else if ((emailDeActBase.getState().equalsIgnoreCase("Himachal")
				&& emailDeActBase.getEmployment().equalsIgnoreCase("state")
				|| emailDeActBase.getPostingState().equalsIgnoreCase("Himachal Pradesh"))
				)
	    	
		  {
			toWhom = "Support";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
		   } 
		
		else if (emailDeActBase.getEmployment().equalsIgnoreCase("state")
				&& (emailDeActBase.getState().equalsIgnoreCase("punjab"))) {
			
			toWhom = "Support";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
			
		} else if (emailDeActBase.getEmployment().equalsIgnoreCase("state")
				&& emailDeActBase.getState().equalsIgnoreCase("maharastra")
				&& emailDeActBase.getPostingState().equalsIgnoreCase("maharastra")) {
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(emailDeActBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			Set<String> co = utilityService.fetchCoordinators(org);	
			daEmail = String.join(",", da);
			coEmail = String.join(",", co);
		}

		else if (emailDeActBase.getEmployment().equalsIgnoreCase("central")
				&& !emailDeActBase.getPostingState().equalsIgnoreCase("Delhi")) {
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(emailDeActBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			
			Set<String> co = utilityService.fetchCoordinators(org);
			
			daEmail = String.join(",", da);
			coEmail = String.join(",", co);
			if (co != null && co.size() > 0) {
				toWhom = "Coordinator";
				daEmail = String.join(",",co);
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			} else {
				if (org.getDepartment().equalsIgnoreCase("other")) {
					toWhom = "Coordinator";
					daEmail = String.join(",",co);
					recipientType = Constants.STATUS_COORDINATOR_TYPE;     //dout 
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
			OrganizationBean org = modelMapper.map(emailDeActBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			Set<String> co = utilityService.fetchCoordinators(org);

			daEmail = String.join(",", da);
			coEmail = String.join(",", co);

			if (da != null && da.size() > 0) {
				if (daEmail.equals("support@nic.in") || daEmail.equals("support@gov.in")
						|| daEmail.equals("support@dummy.nic.in") ) {
					
					toWhom = "Support";
					daEmail = Constants.SUPPORT_EMAIL;
					recipientType = Constants.STATUS_SUPPORT_TYPE;
					nextStatus = Constants.STATUS_SUPPORT_PENDING;
				} else {
					if (!daEmail.isEmpty()) {
						String pref1_domain = preferred_email.split("@")[1].trim().toLowerCase();
						
						if (domains.size() > 0) {
							if (domains.contains(pref1_domain)) {
								nextStatus = Constants.STATUS_DA_PENDING;
								toWhom = "Delegated Admin";
								daEmail = String.join(",", da);
							} else {
								nextStatus = Constants.STATUS_COORDINATOR_PENDING;
								toWhom = "Coordinator";
								daEmail = String.join(",", da);
							}
						} else {
							nextStatus = Constants.STATUS_DA_PENDING;
							toWhom = "Delegated Admin";
							daEmail = String.join(",", da);
						}
					} else {
					
						nextStatus = Constants.STATUS_SUPPORT_PENDING;
						toWhom = "Support";
						daEmail = "support@nic.in";
					}
				}
			} else if (co != null && co.size() > 0) {
				 coEmail = String.join(",", co);
				
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
		finalAuditTrack.setAppCaType(submissionType);

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
	
	public ResponseBean rejectRO(String regNumber, String ip, String email, String remarks, ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by RO.");
		log.info("Email DeActivation Form reject by {}",email);
		String formType = "emaildeactivate";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		EmailDeActBase emailDeActBase = fetchDetails(regNumber);

		status =  utilityService.initializeStatusTable(ip, email, formType, remarks, emailDeActBase.getHodMobile(),
				emailDeActBase.getHodName(), "ro");

		finalAuditTrack =  utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, emailDeActBase.getHodMobile(),
				emailDeActBase.getHodName(), "ro", regNumber);

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

	 public ResponseBean manualupload(@ModelAttribute("manualUploadBean") ManualUploadBean manualUploadBean,HttpServletRequest request, ResponseBean responseBean) throws IOException {
			
			manualUploadBean.setEmail(request.getParameter("email"));
			manualUploadBean.setClientIp(request.getParameter("clientIp"));
			
			responseBean.setRequestType("approving the request by ro");
			log.info("EmailAct manual upload request");
			String formType = "emailactivate";
			Status status = null;
			FinalAuditTrack finalAuditTrack = null;
			EmailDeActBase emailActBase = preview(manualUploadBean.getRegNumber());
			String[] contenttype = manualUploadBean.getInfile().getContentType().split("/");
			String ext = contenttype[1];
			String outputfile = new StringBuilder().append(manualUploadBean.getRegNumber()).append("_")
					.append(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
					.append(".").append(ext).toString();
			
			emailActBase.setPdfPath(fileBasePath+outputfile);
			byte[] bytes = manualUploadBean.getInfile().getBytes();
			Path path = Paths.get(fileBasePath + outputfile);
			boolean stat = emailDeActbase(emailActBase);
			if(stat) {
				
				Files.write(path, bytes);
			}
			else {
				
				responseBean.setStatus("File failed to upload");
				responseBean.setRegNumber(manualUploadBean.getRegNumber());
	   		return responseBean;
			}
			String dn = utilityService.findDn(emailActBase.getEmail());
			String roDn = utilityService.findDn(manualUploadBean.getEmail());
			List<String> aliases = utilityService.aliases(manualUploadBean.getEmail());
			String daEmail = "";
			String toWhom = "";
			String recipientType = "";
			String nextStatus = "";
			
			finalAuditTrack = utilityService.initializeFinalAuditTrackTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), emailActBase.getHodMobile(),
					emailActBase.getHodName(),"usermanual" ,manualUploadBean.getRegNumber());
			status = utilityService.initializeStatusTable(manualUploadBean.getClientIp(), manualUploadBean.getEmail(), formType, manualUploadBean.getRemarks(), emailActBase.getHodMobile(), emailActBase.getHodName(),"user");
			
	         if (emailActBase.getEmployment().equalsIgnoreCase("State") && emailActBase.getState().equalsIgnoreCase("punjab")
					&& emailActBase.getPostingState().equalsIgnoreCase("punjab")) {
	        	 toWhom = "Support";
	 			daEmail = Constants.SUPPORT_EMAIL;
	 			recipientType = Constants.STATUS_SUPPORT_TYPE;
	 			nextStatus = Constants.STATUS_SUPPORT_PENDING;
			} 
	         if (emailActBase.getEmployment().equalsIgnoreCase("State") && emailActBase.getState().equalsIgnoreCase("Himanchal Pradesh")
	 				|| emailActBase.getPostingState().equalsIgnoreCase("Himachal")) {
	         	 toWhom = "Support";
	  			daEmail = Constants.SUPPORT_EMAIL;
	  			recipientType = Constants.STATUS_SUPPORT_TYPE;
	  			nextStatus = Constants.STATUS_SUPPORT_PENDING;
	 		} else if (utilityService.isNicEmployee(manualUploadBean.getEmail()) && (emailActBase.getPostingState().equalsIgnoreCase("delhi")
					&& emailActBase.getEmployment().equalsIgnoreCase("central")
					&& (dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))) {
				toWhom = "Admin";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else if (utilityService.isNicEmployee(emailActBase.getEmail()) || utilityService.isNicEmployee(manualUploadBean.getEmail())) {
				toWhom = "Admin";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				
			} else if (dn!=null && ((dn.contains("nic support outsourced") || dn.contains("nationalknowledgenetwork")))
					&& (roDn.contains("nic support outsourced") || roDn.contains("nationalknowledgenetwork"))) {
				toWhom = "Admin";
				daEmail = Constants.MAILADMIN_EMAIL;
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				
			} else if (emailActBase.getEmployment().equalsIgnoreCase("Others")
					&& emailActBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
					&& emailActBase.getPostingState().equalsIgnoreCase("maharashtra")
					&& emailActBase.getCity().equalsIgnoreCase("pune") && (emailActBase.getAddress().toLowerCase().contains("ndc")
							|| emailActBase.getAddress().toLowerCase().contains("national data center"))) {
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
				OrganizationBean org = modelMapper.map(emailActBase, OrganizationBean.class);
				 toWhom = "Support";
		 			daEmail = Constants.SUPPORT_EMAIL;
		 			recipientType = Constants.STATUS_SUPPORT_TYPE;
		 			nextStatus = Constants.STATUS_SUPPORT_PENDING;
			}
			status.setRegistrationNo(manualUploadBean.getRegNumber());
			status.setRecipientType(recipientType);
			status.setStatus(nextStatus);
			status.setRecipient(daEmail);

			finalAuditTrack.setStatus(nextStatus);
			finalAuditTrack.setToEmail(daEmail);

			if (updateStatusAndFinalAuditTrack(status, finalAuditTrack)) {
				log.info("Application (" + manualUploadBean.getRegNumber() + ")  Forwarded Successfully to " + toWhom + "(" + daEmail
						+ ")");
				responseBean.setStatus("Application (" + manualUploadBean.getRegNumber() + ") Forwarded Successfully to " + toWhom
						+ "(" + daEmail + ")");
				responseBean.setRegNumber(manualUploadBean.getRegNumber());
			} else {
				log.debug(Constants.ERROR_MESSAGE);
				responseBean.setStatus(Constants.ERROR_MESSAGE);
				responseBean.setRegNumber(manualUploadBean.getRegNumber());
			}
			return responseBean;
		}
	   public boolean emailDeActbase(EmailDeActBase emailActBase) {
			EmailDeActBase details = emailDeActBaseRepo.save(emailActBase);
			if (details.getId() > 0) {
				return true;
			} else {
				return false;
			}
		}
		public EmailDeActBase preview(String regNo) {
			return emailDeActBaseRepo.findByRegistrationNo(regNo);
		}
	
}
