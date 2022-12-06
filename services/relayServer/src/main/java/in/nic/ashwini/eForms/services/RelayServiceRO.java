package in.nic.ashwini.eForms.services;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.entities.RelayBase;
import in.nic.ashwini.eForms.models.FinalAuditTrack;
import in.nic.ashwini.eForms.models.OrganizationBean;
import in.nic.ashwini.eForms.models.ResponseBean;
import in.nic.ashwini.eForms.models.Status;
import in.nic.ashwini.eForms.repositories.RelayBaseRepo;
import in.nic.ashwini.eForms.utils.Constants;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class RelayServiceRO {
	
	private final Util utilityService;
	private final RelayBaseRepo relayBaseRepo;

	@Autowired
	public RelayServiceRO(Util utilityService,RelayBaseRepo relayBaseRepo) {
		super();

		this.utilityService = utilityService;
		this.relayBaseRepo = relayBaseRepo;
	}
	
	public ResponseBean approve(String regNumber, String ip, String email, String remarks ,ResponseBean responseBean) {
		responseBean.setRequestType("Approval of request by Reporting Officer");
		String formType = "relay";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		RelayBase relayBase = preview(regNumber);
		String dn = utilityService.findDn(relayBase.getEmail());
		String roDn = utilityService.findDn(email);
		List<String> aliases = utilityService.aliases(email);
		
		String daEmail = "";
		String toWhom = "";
		String recipientType = "";
		String nextStatus = "";

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, relayBase.getHodMobile(),
				relayBase.getHodName(),"ro");
		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks,
				relayBase.getHodMobile(), relayBase.getHodName(),"ro", regNumber);
		
		 if(relayBase.getEmployment().equalsIgnoreCase("Others") 
				&& relayBase.getOrganization().equalsIgnoreCase("DataCentre and Webservices")
				&& relayBase.getPostingState().equalsIgnoreCase("maharashtra")
				&& relayBase.getCity().equalsIgnoreCase("pune")
				&& (relayBase.getAddress().toLowerCase().contains("ndc")
						|| relayBase.getAddress().toLowerCase().contains("national data center"))) {
			toWhom = "Coordinator";
			daEmail = utilityService.fetchNdcPuneCoord();
			
			if (aliases.contains(daEmail)) {
				recipientType = Constants.STATUS_ADMIN_TYPE;
				nextStatus = Constants.STATUS_MAILADMIN_PENDING;
			} else {
				recipientType = Constants.STATUS_COORDINATOR_TYPE;
				nextStatus = Constants.STATUS_COORDINATOR_PENDING;
			}
			
		}else if(dn.contains("nic employees") || dn.contains("nic-official-id") || dn.contains("dio")) {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		}else if((roDn.contains("nic employees") || roDn.contains("nic-official-id") || roDn.contains("dio"))  && (relayBase.getPostingState().equalsIgnoreCase("delhi")
				&& relayBase.getEmployment().equalsIgnoreCase("central")
				&& utilityService.isNicOutsourced(relayBase.getEmail())))   {
			toWhom = "Admin";
			daEmail = Constants.MAILADMIN_EMAIL;
			recipientType = Constants.STATUS_ADMIN_TYPE;
			nextStatus = Constants.STATUS_MAILADMIN_PENDING;
		}else if(utilityService.isNicOutsourced(relayBase.getEmail())
				&& (utilityService.isNicOutsourced(email))) {
			
			toWhom = "Support";
			daEmail = Constants.SUPPORT_EMAIL;
			recipientType = Constants.STATUS_SUPPORT_TYPE;
			nextStatus = Constants.STATUS_SUPPORT_PENDING;
			
		}
		else {
			
			ModelMapper modelMapper = new ModelMapper();
			OrganizationBean org = modelMapper.map(relayBase, OrganizationBean.class);
			
			Set<String> da = utilityService.fetchDAs(org);
			Set<String> co = utilityService.fetchCoordinators(org);
			if(da != null && da.size() > 0) {
				daEmail = String.join(",", da);
				
				//isApplicantCoordinator					//isRoCoordinator
				 if (daEmail.contains(relayBase.getEmail()) || daEmail.contains(email)) {
					 	toWhom = "Admin";
						recipientType = Constants.STATUS_ADMIN_TYPE;
						nextStatus = Constants.STATUS_MAILADMIN_PENDING;
				 }else {
					 	toWhom = "Support";
					 	daEmail = Constants.SUPPORT_EMAIL;
					 	recipientType = Constants.STATUS_SUPPORT_TYPE;
						nextStatus = Constants.STATUS_SUPPORT_PENDING;
				 }
			}else {
				toWhom = "Support";
				daEmail = Constants.SUPPORT_EMAIL;
				recipientType = Constants.STATUS_SUPPORT_TYPE;
				nextStatus = Constants.STATUS_SUPPORT_PENDING;
			}
			if (utilityService.isSupportEmail(daEmail)) {
				toWhom = "Support";
				daEmail = Constants.SUPPORT_EMAIL;
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
	
	
	public ResponseBean reject( String regNumber,String ip, String email, String remarks , ResponseBean responseBean) {
		responseBean.setRequestType("Rejection of request by RO.");
		String formType = "relay";
		Status status = null;
		FinalAuditTrack finalAuditTrack = null;
		RelayBase relayBase = preview(regNumber);

		status = utilityService.initializeStatusTable(ip, email, formType, remarks, relayBase.getHodMobile(), relayBase.getHodName(),"ro");

		finalAuditTrack = utilityService.initializeFinalAuditTrackTable(ip, email, formType, remarks, relayBase.getHodMobile(), relayBase.getHodName(),"ro", regNumber);

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
	
	
	
	public RelayBase preview(String regNo) {
		return relayBaseRepo.findByRegistrationNo(regNo);
	}

	

	public boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		return utilityService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}
	
	

}
