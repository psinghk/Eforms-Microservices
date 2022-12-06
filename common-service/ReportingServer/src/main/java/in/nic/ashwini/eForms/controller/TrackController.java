package in.nic.ashwini.eForms.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.db.master.entities.FinalAuditTrack;
import in.nic.ashwini.eForms.db.master.entities.Status;
import in.nic.ashwini.eForms.db.slave.entities.FinalAuditTrackFromSlave;
import in.nic.ashwini.eForms.models.RequestBean;
import in.nic.ashwini.eForms.models.TrackDto;
import in.nic.ashwini.eForms.services.TrackService;
import in.nic.ashwini.eForms.services.UtilityService;

@RestController
@RequestMapping("/track")
public class TrackController {

	private final TrackService trackService;

	private final UtilityService utilityService;

	@Autowired
	public TrackController(TrackService trackService, UtilityService utilityService) {
		super();
		this.trackService = trackService;
		this.utilityService = utilityService;
	}

	@GetMapping("/checkCurrentStatus")
	public TrackDto checkCurrentStatus(@RequestParam("regNumber") @NotEmpty String registrationNo,
			@RequestParam("email") @NotEmpty String email) {
		TrackDto track = new TrackDto();
		track.setRoles(trackService.fetchRoles(registrationNo));
		FinalAuditTrackFromSlave finalAuditTrack = trackService.fetchCompleteDetails(registrationNo);
		if (finalAuditTrack != null) {
			track.setApplicantEmail(finalAuditTrack.getApplicantEmail());
			track.setApplicantMobile(finalAuditTrack.getApplicantMobile());
			track.setApplicantName(finalAuditTrack.getApplicantName());
			track.setFormName(finalAuditTrack.getFormName());
			track.setRegNumber(registrationNo);
			track.setSubmissionDateTime(finalAuditTrack.getApplicantDatetime());

			String currentStatus = finalAuditTrack.getStatus().toLowerCase();
			if (currentStatus.contains("pending") || currentStatus.contains("manual")) {
				track.setStatus("Pending");
			} else if (currentStatus.contains("rejected") || currentStatus.contains("cancel")
					|| currentStatus.contains("us_expired")) {
				track.setStatus("Rejected");
			} else if (currentStatus.contains("completed")) {
				track.setStatus("Completed");
			} else if (currentStatus.isEmpty()) {
				track.setStatus("Application cancelled by User.");
			}

			track.setMessage(trackService.generateMessageForTrack(finalAuditTrack.getToDatetime(),
					finalAuditTrack.getToEmail(), currentStatus, registrationNo, email));
		}
		return track;
	}

	@GetMapping("/fetchCurrentStatusByRole")
	public Map<String, String> fetchCurrentStatusByRole(@RequestParam @NotEmpty String regNumber,
			@RequestParam String srole, @RequestParam String trole, @RequestParam String forward,@RequestParam Integer position,
			@RequestParam("email") String email) {
		Map<String, String> map = new HashMap<>();
		map.put("msg", trackService.fetchCurrentStatusByRole(srole, trole, regNumber, forward, email, position));
		map.put("regNo", regNumber);
		return map;
	}

	@GetMapping(value = "/searchByKeyword")
	public ResponseEntity<?> searchByKeyword(@RequestParam("keyword") @NotEmpty String keyword) {
		List<FinalAuditTrackFromSlave> data = trackService.searchByKeyword(keyword);
		return ResponseEntity.ok().body(data);

	}

	@GetMapping(value = "/export-data")
	public File exportData() throws IOException {
		String excelFileName = "";
		String sheetName = "Export Data";
		List<FinalAuditTrackFromSlave> finalAuditDataList = trackService.finalAuditTrackExportData();

		if (finalAuditDataList.isEmpty()) {
			excelFileName = "/tmp/norecord.xls";
		} else {
			excelFileName = "D://data.xls";
		}

		List<String> headerList = new ArrayList<>();
		headerList.add("Registration No");
		headerList.add("Applicant Email");
		headerList.add("Applicant Mobile");
		headerList.add("Applicant Name");
		headerList.add("Applicant Ip");
		headerList.add("DateTime");
		headerList.add("Status");
		headerList.add("SubmissionType");

		List<String[]> dataList = new ArrayList<>();
		for (FinalAuditTrackFromSlave auditData : finalAuditDataList) {
			String[] trackData = new String[8];
			trackData[0] = auditData.getRegistrationNo();
			trackData[1] = auditData.getApplicantEmail();
			trackData[2] = auditData.getApplicantMobile();
			trackData[3] = auditData.getApplicantName();
			trackData[4] = auditData.getApplicantIp();
			trackData[5] = auditData.getToDatetime().toString();
			trackData[6] = auditData.getStatus();
			trackData[7] = auditData.getAppUserType();
			dataList.add(trackData);
		}

		return utilityService.createWorkbookHash(headerList, dataList, excelFileName, sheetName);

	}

	@GetMapping("fetchForms")
	public Page<FinalAuditTrackFromSlave> fetchForms(@RequestParam("email") @NotEmpty String email,
			@RequestParam("allowedForms") @NotEmpty List<String> allowedForms,
			@RequestParam("searchBy") @NotEmpty String by, @RequestParam("value") @NotEmpty String value,
			@RequestParam("forRole") @NotEmpty String forRole, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		return trackService.fetchForms(allowedForms, by, value, forRole, sortedByIdDesc);
	}

	// new added
	@GetMapping("/findByApplicantEmailAndStatus")
	public List<FinalAuditTrackFromSlave> findByApplicantEmailAndStatus(@RequestParam("applicantemail") String email,
			@RequestParam("status") String status) {
		return trackService.findByApplicantEmailAndStatus(email, status);
	}

	@PostMapping("/findByApplicantEmailInAndStatusIn")
	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndStatusIn(@RequestBody RequestBean request) {
		return trackService.findByApplicantEmailInAndStatusIn(request.getApplicantEmails(), request.getStatus());
	}

	@PostMapping("/findByApplicantEmailInAndStatusInPageable")
	public Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndStatusInPageable(@RequestBody RequestBean request,
			Pageable pageable) {
		return trackService.findByApplicantEmailInAndStatusIn(request.getApplicantEmails(), request.getStatus(),
				pageable);
	}

	@PostMapping("/countByApplicantEmailInAndStatusIn")
	public Integer countByApplicantEmailInAndStatusIn(@RequestBody RequestBean request) {
		return trackService.findByApplicantEmailInAndStatusIn(request.getApplicantEmails(), request.getStatus()).size();
	}

	@GetMapping("/findByApplicantEmailAndToEmailAndStatus")
	public List<FinalAuditTrackFromSlave> findByToEmailAndStatus(@RequestParam("applicantemail") String applicantemail,
			@RequestParam("toemail") String toemail, @RequestParam("status") String status) {
		return trackService.findByApplicantEmailAndToEmailAndStatus(applicantemail, toemail, status);
	}

	@PostMapping("/findTotalRequestsOfHog")
	public Page<FinalAuditTrackFromSlave> findTotalRequestsOfHog(@RequestParam("email") String hogMail,
			@RequestBody RequestBean request, Pageable pageable) {
		return trackService.findByApplicantEmailInAndStatusInOrApplicantEmailInAndHogEmailAndStatusIn(
				request.getApplicantEmails(), Arrays.asList("hog_pending"), request.getApplicantEmails(), hogMail,
				Arrays.asList("completed", "mail-admin_pending", "da_pending", "mail-admin_rejected", "da_rejected",
						"hog_rejected"), pageable);

	}

	@PostMapping("/findByApplicantEmailInAndToEmailAndStatusIn")
	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndToEmailAndStatusIn(@RequestBody RequestBean request,
			@RequestParam String toEmail) {
		return trackService.findByApplicantEmailInAndToEmailAndStatusIn(request.getApplicantEmails(), toEmail,
				request.getStatus());
	}

	@PostMapping("/countByApplicantEmailInAndToEmailAndStatusIn")
	public Integer countByApplicantEmailInAndToEmailAndStatusIn(@RequestBody RequestBean request,
			@RequestParam String toEmail) {
		return trackService
				.findByApplicantEmailInAndToEmailAndStatusIn(request.getApplicantEmails(), toEmail, request.getStatus())
				.size();
	}

	@GetMapping("/findByApplicantEmailAndCoordinatorEmailAndStatus")
	public List<FinalAuditTrackFromSlave> findByApplicantEmailAndCoordinatorEmailAndStatus(
			@RequestParam("applicantemail") String applicantemail, @RequestParam("coordemail") String coordemail,
			@RequestParam("status") String status) {
		return trackService.findByApplicantEmailAndCoordinatorEmailAndStatus(applicantemail, coordemail, status);
	}

	@PostMapping("/findByApplicantEmailInAndCoordinatorEmailAndStatusIn")
	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndCoordinatorEmailAndStatusIn(
			@RequestBody RequestBean request, @RequestParam String coordEmail) {
		return trackService.findByApplicantEmailInAndCoordinatorEmailAndStatusIn(request.getApplicantEmails(),
				coordEmail, request.getStatus());
	}

	@PostMapping("/countByApplicantEmailInAndCoordinatorEmailAndStatusIn")
	public Integer countByApplicantEmailInAndCoordinatorEmailAndStatusIn(@RequestBody RequestBean request,
			@RequestParam String coordEmail) {
		return trackService.findByApplicantEmailInAndCoordinatorEmailAndStatusIn(request.getApplicantEmails(),
				coordEmail, request.getStatus()).size();
	}

	@GetMapping("/findByApplicantEmailAndCoordinatorEmailAndStatusNot")
	public List<FinalAuditTrackFromSlave> findByApplicantEmailAndCoordinatorEmailAndStatusNot(
			@RequestParam("applicantemail") String applicantemail, @RequestParam("coordemail") String coordemail,
			@RequestParam("status") String status) {
		return trackService.findByApplicantEmailAndCoordinatorEmailAndStatusNot(applicantemail, coordemail, status);
	}

	@PostMapping("/findByApplicantEmailInAndCoordinatorEmailAndStatusNotIn")
	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndCoordinatorEmailAndStatusNotIn(
			@RequestBody RequestBean request, @RequestParam String coordEmail) {
		return trackService.findByApplicantEmailInAndCoordinatorEmailAndStatusNotIn(request.getApplicantEmails(),
				coordEmail, request.getStatus());
	}

	@PostMapping("/countByApplicantEmailInAndCoordinatorEmailAndStatusNotIn")
	public Integer countByApplicantEmailInAndCoordinatorEmailAndStatusNotIn(@RequestBody RequestBean request,
			@RequestParam String coordEmail) {
		return trackService.findByApplicantEmailInAndCoordinatorEmailAndStatusNotIn(request.getApplicantEmails(),
				coordEmail, request.getStatus()).size();
	}

	@PostMapping("/findByApplicantEmailInAndHogEmailAndStatusIn")
	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndHogEmailAndStatusIn(@RequestBody RequestBean request,
			@RequestParam String hogEmail) {
		return trackService.findByApplicantEmailInAndHogEmailAndStatusIn(request.getApplicantEmails(), hogEmail,
				request.getStatus());
	}

	@PostMapping("/findByApplicantEmailInAndHogEmailAndStatusInPageable")
	public Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndHogEmailAndStatusInPageable(
			@RequestBody RequestBean request, @RequestParam String hogEmail, Pageable pageable) {
		return trackService.findByApplicantEmailInAndHogEmailAndStatusIn(request.getApplicantEmails(), hogEmail,
				request.getStatus(), pageable);
	}

	@PostMapping("findByApplicantEmailInAndStatusInOrApplicantEmailInAndHogEmailAndStatusIn")
	public Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndStatusInOrApplicantEmailInAndHogEmailAndStatusIn(
			@RequestBody RequestBean request, @RequestParam String hogEmail, @RequestParam List<String> statusList,
			Pageable pageable) {
		return trackService.findByApplicantEmailInAndStatusInOrApplicantEmailInAndHogEmailAndStatusIn(
				request.getApplicantEmails(), statusList, request.getApplicantEmails(), hogEmail, request.getStatus(),
				pageable);
	}

	@PostMapping("/countByApplicantEmailInAndHogEmailAndStatusIn")
	public Integer countByApplicantEmailInAndHogEmailAndStatusIn(@RequestBody RequestBean request,
			@RequestParam String hogEmail) {
		return trackService.countByApplicantEmailInAndHogEmailAndStatusIn(request.getApplicantEmails(), hogEmail,
				request.getStatus());
	}

	@PostMapping("/countByApplicantEmailInAndHogEmailInAndStatusIn")
	public Integer countByApplicantEmailInAndHogEmailInAndStatusIn(@RequestBody RequestBean request,
			@RequestParam String hogEmail) {
		List<String> aliases = utilityService.aliases(hogEmail);
		return trackService.countByApplicantEmailInAndHogEmailInAndStatusIn(request.getApplicantEmails(), aliases,
				request.getStatus());
	}

	@PostMapping("/savedata")
	public FinalAuditTrack savedata(@RequestBody FinalAuditTrack finalAuditTrack) {

		return trackService.savedata(finalAuditTrack);
	}

	@PostMapping("/saveDataStatus")
	public Status saveDataStatus(@RequestBody Status status) {

		return trackService.saveDataStatus(status);
	}

	@GetMapping("/findByRegistrationNoFinalAudit")
	public FinalAuditTrackFromSlave findByRegistrationNoFinalAudit(String regno) {

		FinalAuditTrackFromSlave retval = trackService.findByRegistrationNo(regno);
		return retval;
	}

}
