package in.nic.ashwini.eForms.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.db.master.entities.FinalAuditTrack;
import in.nic.ashwini.eForms.db.master.entities.SearchBean;
import in.nic.ashwini.eForms.db.master.repositories.FinalAuditTrackRepository;
import in.nic.ashwini.eForms.db.slave.entities.FinalAuditTrackFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.FinalAuditTrackRepositoryToRead;
import in.nic.ashwini.eForms.services.Constants;
import in.nic.ashwini.eForms.services.DashBoardService;
import in.nic.ashwini.eForms.services.UtilityService;

@RestController
@RequestMapping("/admin/requests/")
public class AdminDashboardController {
	private final FinalAuditTrackRepository finalAuditTrackRepository;
	private final FinalAuditTrackRepositoryToRead finalAuditTrackRepositoryToRead;
	private final UtilityService utilityService;
	private final DashBoardService dashBoardService;
	
	@Autowired
	public AdminDashboardController(FinalAuditTrackRepository finalAuditTrackRepository, UtilityService utilityService, DashBoardService dashBoardService, FinalAuditTrackRepositoryToRead finalAuditTrackRepositoryToRead) {
		super();
		this.finalAuditTrackRepository = finalAuditTrackRepository;
		this.utilityService = utilityService;
		this.dashBoardService = dashBoardService;
		this.finalAuditTrackRepositoryToRead = finalAuditTrackRepositoryToRead;
	}

	@PostMapping("pending")
	public Page<FinalAuditTrackFromSlave> pending(@RequestParam("email") @NotEmpty String email, @RequestParam("allowedForms") List<String> allowedForms, SearchBean searchBean, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("mail-admin_pending");
		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByStatusInAndFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							status_list, allowedForms, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByStatusInAndFormNameIn(status_list, allowedForms, sortedByIdDesc);
	}
	
	@PostMapping("completed")
	public Page<FinalAuditTrackFromSlave> completed(@RequestParam("email") @NotEmpty String email,@RequestParam("allowedForms") List<String> allowedForms, Pageable pageable, SearchBean searchBean) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("completed");
		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByStatusInAndFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							status_list, allowedForms, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByStatusInAndFormNameIn(status_list, allowedForms, sortedByIdDesc);
	}
	
	@PostMapping("rejected")
	public Page<FinalAuditTrackFromSlave> rejected(@RequestParam("email") @NotEmpty String email, @RequestParam("allowedForms") List<String> allowedForms,Pageable pageable, SearchBean searchBean) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("mail-admin_rejected");
		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByStatusInAndFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							status_list, allowedForms, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByStatusInAndFormNameIn(status_list, allowedForms, sortedByIdDesc);
	}
	
	@PostMapping("forwarded")
	public Page<FinalAuditTrackFromSlave> forwarded(@RequestParam("email") @NotEmpty String email, @RequestParam("allowedForms") List<String> allowedForms, Pageable pageable, SearchBean searchBean) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = Arrays.asList("completed");
		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByStatusInAndFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							status_list, allowedForms, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByStatusInAndFormNameIn(status_list, allowedForms, sortedByIdDesc);
	}
	
	@PostMapping("all")
	public Page<FinalAuditTrackFromSlave> all(@RequestParam("email") @NotEmpty String email, @RequestParam("allowedForms") List<String> allowedForms,Pageable pageable, SearchBean searchBean) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		if (searchBean.getSearchKey() != null && !searchBean.getSearchKey().isEmpty()) {
			String searchKey = searchBean.getSearchKey();
			String statusString = fetchSearchString(searchKey);

			if (statusString.isEmpty())
				statusString = searchKey;
			return finalAuditTrackRepositoryToRead
					.findByFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
							allowedForms, searchKey, searchKey, searchKey, searchKey, statusString, searchKey,
							searchKey, sortedByIdDesc);
		}
		return finalAuditTrackRepositoryToRead.findByFormNameIn(allowedForms, sortedByIdDesc);
	}
	
	@GetMapping("fetchDashBoardCounts")
	public Map<String,Integer> fetchDashBoardCounts(@RequestParam("email") @NotEmpty String email, @RequestParam("allowedForms") List<String> allowedForms){
		Map<String,Integer> map = new HashMap<>();
		map.put("totalPending", countPending(email, allowedForms));
		map.put("totalCompleted", countCompleted(email, allowedForms));
		map.put("totalRejeted", countRejected(email, allowedForms));
		map.put("totalForwarded", countForwarded(email, allowedForms));
		map.put("totalAll", countAll(email, allowedForms));
		return map;
	}
	
	@GetMapping("count/pending")
	public int countPending(@RequestParam("email") @NotEmpty String email, @RequestParam("allowedForms") List<String> allowedForms) {
		List<String> status_list = Arrays.asList("mail-admin_pending");
		return finalAuditTrackRepositoryToRead.countByStatusInAndFormNameIn(status_list, allowedForms);
	}
	
	@GetMapping("count/completed")
	public int countCompleted(@RequestParam("email") @NotEmpty String email, @RequestParam("allowedForms") List<String> allowedForms) {
		List<String> status_list = Arrays.asList("completed");
		return finalAuditTrackRepositoryToRead.countByStatusInAndFormNameIn(status_list, allowedForms);
	}
	
	@GetMapping("count/rejected")
	public int countRejected(@RequestParam("email") @NotEmpty String email,@RequestParam("allowedForms") List<String> allowedForms) {
		List<String> status_list = Arrays.asList("mail-admin_rejected");
		return finalAuditTrackRepositoryToRead.countByStatusInAndFormNameIn(status_list, allowedForms);
	}
	
	@GetMapping("count/forwarded")
	public int countForwarded(@RequestParam("email") @NotEmpty String email,@RequestParam("allowedForms") List<String> allowedForms) {
		List<String> status_list = Arrays.asList("completed");
		return finalAuditTrackRepositoryToRead.countByStatusInAndFormNameIn(status_list, allowedForms);
	}
	
	@GetMapping("count/all")
	public int countAll(@RequestParam("email") @NotEmpty String email,@RequestParam("allowedForms") List<String> allowedForms) {
		return finalAuditTrackRepositoryToRead.countByFormNameIn(allowedForms);
	}
	
	@GetMapping("fetchFilter")
	public Map<String,Set<String>> fetchFilter(@RequestParam("email") @NotEmpty String email,@RequestParam("allowedForms") List<String> allowedForms){
		Map<String,Set<String>> map = new HashMap<>();
		Set<String> hSet = new HashSet<String>(allowedForms); 
		Set<String> services = dashBoardService.convertFormNamesForFilters(hSet);
		Set<String> requestTypes = dashBoardService.setRequestTypes(finalAuditTrackRepositoryToRead.findDistinctStatus());
		
		map.put("services", services);
		map.put("requestTypes", requestTypes);
		return map;
	}
	
//	@PostMapping("applyFilter")
//	private Page<FinalAuditTrack> filteredRequests(@RequestParam("email") @NotEmpty String email, @ModelAttribute SearchBean searchBean, @RequestParam("allowedForms") List<String> allowedForms,Pageable pageable) {
//		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
//				Sort.by("toDatetime").descending());
//		List<String> status_list = null;
//		List<String> service_list = null;
//		if(searchBean.getServices() == null && searchBean.getStatus() == null) {
//			return pending(email,allowedForms, pageable);
//		} else if(searchBean.getServices() != null && searchBean.getServices().size() == 0 && searchBean.getStatus() != null && searchBean.getStatus().size() == 0) {
//			return pending(email,allowedForms, pageable);
//		} else if(searchBean.getServices() != null && searchBean.getServices().size() > 0 && (searchBean.getStatus() == null || (searchBean.getStatus() != null && searchBean.getStatus().size() == 0))){
//			service_list = utilityService.fetchListOfServices(searchBean.getServices());
//			return finalAuditTrackRepository.findByFormNameIn(service_list, sortedByIdDesc);
//		} else if(searchBean.getStatus() != null && searchBean.getStatus().size() > 0 && (searchBean.getServices() == null || (searchBean.getServices() != null && searchBean.getServices().size() == 0))) {
//			status_list = utilityService.fetchListOfStatus(searchBean.getStatus(),"admnin");
//			return finalAuditTrackRepository.findByStatusInAndFormNameIn(status_list, allowedForms,sortedByIdDesc);
//		} else {
//			service_list = utilityService.fetchListOfServices(searchBean.getServices());
//			status_list = utilityService.fetchListOfStatus(searchBean.getStatus(),"admnin");
//			return finalAuditTrackRepository.findByStatusInAndFormNameIn(status_list, service_list,sortedByIdDesc);
//		}
//	}
	
	@PostMapping("applyFilter")
	private Page<FinalAuditTrackFromSlave> filteredRequests(@RequestParam("email") @NotEmpty String email, @RequestParam("allowedForms") List<String> allowedForms,
			@ModelAttribute SearchBean searchBean, String searchKey, Pageable pageable) {
		Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("toDatetime").descending());
		List<String> status_list = null;
		List<String> service_list = null;
		String statusString = "";
		if (searchBean.getServices() == null && searchBean.getStatus() == null) {
			if (searchKey == null || searchKey.isEmpty())
				return pending(email, allowedForms, searchBean, pageable);
			else {
				status_list = Arrays.asList("mail-admin_pending");

				statusString = fetchSearchString(searchKey);

				if (statusString.isEmpty()) {
					statusString = searchKey;
				}

				return finalAuditTrackRepositoryToRead
						.findByStatusInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								status_list, searchKey, searchKey, searchKey, searchKey, statusString,
								searchKey, searchKey, sortedByIdDesc);
			}
		} else if (searchBean.getServices() != null && searchBean.getServices().size() == 0
				&& searchBean.getStatus() != null && searchBean.getStatus().size() == 0) {
			if (searchKey == null || searchKey.isEmpty())
				return pending(email, allowedForms, searchBean, pageable);
			else {
				status_list = Arrays.asList("mail-admin_pending");

				statusString = fetchSearchString(searchKey);

				if (statusString.isEmpty()) {
					statusString = searchKey;
				}

				return finalAuditTrackRepositoryToRead
						.findByStatusInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								status_list, searchKey, searchKey, searchKey, searchKey, statusString,
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
						.findByFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								service_list, searchKey, searchKey, searchKey, searchKey, searchKey,
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
						.findByStatusInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								status_list, searchKey, searchKey, searchKey, searchKey, statusString,
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
						.findByStatusInAndFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
								status_list, service_list, searchKey, searchKey, searchKey, searchKey,
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
