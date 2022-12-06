package in.nic.ashwini.eForms.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.nic.ashwini.eForms.entities.FinalAuditTrack;
import in.nic.ashwini.eForms.entities.Status;
import in.nic.ashwini.eForms.repositories.FinalAuditTrackRepository;
import in.nic.ashwini.eForms.repositories.StatusRepository;

@Service
public class StatusService {

	private final StatusRepository statusRepository;
	private final FinalAuditTrackRepository finalAuditTrackRepository;
	private final UtilityService utilityService;

	@Autowired
	public StatusService(StatusRepository statusRepository, FinalAuditTrackRepository finalAuditTrackRepository,
			UtilityService utilityService) {
		super();
		this.statusRepository = statusRepository;
		this.finalAuditTrackRepository = finalAuditTrackRepository;
		this.utilityService = utilityService;
	}

	public Boolean isUserRo(String email) {
		List<String> aliases = utilityService.aliases(email);
		for (String sMail : aliases) {
			Optional<Status> status = statusRepository.findFirstByRecipientTypeAndRecipient("ca", email);
			if (status.isPresent()) {
				return true;
			}
		}
		return false;
	}

	public Boolean isRequestPendingWithLoggedInUser(String regNumber, String email) {
		if (finalAuditTrackRepository.countByRegistrationNoAndToEmailContaining(regNumber, email) > 0) {
			return true;
		}
		return false;
	}

	public Boolean isRequestPendingWithSupport(String regNumber) {
		if (finalAuditTrackRepository.countByRegistrationNoAndStatus(regNumber, "support_pending") > 0) {
			return true;
		}
		return false;
	}

	public boolean isRequestManuallySubmitted(@NotEmpty String regNumber, String sMail) {
		if (finalAuditTrackRepository.countByRegistrationNoAndStatusContaining(regNumber, "manaul") > 0) {
			return true;
		}
		return false;
	}

	public Boolean isRequestPendingWithAdmin(String regNumber) {
		if (finalAuditTrackRepository.countByRegistrationNoAndStatus(regNumber, "mail-admin_pending") > 0) {
			return true;
		}
		return false;
	}

	public Boolean isLoggedInUserStakeHolder(String regNumber, List<String> aliases) {
		if (finalAuditTrackRepository.countRegistrationNumbersWhereUserIsStakeHolderButConsideringUniqueEmailInToEmail(
				regNumber, aliases) > 0) {
			return true;
		} else {
			for (String email : aliases) {
				if (finalAuditTrackRepository.countByRegistrationNoAndToEmailContaining(regNumber, email) > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public Set<String> findAllStakeHolders(String regNumber) {
		List<Status> statusRecords = statusRepository.findByRegistrationNoOrderByIdDesc(regNumber);
		Set<String> set = new HashSet<>();
		set.add("u");
		for (Status status : statusRecords) {
			if (status.getStatus().equalsIgnoreCase("ca_pending")) {
				set.add("ca");
			} else if (status.getStatus().equalsIgnoreCase("support_pending")) {
				set.add("s");
			} else if (status.getStatus().equalsIgnoreCase("coordinator_pending")) {
				set.add("c");
			} else if (status.getStatus().equalsIgnoreCase("da_pending")) {
				set.add("d");
			} else if (status.getStatus().equalsIgnoreCase("mail-admin_pending")) {
				set.add("m");
			}
		}
		return set;
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean updateStatusAndFinalAuditTrack(Status status, FinalAuditTrack finalAuditTrack) {
		status = statusRepository.save(status);
		finalAuditTrack = finalAuditTrackRepository.save(finalAuditTrack);
		return true;
	}

	public boolean isEditable(String regNumber, String email) {
		FinalAuditTrack finalAuditTrack = finalAuditTrackRepository.findByRegistrationNo(regNumber);
		if (finalAuditTrack.getStatus().equalsIgnoreCase("ca_pending")
				|| finalAuditTrack.getStatus().equalsIgnoreCase("manual_upload")
				|| finalAuditTrack.getStatus().equalsIgnoreCase("manual")) {
			if (finalAuditTrack.getToEmail().contains(email)
					|| finalAuditTrack.getApplicantEmail().equalsIgnoreCase(email)) {
				return true;
			}
			return false;
		} else {
			List<Status> statusList = statusRepository.findByRegistrationNoOrderByIdDesc(regNumber);
			if (statusList.size() == 1) {
				Status status = statusList.get(0);
				if (status.getSenderType().equals("a") && status.getSender().contains(email)) {
					return true;
				}
				return false;
			} else {
				if (finalAuditTrack.getStatus().contains("pending")
						&& finalAuditTrackRepository.countByRegistrationNoAndToEmailContaining(regNumber, email) > 0) {
					return true;
				}
				return false;
			}
		}
	}

	public FinalAuditTrack fetchFinalAuditTrack(String regNumber) {
		return finalAuditTrackRepository.findByRegistrationNo(regNumber);
	}

	public List<Status> fetchStatusTable(@NotEmpty String regNumber) {
		return statusRepository.findByRegistrationNoOrderByIdDesc(regNumber);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean putOnHold(@NotEmpty String regNumber, @NotEmpty String onHoldStatus) {
		if (statusRepository.updateOnHoldStatus(regNumber, onHoldStatus) > 0)
			return true;
		return false;
	}

	public Page<FinalAuditTrack> showOnHold(Pageable pageable) {
		Page<FinalAuditTrack> list = finalAuditTrackRepository.findByOnHold("y", pageable);
		return list;
	}

	public Page<FinalAuditTrack> showOffHold(Pageable pageable) {
		Page<FinalAuditTrack> list = finalAuditTrackRepository.findByOnHold("n", pageable);
		return list;
	}

	public String showOnHoldRemarks(@NotEmpty String regNumber) {
		String remarks = finalAuditTrackRepository.findByRegistrationNo(regNumber).getHoldRemarks();
		if (remarks != null)
			return remarks;
		return "";
	}

	public boolean isOnHold(String registrationNo) {
		FinalAuditTrack finalAuditTrack = finalAuditTrackRepository.findByRegistrationNo(registrationNo);

		if (finalAuditTrack.getOnHold().equalsIgnoreCase("y")) {
			return true;
		}
		return false;
	}

	public boolean isRequestEsigned(@NotEmpty String regNumber, String sMail) {
		if (finalAuditTrackRepository.countByRegistrationNoAndStatusContaining(regNumber, "esign") > 0) {
			return true;
		}
		return false;
	}

	public boolean isRequestPendingWithNextLevelOfAuthority(@NotEmpty String regNumber, String sMail, String role) {
		FinalAuditTrack finalAuditTrack = finalAuditTrackRepository.findByRegistrationNo(regNumber);
		String status = finalAuditTrack.getStatus();
		String appUserType = finalAuditTrack.getAppUserType();
		String caEmail = finalAuditTrack.getCaEmail();
		String supportEmail = finalAuditTrack.getSupportEmail();
		String coordinatorEmail = finalAuditTrack.getCoordinatorEmail();
		
		if (appUserType.contains("esign")) {
			return false;
		}

		switch (role) {
		case "user":
			if (status.contains("manual") || status.equalsIgnoreCase("ca_pending"))
				return true;
			else {
				if(status.equalsIgnoreCase("coordinator_pending") && (caEmail == null || caEmail.isEmpty()) && (supportEmail == null || supportEmail.isEmpty())) {
					return true;
				}else if(status.equalsIgnoreCase("support_pending") && (caEmail == null || caEmail.isEmpty())) {
					return true;
				}else if(status.equalsIgnoreCase("mail-admin_pending") && (caEmail == null || caEmail.isEmpty()) && (supportEmail == null || supportEmail.isEmpty()) && (coordinatorEmail == null || coordinatorEmail.isEmpty())) {
					return true;
				}else if(status.equalsIgnoreCase("da_pending") && (caEmail == null || caEmail.isEmpty()) && (supportEmail == null || supportEmail.isEmpty()) && (coordinatorEmail == null || coordinatorEmail.isEmpty())) {
					return true;
				}
			}
			break;
		case "ro":
			if(status.equals("ca_pending") || status.equalsIgnoreCase("support_pending")){
				return true;
			}else {
				if(status.equalsIgnoreCase("coordinator_pending") && (supportEmail == null || supportEmail.isEmpty())) {
					return true;
				}else if(status.equalsIgnoreCase("mail-admin_pending") && (supportEmail == null || supportEmail.isEmpty()) && (coordinatorEmail == null || coordinatorEmail.isEmpty())) {
					return true;
				}else if(status.equalsIgnoreCase("da_pending") && (supportEmail == null || supportEmail.isEmpty()) && (coordinatorEmail == null || coordinatorEmail.isEmpty())) {
					return true;
				}
			}
			break;
		case "support":
			if(status.equalsIgnoreCase("support_pending") || status.equalsIgnoreCase("coordinator_pending")){
				return true;
			}else {
				if(status.equalsIgnoreCase("mail-admin_pending") && (coordinatorEmail == null || coordinatorEmail.isEmpty())) {
					return true;
				}else if(status.equalsIgnoreCase("da_pending") && (coordinatorEmail == null || coordinatorEmail.isEmpty())) {
					return true;
				}
			}
			break;
		case "coordinator":
			if(status.equalsIgnoreCase("mail-admin_pending") || status.equalsIgnoreCase("da_pending")){
				return true;
			}
			break;
		case "admin":
			return true;
		}
		return false;
	}
	
	public List<String> fetchCompletedRegNumbers(List<String> regNumber) {
		System.out.println("Check");
		return finalAuditTrackRepository.findCompletedRegistrationNos(regNumber);
	}
}
