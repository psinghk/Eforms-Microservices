package in.nic.ashwini.eForms.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.db.master.entities.FinalAuditTrack;
import in.nic.ashwini.eForms.db.master.entities.Query;
import in.nic.ashwini.eForms.db.master.repositories.FinalAuditTrackRepository;
import in.nic.ashwini.eForms.db.master.repositories.QueryRepository;
import in.nic.ashwini.eForms.db.slave.entities.QueryFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.FinalAuditTrackRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.QueryRepositoryToRead;
import in.nic.ashwini.eForms.models.CustomMessage;

@Service
public class QueryService {

	private final StatusService statusService;
	private final QueryRepository queryRepository;
	private final FinalAuditTrackRepository finalAuditTrackRepository;
	private final QueryRepositoryToRead queryRepositoryToRead;
	private final FinalAuditTrackRepositoryToRead finalAuditTrackRepositoryToRead;
	private final UtilityService utilityService;

	@Autowired
	public QueryService(StatusService statusService, QueryRepository queryRepository, UtilityService utilityService,
			FinalAuditTrackRepository finalAuditTrackRepository,
			QueryRepositoryToRead queryRepositoryToRead, FinalAuditTrackRepositoryToRead finalAuditTrackRepositoryToRead) {
		super();
		this.statusService = statusService;
		this.queryRepository = queryRepository;
		this.utilityService = utilityService;
		this.finalAuditTrackRepository = finalAuditTrackRepository;
		this.queryRepositoryToRead = queryRepositoryToRead;
		this.finalAuditTrackRepositoryToRead = finalAuditTrackRepositoryToRead;
	}

	public Map<String, Object> fetchQueries(String regNumber) {
		Map<String, Object> map = new HashMap<>();
		Set<String> stakeHolders = statusService.findAllStakeHolders(regNumber);
		List<QueryFromSlave> queries = queryRepositoryToRead.findByRegistrationNoOrderByQueryRaisedTimeDesc(regNumber);
		map.put("stakeHolders", stakeHolders);
		map.put("queries", queries);
		return map;
	}

	public Map<String, Object> raiseQuery(String regNumber, String currentRole, String recipientRole, String remarks, String email) {
		Set<String> stakeHolders = statusService.findAllStakeHolders(regNumber);
		Map<String, Object> msgStatus = new HashMap<>();
		if (stakeHolders.contains(recipientRole) && !currentRole.equalsIgnoreCase(recipientRole)) {
			// populate Query model and save it
			Query query = new Query();
			query.setFormType(utilityService.fetchService(regNumber));
			query.setQuery(remarks);
			query.setRecipientType(recipientRole);
			query.setSenderType(currentRole);
			query.setSender(email);
			query.setRegistrationNo(regNumber);

			FinalAuditTrack finalAuditTrack = finalAuditTrackRepository.findByRegistrationNo(regNumber);
			if (finalAuditTrack != null) {
				if (recipientRole.equalsIgnoreCase("u")) {
					query.setRecipient(finalAuditTrack.getApplicantEmail());
				} else if (recipientRole.equalsIgnoreCase("ca")) {
					if (finalAuditTrack.getStatus().equalsIgnoreCase(Constants.STATUS_CA_PENDING)) {
						query.setRecipient(finalAuditTrack.getToEmail());
					} else {
						query.setRecipient(finalAuditTrack.getCaEmail());
					}
				} else if (recipientRole.equalsIgnoreCase("co")) {
					if (finalAuditTrack.getStatus().equalsIgnoreCase(Constants.STATUS_COORDINATOR_PENDING)) {
						query.setRecipient(finalAuditTrack.getToEmail());
					} else {
						query.setRecipient(finalAuditTrack.getCoordinatorEmail());
					}
				} else if (recipientRole.equalsIgnoreCase("d")) {
					if (finalAuditTrack.getStatus().equalsIgnoreCase(Constants.STATUS_DA_PENDING)) {
						query.setRecipient(finalAuditTrack.getToEmail());
					} else {
						query.setRecipient(finalAuditTrack.getDaEmail());
					}
				} else if (recipientRole.equalsIgnoreCase("m")) {
					if (finalAuditTrack.getStatus().equalsIgnoreCase(Constants.STATUS_MAILADMIN_PENDING)) {
						query.setRecipient(finalAuditTrack.getToEmail());
					} else {
						query.setRecipient(finalAuditTrack.getAdminEmail());
					}
				} else if (recipientRole.equalsIgnoreCase("s")) {
					if (finalAuditTrack.getStatus().equalsIgnoreCase(Constants.STATUS_SUPPORT_PENDING)) {
						query.setRecipient(finalAuditTrack.getToEmail());
					} else {
						query.setRecipient(finalAuditTrack.getSupportEmail());
					}
				}
				
				Query q = queryRepository.save(query);
				if(q.getId()>0) {
					msgStatus.put("success", "Query raised successfully for registration number "+regNumber);
					
					CustomMessage message=new CustomMessage();
					message.setFrom("noreply@nic.in");
					message.setEmail(finalAuditTrack.getApplicantEmail());
					message.setEmailContent("Query raised by "+email+" successfully for registration number " + regNumber+" to emailid "+finalAuditTrack.getToEmail());
					//message.setSmsContent("Application with registration no " + regno.get(k)+ "forwarded successfully to mailadmin " + to_email);
					message.setIsOtp(false);
					utilityService.sendEmailSms(message);

					
					return msgStatus;
				} else {
					msgStatus.put("error", "Query could not be raised for registration number "+regNumber);
					return msgStatus;
				}
			}

			// Once data gets saved, notify recipients
		}
		msgStatus.put("error", "Query could not be raised for registration number "+regNumber);
		return msgStatus;
	}

	private String fetchRecipient(String recipientRole) {
		if (recipientRole.equals("u")) {
			return "User";
		} else if (recipientRole.equals("ca")) {
			return "Competent Authority";
		} else if (recipientRole.equals("s")) {
			return "Support";
		} else if (recipientRole.equals("co")) {
			return "NIC Coordinator";
		} else if (recipientRole.equals("da")) {
			return "Delegated Admin";
		} else if (recipientRole.equals("m")) {
			return "Admin";
		} else {
			return "";
		}
	}

}
