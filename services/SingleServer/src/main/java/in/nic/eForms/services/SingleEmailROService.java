package in.nic.eForms.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.nic.eForms.entities.SingleEmailBase;
import in.nic.eForms.entities.NknSingleSha;
import in.nic.eForms.models.FinalAuditTrack;
import in.nic.eForms.models.OrganizationBean;
import in.nic.eForms.models.ResponseBean;
import in.nic.eForms.models.Status;
import in.nic.eForms.repositories.NknSingleShaRepo;
import in.nic.eForms.repositories.SingleBaseRepo;
import in.nic.eForms.utils.Constants;
import in.nic.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class SingleEmailROService {
	private final SingleBaseRepo singleBaseRepo;
	private final NknSingleShaRepo nknSingleShaRepo;
	private final Util utilityService;

	@Autowired
	public SingleEmailROService(SingleBaseRepo singleBaseRepo,
			NknSingleShaRepo nknSingleShaRepo,  Util utilityService) {
		super();
		this.singleBaseRepo = singleBaseRepo;
		this.utilityService = utilityService;
		this.nknSingleShaRepo = nknSingleShaRepo;

	}


	public SingleEmailBase fetchDetails(String regNo) {
		return singleBaseRepo.findByRegistrationNo(regNo);
	}


	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}


//	public Set<String> findBo(SingleEmailBase singleEmailBase) {
//		Set<String> bos = new HashSet<>();
//		if (singleEmailBase.getEmployment().trim().equalsIgnoreCase("central")
//				|| singleEmailBase.getEmployment().trim().equalsIgnoreCase("ut")) {
//
//			bos = nknSingleEmpCoordRepo.fetchByMinistry(singleEmailBase.getEmployment(), singleEmailBase.getMinistry(),
//					singleEmailBase.getDepartment());
//		} else if (singleEmailBase.getEmployment().trim().equalsIgnoreCase("state")) {
//			bos = nknSingleEmpCoordRepo.fetchByState(singleEmailBase.getEmployment(), singleEmailBase.getState(),
//					singleEmailBase.getDepartment());
//		} else {
//			bos = nknSingleEmpCoordRepo.fetchByOrg(singleEmailBase.getEmployment(), singleEmailBase.getOrganization());
//		}
//		return bos;
//	}
	
	public Set<String> findBo(SingleEmailBase singleEmailBase) {
		List<String> bo = null;
		if (singleEmailBase.getEmployment().trim().equalsIgnoreCase("central")
				|| singleEmailBase.getEmployment().trim().equalsIgnoreCase("ut")) {

			bo = utilityService.fetchBoByMinistry(singleEmailBase.getEmployment(), singleEmailBase.getMinistry(),
					singleEmailBase.getDepartment());
		} else if (singleEmailBase.getEmployment().trim().equalsIgnoreCase("state")) {
			bo = utilityService.fetchBoByState(singleEmailBase.getEmployment(), singleEmailBase.getState(),
					singleEmailBase.getDepartment());
		} else {
			bo = utilityService.fetchBoByOrg(singleEmailBase.getEmployment(), singleEmailBase.getOrganization());
		}
		Set<String> bos = new HashSet<String>(bo);
		return bos;
	}


	public ResponseBean approveRO(String regNumber, String ip, String email, String remarks, String submissionType,
			ResponseBean responseBean) {
		System.out.println("::::::::::approveRO service:::::::::::::::::");
		responseBean.setRequestType("Approval of request by Reporting Officer");
		String formType = "single";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		SingleEmailBase singleEmailBase = fetchDetails(regNumber);
		Boolean isEmailAvailable = utilityService.isGovEmployee(email);
		System.out.println("::::::::::isEmailAvailable:::::::::::::::::"+isEmailAvailable);
		List<String> aliases = utilityService.aliases(email);
		System.out.println("::::::::::aliases:::::::::::::::::"+aliases);
		String daEmail = "";
		String coEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, singleEmailBase.getHodMobile(),
				singleEmailBase.getHodName(), "ro");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, singleEmailBase.getHodMobile(),
				singleEmailBase.getHodName(),"ro", regNumber);

		String preferred_email1 = singleEmailBase.getPreferredEmail1();
		String preferred_email2 = singleEmailBase.getPreferredEmail2();
		Set<String> bo = findBo(singleEmailBase);
		System.out.println("::::::::::bo:::::::::::::::::"+bo);
		
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
			daEmail = singleEmailBase.getUnderSecEmail();
			recipientType = Constants.STATUS_US_TYPE;
			nextStatus = Constants.STATUS_US_PENDING;
			NknSingleSha sha = createSHA(regNumber, formType, email, singleEmailBase.getUnderSecEmail());

		} else if (singleEmailBase.getEmployment().equalsIgnoreCase("Others")
				&& singleEmailBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& singleEmailBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& singleEmailBase.getCity().equalsIgnoreCase("pune")
				&& (singleEmailBase.getAddress().toLowerCase().contains("ndc")
						|| singleEmailBase.getAddress().toLowerCase().contains("national data center"))) {
			
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
			
		} else if (utilityService.isNicEmployee(singleEmailBase.getEmail())) {
			
			System.out.println("::::::::::4:::::::::::::::::");
			toWhom = "Admin";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;

		} else if ((utilityService.isNicEmployee(email))
				&& (singleEmailBase.getPostingState().equalsIgnoreCase("delhi")
						&& singleEmailBase.getEmployment().equalsIgnoreCase("central"))
				&& (utilityService.isNicOutsourced(singleEmailBase.getEmail()))) {
			System.out.println("::::::::::5:::::::::::::::::");
			toWhom = "Admin";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;

		} else if (utilityService.isNicOutsourced(singleEmailBase.getEmail())
				&& (utilityService.isNicOutsourced(email))) {
			
			System.out.println("::::::::::5.1:::::::::::::::::");
			toWhom = "Support";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
		}else if ((singleEmailBase.getState().equalsIgnoreCase("Himachal")
				&& singleEmailBase.getEmployment().equalsIgnoreCase("state")
				|| singleEmailBase.getPostingState().equalsIgnoreCase("Himachal Pradesh"))
				/*&& (utilityService.fetchDAs(org).equals("kaushal.shailender@nic.in")
						&& !utilityService.fetchCoordinators(org).equals("kaushal.shailender@nic.in"))*/) {
			
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(singleEmailBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			
			System.out.println("::::::::::6:::::::::da::::::::"+da);
			
			Set<String> co = utilityService.fetchCoordinators(org);
			
			System.out.println("::::::::::6:::::::::co::::::::"+co);
			
			daEmail = String.join(",", da);
			
			coEmail = String.join(",", co);
			System.out.println("::::::::::6:::::::::daEmail::::::::"+daEmail);
			
			if (singleEmailBase.getEmpType().equals("emp_regular")) {
				String pref1_domain = preferred_email1.split("@")[1].trim().toLowerCase();
				String pref2_domain = preferred_email2.split("@")[1].trim().toLowerCase();
				System.out.println("::::::::::8::::::::::::::::pref1_domain:"+pref1_domain);
				System.out.println("::::::::::8::::::::::::::::pref1_domain:"+pref2_domain);
				System.out.println("::::::::::8::::::::::::::::domains:"+domains);
				
				if (!domains.contains(pref1_domain) || !domains.contains(pref2_domain)) {
					toWhom = "Delegated Admin";
					recipientType = Constants.STATUS_DA_TYPE;
					nextStatus = Constants.STATUS_DA_PENDING;
					daEmail = String.join(",", da);
				} else {
					toWhom = "Coordinator";
					daEmail = String.join(",");
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				}
			} else {
				toWhom = "Coordinator";
				daEmail = String.join(",", co);
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
			toWhom = "Coordinator";
			daEmail = String.join(",", co);
			recipientType = Constants.STATUS_COORDINATOR_TYPE;
			nextStatus = Constants.STATUS_COORDINATOR_PENDING;
		} else if (singleEmailBase.getEmployment().equalsIgnoreCase("state")
				&& (singleEmailBase.getState().equalsIgnoreCase("punjab"))) {
			
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(singleEmailBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			
			System.out.println("::::::::::6:::::::::da::::::::"+da);
			
			Set<String> co = utilityService.fetchCoordinators(org);
			
			System.out.println("::::::::::6:::::::::co::::::::"+co);
			
			daEmail = String.join(",", da);
			
			coEmail = String.join(",", co);
			System.out.println("::::::::::6:::::::::daEmail::::::::"+daEmail);
			if (singleEmailBase.getPostingState().equalsIgnoreCase("punjab")) {
				toWhom = "Coordinator";
				List<String> punjabCoords = utilityService.fetchPunjabCoords(singleEmailBase.getCity());
				if (punjabCoords.size() > 0) {
					daEmail = String.join(",", punjabCoords);
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				} else {
					// Fetch DA from EmpCoord Table. IF DA Exists
					//Set<String> da = utilityService.fetchDAs(org);
					if (da != null && da.size() > 0) {
						if (singleEmailBase.getEmpType().equals("emp_regular")) {
							System.out.println("::::::::::8:::::::::::::::::");
							String pref1_domain = preferred_email1.split("@")[1].trim().toLowerCase();
							String pref2_domain = preferred_email2.split("@")[1].trim().toLowerCase();
							System.out.println("::::::::::8::::::::::::::::pref1_domain:"+pref1_domain);
							System.out.println("::::::::::8::::::::::::::::pref1_domain:"+pref2_domain);
							System.out.println("::::::::::8::::::::::::::::domains:"+domains);
							
							if (!domains.contains(pref1_domain) || !domains.contains(pref2_domain)) {
								toWhom = "Delegated Admin";
								recipientType = Constants.STATUS_DA_TYPE;
								nextStatus = Constants.STATUS_DA_PENDING;
								daEmail = String.join(",", da);

							} else {
								toWhom = "Coordinator";
								daEmail = String.join(",",punjabCoords);
								recipientType = Constants.STATUS_COORDINATOR_TYPE;
								nextStatus = Constants.STATUS_COORDINATOR_PENDING;
							}
						} else {
							toWhom = "Coordinator";
							daEmail = String.join(",",punjabCoords);
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
		} else if (singleEmailBase.getEmployment().equalsIgnoreCase("state")
				&& singleEmailBase.getState().equalsIgnoreCase("maharastra")
				&& singleEmailBase.getPostingState().equalsIgnoreCase("maharastra")) {
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(singleEmailBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			
			System.out.println("::::::::::6:::::::::da::::::::"+da);
			
			Set<String> co = utilityService.fetchCoordinators(org);
			
			System.out.println("::::::::::6:::::::::co::::::::"+co);
			
			daEmail = String.join(",", da);
			
			coEmail = String.join(",", co);
			System.out.println("::::::::::6:::::::::daEmail::::::::"+daEmail);
			
			if (da != null && da.size() > 0) {
				if (singleEmailBase.getEmpType().equals("emp_regular")) {
					System.out.println("::::::::::8:::::::::::::::::");
					String pref1_domain = preferred_email1.split("@")[1].trim().toLowerCase();
					String pref2_domain = preferred_email2.split("@")[1].trim().toLowerCase();
					System.out.println("::::::::::8::::::::::::::::pref1_domain:"+pref1_domain);
					System.out.println("::::::::::8::::::::::::::::pref1_domain:"+pref2_domain);
					System.out.println("::::::::::8::::::::::::::::domains:"+domains);
					
					if (!domains.contains(pref1_domain) || !domains.contains(pref2_domain)) {
						toWhom = "Delegated Admin";
						recipientType = Constants.STATUS_DA_TYPE;
						nextStatus = Constants.STATUS_DA_PENDING;
						daEmail = String.join(",", da);
					} else {
						toWhom = "Coordinator";
						daEmail = String.join(",",co);
						recipientType = Constants.STATUS_COORDINATOR_TYPE;
						nextStatus = Constants.STATUS_COORDINATOR_PENDING;
					}
				} else {
					toWhom = "Coordinator";
					daEmail = String.join(",",co);
					recipientType = Constants.STATUS_COORDINATOR_TYPE;
					nextStatus = Constants.STATUS_COORDINATOR_PENDING;
				}
			} else {
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
			}

		}

		else if (singleEmailBase.getEmployment().equalsIgnoreCase("central")
				&& !singleEmailBase.getPostingState().equalsIgnoreCase("Delhi")) {
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(singleEmailBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			
			System.out.println("::::::::::6:::::::::da::::::::"+da);
			
			Set<String> co = utilityService.fetchCoordinators(org);
			
			System.out.println("::::::::::6:::::::::co::::::::"+co);
			
			daEmail = String.join(",", da);
			
			coEmail = String.join(",", co);
			System.out.println("::::::::::6:::::::::daEmail::::::::"+daEmail);
			
			if (co != null && co.size() > 0) {
				toWhom = "Coordinator";
				daEmail = String.join(",",co);
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			} else {
				if (org.getDepartment().equalsIgnoreCase("other")) {
					toWhom = "Coordinator";
					daEmail = String.join(",",co);
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
			
			System.out.println("::::::::::6:::::::::::::::::");
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(singleEmailBase, OrganizationBean.class);
			Set<String> da = utilityService.fetchDAs(org);
			
			System.out.println("::::::::::6:::::::::da::::::::"+da);
			
			Set<String> co = utilityService.fetchCoordinators(org);
			
			System.out.println("::::::::::6:::::::::co::::::::"+co);
			
			daEmail = String.join(",", da);
			
			coEmail = String.join(",", co);
			System.out.println("::::::::::6:::::::::daEmail::::::::"+daEmail);
			
			if (da != null && da.size() > 0) {
				if (daEmail.equals("support@nic.in") || daEmail.equals("support@gov.in")
						|| daEmail.equals("support@dummy.nic.in") ) {
					
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
		String formType = "single";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		SingleEmailBase singleEmailBase = fetchDetails(regNumber);

		status =  utilityService.initializeStatusTable(ip, email, formType, remarks, singleEmailBase.getHodMobile(),
				singleEmailBase.getHodName(), "ro");

		finalAuditTrack =  utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, singleEmailBase.getHodMobile(),
				singleEmailBase.getHodName(), "ro", regNumber);

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
