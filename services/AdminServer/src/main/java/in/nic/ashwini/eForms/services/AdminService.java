package in.nic.ashwini.eForms.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.entities.Admins;
import in.nic.ashwini.eForms.entities.DashboardAdmins;
import in.nic.ashwini.eForms.entities.RegNumberService;
import in.nic.ashwini.eForms.entities.ServicesTable;
import in.nic.ashwini.eForms.entities.Support;
import in.nic.ashwini.eForms.repositories.AdminsRepository;
import in.nic.ashwini.eForms.repositories.DashboardAdminsRepository;
import in.nic.ashwini.eForms.repositories.RegNumberServiceRepository;
import in.nic.ashwini.eForms.repositories.ServiceAdminRepository;
import in.nic.ashwini.eForms.repositories.ServicesTableRepository;
import in.nic.ashwini.eForms.repositories.SupportRepository;

@Service
public class AdminService {

	private final AdminsRepository adminsRepository;
	private final DashboardAdminsRepository dashboardAdminsRepository;
	private final SupportRepository supportRepository;
	private final ServiceAdminRepository serviceAdminRepository;
	private final ServicesTableRepository servicesTableRepository;
	private final RegNumberServiceRepository regNumberServiceRepository;
	private final UtilityService utilityService;

	@Autowired
	public AdminService(AdminsRepository adminsRepository, DashboardAdminsRepository dashboardAdminsRepository,
			SupportRepository supportRepository, ServiceAdminRepository serviceAdminRepository,
			ServicesTableRepository servicesTableRepository, UtilityService utilityService,
			RegNumberServiceRepository regNumberServiceRepository) {
		super();
		this.adminsRepository = adminsRepository;
		this.dashboardAdminsRepository = dashboardAdminsRepository;
		this.supportRepository = supportRepository;
		this.serviceAdminRepository = serviceAdminRepository;
		this.servicesTableRepository = servicesTableRepository;
		this.utilityService = utilityService;
		this.regNumberServiceRepository = regNumberServiceRepository;
	}

	public boolean isUserDashboardAdmin(String email) {
		Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
		for (String sMail : aliases) {
			Optional<DashboardAdmins> dashBoardAdmin = dashboardAdminsRepository.findFirstByEmail(sMail);
			if (dashBoardAdmin.isPresent()) {
				return true;
			}
		}
		return false;
	}

	public boolean isUserSupport(String remoteIp, String email, String mobile) {
		Optional<Support> support = supportRepository.findFirstByIp(remoteIp);
		if (support.isPresent()) {
			Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
			mobile = mobile.trim();
			mobile = utilityService.transformMobile(mobile);
			for (String sMail : aliases) {
				if (utilityService.isSupportEmail(sMail)) {
					Optional<Admins> supportAdminTable = adminsRepository.findByMobile(mobile);
					if (supportAdminTable.isPresent()) {
						System.out.println("first");
						return true;
					}
				} else {
					Optional<Admins> supportAdminTable = adminsRepository.findByEmail(sMail);
					if (supportAdminTable.isPresent()) {
						System.out.println("second");
						return true;
					}
				}
			}
		}
		System.out.println("final false");
		return false;
	}

	public boolean isUserAdmin(String remoteIp, String email, String mobile) {
		Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
		mobile = mobile.trim();
		mobile = utilityService.transformMobile(mobile);
		for (String sMail : aliases) {
			if (utilityService.isSupportEmail(sMail)) {
				Optional<Admins> support = adminsRepository.findByMobileAndVpnIp(mobile, remoteIp);
				if (support.isPresent()) {
					return true;
				}
			} else {
				Optional<Admins> support = adminsRepository.findByEmailAndVpnIp(sMail, remoteIp);
				if (support.isPresent()) {
					return true;
				}
			}
		}
		return false;
	}

	public List<ServicesTable> fetchInternalServices() {
		List<ServicesTable> serviceTableList = servicesTableRepository.findByServiceTypeAndStatusOrderByServiceOrder("internal", 1);
		if(serviceTableList != null) {
			return serviceTableList;
		}else {
			return new ArrayList<ServicesTable>();
		}
	}

	public List<ServicesTable> fetchExternalServices() {
		List<ServicesTable> serviceTableList = servicesTableRepository.findByServiceTypeAndStatusOrderByServiceOrder("external", 1);
		if(serviceTableList != null) {
			return serviceTableList;
		}else {
			return new ArrayList<ServicesTable>();
		}
	}

	public Set<String> fetchAllowedForms(String email, String role) {
		Collection<Integer> finalServiceIds = new HashSet<>();
		Set<String> finalServices = new HashSet<>();

		if (role.equals("ROLE_SUPERADMIN")) {
			Set<String> services = servicesTableRepository.findDistinctServices();
			if(services != null) {
				return services;
			}else {
				return finalServices;
			}
		}

		if (role.equals("ROLE_SUPPORT") || role.equals("ROLE_ADMIN")) {
			Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
			for (String email1 : aliases) {
				Set<Integer> serviceIds = serviceAdminRepository.findByAdminEmail(email1);
				if(serviceIds != null)
				finalServiceIds.addAll(serviceIds);
			}

			List<ServicesTable> services = servicesTableRepository.findByIdIn(finalServiceIds);
			if (services != null) {
				for (ServicesTable servicesTable : services) {
					if (servicesTable.getKeyword().equalsIgnoreCase("vpn")) {
						finalServices.add("vpn_delete");
						finalServices.add("vpn_single");
						finalServices.add("vpn_renew");
						finalServices.add("vpn_surrender");
						finalServices.add("change_add");
						finalServices.add("vpn");
					} else if (servicesTable.getKeyword().equalsIgnoreCase("email")) {
						finalServices.add("bulk");
						finalServices.add("single");
						finalServices.add("nkn_bulk");
						finalServices.add("nkn_single");
						finalServices.add("gem");
						finalServices.add("email_act");
						finalServices.add("email_deact");
						finalServices.add("email");
					} else {
						finalServices.add(servicesTable.getKeyword());
					}
				}
				return finalServices;
			}
		}
		return new HashSet<>();
	}

	public boolean isRegNumberMatchesWithApiCall(String registrationNo, String requestUri) {
		Optional<RegNumberService> serviceNameOptional = regNumberServiceRepository
				.findByRegNumberFormat((registrationNo.toLowerCase().split("-form"))[0] + "-form");
		RegNumberService serviceName = null;
		if (serviceNameOptional.isPresent()) {
			serviceName = serviceNameOptional.orElse(null);
			if (requestUri.contains(servicesTableRepository.findServiceName(serviceName.getService()))) {
				return true;
			}
		}
		return false;
	}

	public String fetchServiceName(String registrationNo) {
		Optional<RegNumberService> serviceNameOptional = regNumberServiceRepository
				.findByRegNumberFormat((registrationNo.toLowerCase().split("-form"))[0] + "-form");
		RegNumberService serviceName = null;
		if (serviceNameOptional.isPresent()) {
			serviceName = serviceNameOptional.orElse(null);
			return servicesTableRepository.findServiceName(serviceName.getService());
		}
		return "";
	}
}
