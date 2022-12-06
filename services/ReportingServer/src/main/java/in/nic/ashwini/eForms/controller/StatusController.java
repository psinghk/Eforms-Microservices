package in.nic.ashwini.eForms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.entities.FinalAuditTrack;
import in.nic.ashwini.eForms.entities.Status;
import in.nic.ashwini.eForms.models.RegNumberBeanForDns;
import in.nic.ashwini.eForms.services.StatusService;
import in.nic.ashwini.eForms.services.TrackService;
import in.nic.ashwini.eForms.services.UtilityService;

@RestController
public class StatusController {

	private final StatusService statusService;
	private final TrackService trackService;
	private final UtilityService utilityService;

	@Autowired
	public StatusController(StatusService statusService, UtilityService utilityService, TrackService trackService) {
		super();
		this.statusService = statusService;
		this.utilityService = utilityService;
		this.trackService = trackService;
	}

	@GetMapping("/isUserRo")
	public Boolean isUserRo(@RequestParam @NotEmpty String email) {
		return statusService.isUserRo(email);
	}

	@GetMapping("/isRequestPendingWithLoggedInUser")
	public Boolean isRequestPendingWithLoggedInUser(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email) {
		List<String> aliases = utilityService.aliases(email);
		for (String sMail : aliases) {
			if (statusService.isRequestPendingWithLoggedInUser(regNumber, sMail)) {
				return true;
			}
		}
		return false;
	}
	
	@GetMapping("/isRequestPendingWithLoggedInUserOrNextLevelAuthority")
	public Boolean isRequestPendingWithLoggedInUserOrNextLevelAuthority(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email, @RequestParam("role") @NotEmpty String role) {
		List<String> aliases = utilityService.aliases(email);
		for (String sMail : aliases) {
			if (statusService.isRequestPendingWithLoggedInUser(regNumber, sMail)) {
				return true;
			}
		}
		
		if(statusService.isLoggedInUserStakeHolder(regNumber, aliases)) {
			for (String sMail : aliases) {
				if (statusService.isRequestPendingWithNextLevelOfAuthority(regNumber, sMail, role)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@GetMapping("/isLoggedInUserStakeHolder")
	public Boolean isLoggedInUserStakeHolder(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email) {
		List<String> aliases = utilityService.aliases(email);
		return statusService.isLoggedInUserStakeHolder(regNumber, aliases);
	}

	@PostMapping(path = "/updateStatusAndFinalAuditTrack", consumes = "application/json", produces = "application/json")
	public Boolean updateStatusAndFinalAuditTrack(@RequestBody Map<String, Object> data) {
		ObjectMapper mapper = new ObjectMapper();
		Object object = data.get("status");
		Status status = mapper.convertValue(object, Status.class);
		object = data.get("finalAuditTrack");
		FinalAuditTrack finalAuditTrack = mapper.convertValue(object, FinalAuditTrack.class);
		System.out.println("Received a new notification...!!");

//		Status status = (Status) data.get("status");
//		FinalAuditTrack finalAuditTrack = (FinalAuditTrack) data.get("finalAuditTrack");
		return statusService.updateStatusAndFinalAuditTrack(status, finalAuditTrack);
	}

	@GetMapping("/isEditable")
	public boolean isEditable(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email) {
		return statusService.isEditable(regNumber, email);
	}

	@GetMapping("/fetchFinalAuditTrack")
	public FinalAuditTrack fetchFinalAuditTrack(@RequestParam("regNumber") @NotEmpty String regNumber) {
		return statusService.fetchFinalAuditTrack(regNumber);
	}

	@GetMapping("/fetchStatusTable")
	public List<Status> fetchStatusTable(@RequestParam("regNumber") @NotEmpty String regNumber) {
		return statusService.fetchStatusTable(regNumber);
	}

	@GetMapping("/fetchAllStakeHolders")
	public Map<String, String> fetchAllStakeHolders(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("role") @NotEmpty String role) {
		FinalAuditTrack finalAuditTrack = fetchFinalAuditTrack(regNumber);
		Map<String, String> stakeHolders = new HashMap<>();
		if (finalAuditTrack.getCaEmail() != null && !finalAuditTrack.getCaEmail().isEmpty()) {
			stakeHolders.put("ca", "Reporting/Nodal/Forwarding Officer");
		}

		if (finalAuditTrack.getSupportEmail() != null && !finalAuditTrack.getSupportEmail().isEmpty()) {
			stakeHolders.put("s", "Support");
		}

		if (finalAuditTrack.getAdminEmail() != null && !finalAuditTrack.getAdminEmail().isEmpty()) {
			stakeHolders.put("m", "Admin");
		}

		if (finalAuditTrack.getCoordinatorEmail() != null && !finalAuditTrack.getCoordinatorEmail().isEmpty()) {
			stakeHolders.put("co", "Coordinator");
		}

		if (finalAuditTrack.getDaEmail() != null && !finalAuditTrack.getDaEmail().isEmpty()) {
			stakeHolders.put("d", "Delegated Admin");
		}
		if (finalAuditTrack.getApplicantEmail() != null && !finalAuditTrack.getApplicantEmail().isEmpty()) {
			stakeHolders.put("u", "Applicant");
		}

		stakeHolders.remove(role);
		return stakeHolders;
	}

	@RequestMapping("/fetchActions")
	public List<String> fetchActions(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email,
			@RequestParam("allowedForms") @NotEmpty List<String> allowedForms,
			@RequestParam("role") @NotEmpty String currentRole) {
		List<String> actions = new ArrayList<>();
		FinalAuditTrack finalAuditTrack = fetchFinalAuditTrack(regNumber);
		if (currentRole.equalsIgnoreCase("sup") && isRequestPendingWithSupport(regNumber)) {
			if (statusService.isEditable(regNumber, email)) {
				actions.add("icon-edit=>Preview/Edit");
			} else {
				actions.add("icon-display=>Preview");
			}

			actions.add("icon-forward=>Forward");
			actions.add("icon-undo2=>Revert");
			actions.add("icon-cancel-circle=>Reject");
			if(statusService.isOnHold(regNumber)) {
				actions.add("icon-play-circle=>Put off hold");
			}else {
				actions.add("icon-pause-circle=>Put on Hold");
			}
			
			if(finalAuditTrack.getAppUserType().contains("manual")){
				actions.add("icon-play-circle=>Download the scanned copy uploaded by user");
				actions.add("icon-play-circle=>Download the scanned copy uploaded by RO");
			}else if(finalAuditTrack.getAppUserType().contains("esign")) {
				actions.add("icon-play-circle=>Download the user's esigned copy");
			}
			
			if(finalAuditTrack.getAppCaType().contains("esign")) {
				actions.add("icon-play-circle=>Download the RO's esigned copy");
			}
		} else if (currentRole.equalsIgnoreCase("admin") && isRequestPendingWithAdmin(regNumber)) {
			if (statusService.isEditable(regNumber, email)) {
				actions.add("icon-edit=>Preview/Edit");
			} else {
				actions.add("icon-display=>Preview");
			}
			
			actions.add("icon-check-square=>Create ID/Mark as complete");
			actions.add("icon-cancel-circle=>Reject");
			actions.add("icon-forward=>Forward to Admin (iNOC Support)");
			if(statusService.isOnHold(regNumber)) {
				actions.add("icon-play-circle=>Put off hold");
			}else {
				actions.add("icon-pause-circle=>Put on Hold");
			}

			if(finalAuditTrack.getAppUserType().contains("manual")){
				actions.add("icon-play-circle=>Download the scanned copy uploaded by user");
				actions.add("icon-play-circle=>Download the scanned copy uploaded by RO");
			}else if(finalAuditTrack.getAppUserType().contains("esign")) {
				actions.add("icon-play-circle=>Download the user's esigned copy");
			}
			
			if(finalAuditTrack.getAppCaType().contains("esign")) {
				actions.add("icon-play-circle=>Download the RO's esigned copy");
			}
			
		} else if (currentRole.equalsIgnoreCase("co")) {
			if (statusService.isEditable(regNumber, email)) {
				actions.add("icon-edit=>Preview/Edit");
			} else {
				actions.add("icon-display=>Preview");
			}
			
			if (isRequestPendingWithLoggedInUser(regNumber, email)) {
					actions.add("icon-check-circle=>Approve");
					actions.add("icon-cancel-circle=>Reject");
					actions.add("icon-undo2=>Revert");
					if(statusService.isOnHold(regNumber)) {
						actions.add("icon-play-circle=>Put off hold");
					}else {
						actions.add("icon-pause-circle=>Put on Hold");
					}
					
					if(finalAuditTrack.getAppUserType().contains("manual")){
						actions.add("icon-play-circle=>Download the scanned copy uploaded by user");
						actions.add("icon-play-circle=>Download the scanned copy uploaded by RO");
					}else if(finalAuditTrack.getAppUserType().contains("esign")) {
						actions.add("icon-play-circle=>Download the user's esigned copy");
					}
					
					if(finalAuditTrack.getAppCaType().contains("esign")) {
						actions.add("icon-play-circle=>Download the RO's esigned copy");
					}
			}
		} else if (currentRole.equalsIgnoreCase("ro")) {
			if (statusService.isEditable(regNumber, email)) {
				actions.add("icon-edit=>Preview/Edit");
			} else {
				actions.add("icon-display=>Preview");
			}
			
			if (isRequestPendingWithLoggedInUser(regNumber, email)) {
					actions.add("icon-check-circle=>Approve");
					actions.add("icon-cancel-circle=>Reject");
					
					if(finalAuditTrack.getAppUserType().contains("manual")){
						actions.add("icon-play-circle=>Download the scanned copy uploaded by user");
					}
			}
		} else {
			if (statusService.isEditable(regNumber, email)) {
				actions.add("icon-edit=>Preview/Edit");
			} else {
				actions.add("icon-display=>Preview");
			}

			if (isRequestPendingWithLoggedInUser(regNumber, email)) {
				if (isRequestManuallySubmitted(regNumber, email)) {
					actions.add("icon-upload-cloud=>Final Submit");
					actions.add("icon-cancel-circle=>Cancel");
				} else {
					actions.add("icon-check-circle=>Approve");
					actions.add("icon-cancel-circle=>Reject");
				}
			}
		}

		actions.add("icon-location=>Track");
		actions.add("icon-file-pdf=>Generate PDF");
		actions.add("icon-bubbles3=>Raise/Respond to query");
		actions.add("icon-upload=>Upload/Download Docs");
		return actions;
	}

	@GetMapping("/isRequestManuallySubmitted")
	public Boolean isRequestManuallySubmitted(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email) {
		List<String> aliases = utilityService.aliases(email);
		for (String sMail : aliases) {
			if (statusService.isRequestManuallySubmitted(regNumber, sMail)) {
				return true;
			}
		}
		return false;
	}
	
	@GetMapping("/isRequestEsigned")
	public Boolean isRequestEsigned(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email) {
		List<String> aliases = utilityService.aliases(email);
		for (String sMail : aliases) {
			if (statusService.isRequestEsigned(regNumber, sMail)) {
				return true;
			}
		}
		return false;
	}

	@GetMapping("/isRequestPendingWithSupport")
	public Boolean isRequestPendingWithSupport(@RequestParam("regNumber") @NotEmpty String regNumber) {
		if (statusService.isRequestPendingWithSupport(regNumber)) {
			return true;
		}
		return false;
	}

	@GetMapping("/isRequestPendingWithAdmin")
	public Boolean isRequestPendingWithAdmin(@RequestParam("regNumber") @NotEmpty String regNumber) {
		if (statusService.isRequestPendingWithAdmin(regNumber)) {
			return true;
		}
		return false;
	}

	@PostMapping("/putOnHold")
	public Map<String, String> putOnHold(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email, @RequestParam("remarks") @NotEmpty String remarks) {
		boolean isUpdatedInStatustable = statusService.putOnHold(regNumber, "y");
		boolean isUpdatedInTrackTable = trackService.putOnHold(regNumber, remarks, "y");
		Map<String, String> res = new HashMap<>();
		if (isUpdatedInStatustable && isUpdatedInTrackTable) {
			res.put("success", "Application (" + regNumber + ") put on hold successfully");
		} else if (!isUpdatedInStatustable && !isUpdatedInTrackTable) {
			res.put("error", "Application (" + regNumber + ") could not be put on Hold!!! Please try after some time.");
		} else if (!isUpdatedInStatustable) {
			trackService.putOnHold(regNumber, "", "");
			res.put("error", "Application (" + regNumber + ") could not be put on Hold!!! Please try after some time.");
		} else {
			statusService.putOnHold(regNumber, "");
			res.put("error", "Application (" + regNumber + ") could not be put on Hold!!! Please try after some time.");
		}
		return res;
	}

	@PostMapping("/putOffHold")
	public Map<String, String> putOffHold(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email, @RequestParam("remarks") @NotEmpty String remarks) {
		boolean isUpdatedInStatustable = statusService.putOnHold(regNumber, "n");
		boolean isUpdatedInTrackTable = trackService.putOnHold(regNumber, remarks, "n");
		Map<String, String> res = new HashMap<>();
		if (isUpdatedInStatustable && isUpdatedInTrackTable) {
			res.put("success", "Application (" + regNumber + ") put off hold successfully");
		} else if (!isUpdatedInStatustable && !isUpdatedInTrackTable) {
			res.put("success", "Application (" + regNumber + ") could not be put off Hold!!! Please try after some time.");
		} else if (!isUpdatedInStatustable) {
			trackService.putOnHold(regNumber, "", "");
			res.put("success", "Application (" + regNumber + ") could not be put off Hold!!! Please try after some time.");
		} else {
			statusService.putOnHold(regNumber, "");
			res.put("success", "Application (" + regNumber + ") could not be put off Hold!!! Please try after some time.");
		}
		return res;
	}

	@GetMapping("/showOnHold")
	public Page<FinalAuditTrack> showOnHold(@RequestParam("email") @NotEmpty String email, Pageable pageable) {
		return statusService.showOnHold(pageable);
	}

	@GetMapping("/showOffHold")
	public Page<FinalAuditTrack> showOffHold(@RequestParam("email") @NotEmpty String email, Pageable pageable) {
		return statusService.showOffHold(pageable);
	}
	
	@GetMapping("/showOnHoldRemarks")
	public Map<String, String> showOnHoldRemarks(@RequestParam("regNumber") @NotEmpty String regNumber,
			@RequestParam("email") @NotEmpty String email) {
		Map<String, String> res = new HashMap<>();
		res.put("remarks", statusService.showOnHoldRemarks(regNumber));
		return res;
	}
	
	@RequestMapping("/fetchCompletedRegNumbers")
	public List<String> fetchCompletedRegNumbers(@RequestBody RegNumberBeanForDns regNumberBean) {
		return statusService.fetchCompletedRegNumbers(regNumberBean.getRegNumbers());
	}
}
