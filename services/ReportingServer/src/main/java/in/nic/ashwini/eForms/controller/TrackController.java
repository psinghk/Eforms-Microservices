package in.nic.ashwini.eForms.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.FinalAuditTrack;
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
		FinalAuditTrack finalAuditTrack = trackService.fetchCompleteDetails(registrationNo);
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
		@RequestParam String srole, @RequestParam String trole, @RequestParam String forward, @RequestParam("email") String email) {
		Map<String, String> map = new HashMap<>();
		map.put("msg", trackService.fetchCurrentStatusByRole(srole, trole, regNumber, forward, email));
		map.put("regNo", regNumber);
		return map;
	}
	@RequestMapping(value = "/searchByKeyword")
	public ResponseEntity<?> searchByKeyword(@RequestParam("keyword") @NotEmpty String keyword) {
		List<FinalAuditTrack> data = trackService.searchByKeyword(keyword);
		return ResponseEntity.ok().body(data);

	}

	@RequestMapping(value = "/export-data")
	public File exportData() throws IOException {
		String excelFileName = "";
		String sheetName = "Export Data";
		List<FinalAuditTrack> finalAuditDataList = trackService.finalAuditTrackExportData();
		
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
		for (FinalAuditTrack auditData : finalAuditDataList) {
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
	
	@PostMapping("fetchForms")
	public Page<FinalAuditTrack> fetchForms(@RequestParam("email") @NotEmpty String email, @RequestParam("allowedForms") @NotEmpty List<String> allowedForms, @RequestParam("searchBy") @NotEmpty String by, @RequestParam("value") @NotEmpty String value, @RequestParam("forRole") @NotEmpty String forRole, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		return trackService.fetchForms(allowedForms, by, value, forRole, sortedByIdDesc);
	}
}
