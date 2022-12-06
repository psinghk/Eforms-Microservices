package in.nic.ashwini.eForms.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.db.master.entities.SearchBean;
import in.nic.ashwini.eForms.db.master.repositories.FinalAuditTrackRepository;
import in.nic.ashwini.eForms.db.slave.entities.FinalAuditTrackFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.FinalAuditTrackRepositoryToRead;
import in.nic.ashwini.eForms.models.ApplicantEmailsBean;
import in.nic.ashwini.eForms.services.Constants;
import in.nic.ashwini.eForms.services.DashBoardService;
import in.nic.ashwini.eForms.services.UtilityService;

@RestController
@RequestMapping("/user/requests/")
public class UserDashboardController {
	private final FinalAuditTrackRepository finalAuditTrackRepository;
	private final FinalAuditTrackRepositoryToRead finalAuditTrackRepositoryToRead;
	private final UtilityService utilityService;
	private final DashBoardService dashBoardService;

	@Autowired
	public UserDashboardController(FinalAuditTrackRepository finalAuditTrackRepository, UtilityService utilityService,
			DashBoardService dashBoardService, FinalAuditTrackRepositoryToRead finalAuditTrackRepositoryToRead) {
		super();
		this.finalAuditTrackRepository = finalAuditTrackRepository;
		this.utilityService = utilityService;
		this.dashBoardService = dashBoardService;
		this.finalAuditTrackRepositoryToRead = finalAuditTrackRepositoryToRead;
	}

	@PostMapping("pending")
	public Page<FinalAuditTrackFromSlave> pending(@RequestParam("email") @NotEmpty String email, SearchBean searchBean,
			Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("ca_pending", "coordinator_pending", "mail-admin_pending",
				"da_pending", "support_pending", "us_pending", "manual", "manual_upload", "api", "domainapi");
		List<String> emailList = utilityService.aliases(email);

		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByStatusInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							status_list, emailList, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByStatusInAndApplicantEmailIn(status_list, emailList, sortedByIdDesc);
	}

	@PostMapping("completed")
	public Page<FinalAuditTrackFromSlave> completed(@RequestParam("email") @NotEmpty String email, SearchBean searchBean, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("completed");
		List<String> emailList = utilityService.aliases(email);
		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByStatusInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							status_list, emailList, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByStatusInAndApplicantEmailIn(status_list, emailList, sortedByIdDesc);
	}

	@PostMapping("rejected")
	public Page<FinalAuditTrackFromSlave> rejected(@RequestParam("email") @NotEmpty String email, SearchBean searchBean,
			Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("cancel", "ca_rejected", "support_rejected", "coordinator_rejected",
				"mail-admin_rejected", "da_rejected", "us_rejected");
		List<String> emailList = utilityService.aliases(email);
		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByStatusInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							status_list, emailList, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByStatusInAndApplicantEmailIn(status_list, emailList, sortedByIdDesc);
	}

	@GetMapping("pendingWithUser")
	public Page<FinalAuditTrackFromSlave> pendingWithUser(@RequestParam("email") @NotEmpty String email, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("manual", "manual_upload", "api", "domainapi");
		List<String> emailList = utilityService.aliases(email);
		return finalAuditTrackRepositoryToRead.findByStatusInAndApplicantEmailIn(status_list, emailList, sortedByIdDesc);
	}

	@GetMapping("cancelled")
	public Page<FinalAuditTrackFromSlave> cancelled(@RequestParam("email") @NotEmpty String email, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("cancel");
		List<String> emailList = utilityService.aliases(email);
		return finalAuditTrackRepositoryToRead.findByStatusInAndApplicantEmailIn(status_list, emailList, sortedByIdDesc);
	}

	@PostMapping("forwarded")
	public Page<FinalAuditTrackFromSlave> forwarded(@RequestParam("email") @NotEmpty String email, SearchBean searchBean, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("completed", "ca_pending", "coordinator_pending", "mail-admin_pending",
				"da_pending", "support_pending", "us_pending", "ca_rejected", "support_rejected",
				"coordinator_rejected", "mail-admin_rejected", "da_rejected", "us_rejected");
		List<String> emailList = utilityService.aliases(email);
		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByStatusInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							status_list, emailList, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByStatusInAndApplicantEmailIn(status_list, emailList, sortedByIdDesc);
	}

	@PostMapping("all")
	public Page<FinalAuditTrackFromSlave> all(@RequestParam("email") @NotEmpty String email, SearchBean searchBean, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> emailList = utilityService.aliases(email);
		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							emailList, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByApplicantEmailIn(emailList, sortedByIdDesc);
	}

	@GetMapping("fetchDashBoardCounts")
	public Map<String, Integer> fetchDashBoardCounts(@RequestParam("email") @NotEmpty String email) {
		Map<String, Integer> map = new HashMap<>();
		map.put("totalPending", countPending(email));
		map.put("totalCompleted", countCompleted(email));
		map.put("totalRejeted", countRejected(email));
		map.put("totalForwarded", countForwarded(email));
		map.put("totalCancelled", countCancelled(email));
		map.put("totalPendingWithUser", countPendingWithUser(email));
		map.put("totalAll", countAll(email));
		return map;
	}

	@GetMapping("count/pending")
	public int countPending(@RequestParam("email") @NotEmpty String email) {
		List<String> status_list = Arrays.asList("ca_pending", "coordinator_pending", "mail-admin_pending",
				"da_pending", "support_pending", "us_pending", "manual", "manual_upload", "api", "domainapi");
		List<String> emailList = utilityService.aliases(email);
		return finalAuditTrackRepositoryToRead.countByStatusInAndApplicantEmailIn(status_list, emailList);
	}

	@GetMapping("count/completed")
	public int countCompleted(@RequestParam("email") @NotEmpty String email) {
		List<String> status_list = Arrays.asList("completed");
		List<String> emailList = utilityService.aliases(email);
		return finalAuditTrackRepositoryToRead.countByStatusInAndApplicantEmailIn(status_list, emailList);
	}

	@GetMapping("count/rejected")
	public int countRejected(@RequestParam("email") @NotEmpty String email) {
		List<String> status_list = Arrays.asList("cancel", "ca_rejected", "support_rejected", "coordinator_rejected",
				"mail-admin_rejected", "da_rejected", "us_rejected");
		List<String> emailList = utilityService.aliases(email);
		return finalAuditTrackRepositoryToRead.countByStatusInAndApplicantEmailIn(status_list, emailList);
	}

	@GetMapping("count/pendingWithUser")
	public int countPendingWithUser(@RequestParam("email") @NotEmpty String email) {
		List<String> status_list = Arrays.asList("manual", "manual_upload", "api", "domainapi");
		List<String> emailList = utilityService.aliases(email);
		return finalAuditTrackRepositoryToRead.countByStatusInAndApplicantEmailIn(status_list, emailList);
	}

	@GetMapping("count/cancelled")
	public int countCancelled(@RequestParam("email") @NotEmpty String email) {
		List<String> status_list = Arrays.asList("cancel");
		List<String> emailList = utilityService.aliases(email);
		return finalAuditTrackRepositoryToRead.countByStatusInAndApplicantEmailIn(status_list, emailList);
	}

	@GetMapping("count/forwarded")
	public int countForwarded(@RequestParam("email") @NotEmpty String email) {
		List<String> status_list = Arrays.asList("completed", "ca_pending", "coordinator_pending", "mail-admin_pending",
				"da_pending", "support_pending", "us_pending", "ca_rejected", "support_rejected",
				"coordinator_rejected", "mail-admin_rejected", "da_rejected", "us_rejected");
		List<String> emailList = utilityService.aliases(email);
		return finalAuditTrackRepositoryToRead.countByStatusInAndApplicantEmailIn(status_list, emailList);
	}

	@GetMapping("count/all")
	public int countAll(@RequestParam("email") @NotEmpty String email) {
		List<String> emailList = utilityService.aliases(email);
		return finalAuditTrackRepositoryToRead.countByApplicantEmailIn(emailList);
	}
	
	@PostMapping("count/all/ministries")
	public Integer countAll(@RequestBody ApplicantEmailsBean applicantEmailsBean) {
		List<String> applicantEmails = applicantEmailsBean.getApplicantEmails();
//		List<String> emailList = new ArrayList<>();
//		for (String email : applicantEmails) {
//			List<String> emailList1 = utilityService.aliases(email);
//			emailList.addAll(emailList1);
//		}
		Set<String> set = new HashSet<>(applicantEmails);
		Map<String, Integer> map = new HashMap<>();
		List<String> emailList = new ArrayList<>(set);
		map.put("total", finalAuditTrackRepositoryToRead.countByApplicantEmailIn(emailList));
		List<String> status_list = Arrays.asList("hog_pending", "coordinator_pending", "mail-admin_pending",
				"da_pending", "support_pending", "us_pending");
		map.put("pending", finalAuditTrackRepositoryToRead.countByStatusInAndApplicantEmailIn(status_list, emailList));
		status_list = Arrays.asList("completed");
		map.put("completed", finalAuditTrackRepositoryToRead.countByStatusInAndApplicantEmailIn(status_list, emailList));
		return finalAuditTrackRepositoryToRead.countByApplicantEmailIn(new ArrayList<>(set));
	}

	@GetMapping("fetchFilter")
	public Map<String, Set<String>> fetchFilter(@RequestParam("email") @NotEmpty String email) {
		Map<String, Set<String>> map = new HashMap<>();
		List<String> emailList = utilityService.aliases(email);
		Set<String> services = dashBoardService
				.convertFormNamesForFilters(finalAuditTrackRepositoryToRead.findDistinctFormNamesForUser(emailList));
		Set<String> requestTypes = dashBoardService
				.setRequestTypes(finalAuditTrackRepositoryToRead.findDistinctStatusForUser(emailList));

		map.put("services", services);
		map.put("requestTypes", requestTypes);
		return map;
	}

//	@PostMapping("applyFilter")
//	private Page<FinalAuditTrack> filteredRequests(@RequestParam("email") @NotEmpty String email, @ModelAttribute SearchBean searchBean, Pageable pageable) {
//		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
//				Sort.by("toDatetime").descending());
//		List<String> status_list = null;
//		List<String> service_list = null;
//		if(searchBean.getServices() == null && searchBean.getStatus() == null) {
//			return pending(email, pageable);
//		} else if(searchBean.getServices() != null && searchBean.getServices().size() == 0 && searchBean.getStatus() != null && searchBean.getStatus().size() == 0) {
//			return pending(email, pageable);
//		} else if(searchBean.getServices() != null && searchBean.getServices().size() > 0 && (searchBean.getStatus() == null || (searchBean.getStatus() != null && searchBean.getStatus().size() == 0))){
//			service_list = utilityService.fetchListOfServices(searchBean.getServices());
//			List<String> emailList = utilityService.aliases(email);
//			return finalAuditTrackRepository.findByFormNameInAndApplicantEmailIn(service_list, emailList, sortedByIdDesc);
//		} else if(searchBean.getStatus() != null && searchBean.getStatus().size() > 0 && (searchBean.getServices() == null || (searchBean.getServices() != null && searchBean.getServices().size() == 0))) {
//			status_list = utilityService.fetchListOfStatus(searchBean.getStatus(),"user");
//			List<String> emailList = utilityService.aliases(email);
//			return finalAuditTrackRepository.findByStatusInAndApplicantEmailIn(status_list, emailList, sortedByIdDesc);
//		} else {
//			service_list = utilityService.fetchListOfServices(searchBean.getServices());
//			status_list = utilityService.fetchListOfStatus(searchBean.getStatus(),"user");
//			List<String> emailList = utilityService.aliases(email);
//			return finalAuditTrackRepository.findByStatusInAndFormNameInAndApplicantEmailIn(status_list, service_list, emailList, sortedByIdDesc);
//		}
//	}

	@PostMapping("applyFilter")
	private Page<FinalAuditTrackFromSlave> filteredRequests(@RequestParam("email") @NotEmpty String email,
			@ModelAttribute SearchBean searchBean, String searchKey, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = null;
		List<String> service_list = null;
		String statusString = "";
		if (searchBean.getServices() == null && searchBean.getStatus() == null) {
			if (searchKey == null || searchKey.isEmpty())
				return pending(email, searchBean, pageable);
			else {
				status_list = Arrays.asList("ca_pending", "coordinator_pending", "mail-admin_pending", "da_pending",
						"support_pending", "us_pending", "manual", "manual_upload", "api", "domainapi");
				List<String> emailList = utilityService.aliases(email);

				statusString = fetchSearchString(searchKey);

				if (statusString.isEmpty()) {
					statusString = searchKey;
				}

				return finalAuditTrackRepositoryToRead
						.findByStatusInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								status_list, emailList, searchKey, searchKey, searchKey, searchKey, statusString,
								searchKey, searchKey, sortedByIdDesc);
			}
		} else if (searchBean.getServices() != null && searchBean.getServices().size() == 0
				&& searchBean.getStatus() != null && searchBean.getStatus().size() == 0) {
			if (searchKey == null || searchKey.isEmpty())
				return pending(email, searchBean, pageable);
			else {
				status_list = Arrays.asList("ca_pending", "coordinator_pending", "mail-admin_pending", "da_pending",
						"support_pending", "us_pending", "manual", "manual_upload", "api", "domainapi");
				List<String> emailList = utilityService.aliases(email);

				statusString = fetchSearchString(searchKey);

				if (statusString.isEmpty()) {
					statusString = searchKey;
				}

				return finalAuditTrackRepositoryToRead
						.findByStatusInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								status_list, emailList, searchKey, searchKey, searchKey, searchKey, statusString,
								searchKey, searchKey, sortedByIdDesc);
			}
		} else if (searchBean.getServices() != null && searchBean.getServices().size() > 0
				&& (searchBean.getStatus() == null
						|| (searchBean.getStatus() != null && searchBean.getStatus().size() == 0))) {
			service_list = utilityService.fetchListOfServices(searchBean.getServices());
			List<String> emailList = utilityService.aliases(email);

			if (searchKey == null || searchKey.isEmpty())
				return finalAuditTrackRepositoryToRead.findByFormNameInAndApplicantEmailIn(service_list, emailList,
						sortedByIdDesc);
			else {
				statusString = fetchSearchString(searchKey);

				if (statusString.isEmpty()) {
					statusString = searchKey;
				}
				return finalAuditTrackRepositoryToRead
						.findByFormNameInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								service_list, emailList, searchKey, searchKey, searchKey, searchKey, searchKey,
								searchKey, searchKey, sortedByIdDesc);
			}
		} else if (searchBean.getStatus() != null && searchBean.getStatus().size() > 0
				&& (searchBean.getServices() == null
						|| (searchBean.getServices() != null && searchBean.getServices().size() == 0))) {
			status_list = utilityService.fetchListOfStatus(searchBean.getStatus(), "user");
			List<String> emailList = utilityService.aliases(email);

			if (searchKey == null || searchKey.isEmpty())
				return finalAuditTrackRepositoryToRead.findByStatusInAndApplicantEmailIn(status_list, emailList,
						sortedByIdDesc);
			else {
				statusString = fetchSearchString(searchKey);

				if (statusString.isEmpty()) {
					statusString = searchKey;
				}

				return finalAuditTrackRepositoryToRead
						.findByStatusInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								status_list, emailList, searchKey, searchKey, searchKey, searchKey, statusString,
								searchKey, searchKey, sortedByIdDesc);
			}
		} else {
			service_list = utilityService.fetchListOfServices(searchBean.getServices());
			status_list = utilityService.fetchListOfStatus(searchBean.getStatus(), "user");
			List<String> emailList = utilityService.aliases(email);

			if (searchKey == null || searchKey.isEmpty())
				return finalAuditTrackRepositoryToRead.findByStatusInAndFormNameInAndApplicantEmailIn(status_list,
						service_list, emailList, sortedByIdDesc);
			else {
				statusString = fetchSearchString(searchKey);

				if (statusString.isEmpty()) {
					statusString = searchKey;
				}

				return finalAuditTrackRepositoryToRead
						.findByStatusInAndFormNameInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								status_list, service_list, emailList, searchKey, searchKey, searchKey, searchKey,
								statusString, searchKey, searchKey, sortedByIdDesc);
			}
		}
	}

	private String fetchSearchString(String searchKey) {
		String statusString = "";
		if (!searchKey.isEmpty()) {
			if (Constants.CA_PENDING.contains(searchKey)) {
				statusString = "ca_pending";
			} else if (Constants.COORDINATOR_PENDING.contains(searchKey)) {
				statusString = "coordinator_pending";
			} else if (Constants.SUPPORT_PENDING.contains(searchKey)) {
				statusString = "support_pending";
			} else if (Constants.MAIL_ADMIN_PENDING.contains(searchKey)) {
				statusString = "mail-admin_pending";
			} else if (Constants.CA_REJECTED.contains(searchKey)) {
				statusString = "ca_rejected_pending";
			} else if (Constants.COORDINATOR_REJECTED.contains(searchKey)) {
				statusString = "coordinator_rejected";
			} else if (Constants.SUPPORT_REJECTED.contains(searchKey)) {
				statusString = "support_rejected";
			} else if (Constants.MAIL_ADMIN_REJECTED.contains(searchKey)) {
				statusString = "mail-admin_rejected";
			} else if (Constants.COMPLETED.contains(searchKey)) {
				statusString = "completed";
			}

			if (searchKey.contains("re") || searchKey.contains("fo") || searchKey.contains("no")) {
				statusString = "ca";
			} else if (searchKey.contains("co")) {
				statusString = "coordinator";
			} else if (searchKey.contains("su")) {
				statusString = "support";
			} else if (searchKey.contains("ad")) {
				statusString = "mail-admin";
			}
		}
		return statusString;
	}
}
