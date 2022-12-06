package in.nic.ashwini.eForms.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.db.master.entities.RegNumberService;
import in.nic.ashwini.eForms.db.master.entities.ServicesTable;
import in.nic.ashwini.eForms.db.master.repositories.AdminsRepositoryToWrite;
import in.nic.ashwini.eForms.db.master.repositories.DashboardAdminsRepositoryToWrite;
import in.nic.ashwini.eForms.db.master.repositories.MailAdminRepositoryToWrite;
import in.nic.ashwini.eForms.db.master.repositories.RegNumberServiceRepositoryToWrite;
import in.nic.ashwini.eForms.db.master.repositories.ServiceAdminRepositoryToWrite;
import in.nic.ashwini.eForms.db.master.repositories.ServicesTableRepositoryToWrite;
import in.nic.ashwini.eForms.db.master.repositories.SupportRepositoryToWrite;
import in.nic.ashwini.eForms.db.slave.entities.AdminsFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.DashboardAdminsFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.RegNumberServiceFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.ServicesTableFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.SupportFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.AdminsRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.DashboardAdminsRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.MailAdminRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.RegNumberServiceRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.ServiceAdminRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.ServicesTableRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.SupportRepositoryToRead;

@Service
public class AdminService {

	private final AdminsRepositoryToWrite adminsRepository;
	private final DashboardAdminsRepositoryToWrite dashboardAdminsRepository;
	private final SupportRepositoryToWrite supportRepository;
	private final ServiceAdminRepositoryToWrite serviceAdminRepository;
	private final ServicesTableRepositoryToWrite servicesTableRepository;
	private final RegNumberServiceRepositoryToWrite regNumberServiceRepository;
	private final UtilityService utilityService;
	private final MailAdminRepositoryToWrite mailAdminRepository;
	
	private final AdminsRepositoryToRead adminsRepositoryToRead;
	private final DashboardAdminsRepositoryToRead dashboardAdminsRepositoryToRead;
	private final SupportRepositoryToRead supportRepositoryToRead;
	private final ServiceAdminRepositoryToRead serviceAdminRepositoryToRead;
	private final ServicesTableRepositoryToRead servicesTableRepositoryToRead;
	private final RegNumberServiceRepositoryToRead regNumberServiceRepositoryToRead;
	private final MailAdminRepositoryToRead mailAdminRepositoryToRead;

	@Autowired
	public AdminService(AdminsRepositoryToWrite adminsRepository, DashboardAdminsRepositoryToWrite dashboardAdminsRepository,
			SupportRepositoryToWrite supportRepository, ServiceAdminRepositoryToWrite serviceAdminRepository,
			ServicesTableRepositoryToWrite servicesTableRepository, UtilityService utilityService,
			RegNumberServiceRepositoryToWrite regNumberServiceRepository,MailAdminRepositoryToWrite mailAdminRepository,
			AdminsRepositoryToRead adminsRepositoryToRead,
			DashboardAdminsRepositoryToRead dashboardAdminsRepositoryToRead,
			SupportRepositoryToRead supportRepositoryToRead,
			ServiceAdminRepositoryToRead serviceAdminRepositoryToRead,
			ServicesTableRepositoryToRead servicesTableRepositoryToRead,
			RegNumberServiceRepositoryToRead regNumberServiceRepositoryToRead,
			MailAdminRepositoryToRead mailAdminRepositoryToRead) {
		super();
		this.adminsRepository = adminsRepository;
		this.dashboardAdminsRepository = dashboardAdminsRepository;
		this.supportRepository = supportRepository;
		this.serviceAdminRepository = serviceAdminRepository;
		this.servicesTableRepository = servicesTableRepository;
		this.utilityService = utilityService;
		this.regNumberServiceRepository = regNumberServiceRepository;
		this.mailAdminRepository=mailAdminRepository;
		this.adminsRepositoryToRead = adminsRepositoryToRead;
		this.dashboardAdminsRepositoryToRead = dashboardAdminsRepositoryToRead;
		this.supportRepositoryToRead = supportRepositoryToRead;
		this.serviceAdminRepositoryToRead = serviceAdminRepositoryToRead;
		this.servicesTableRepositoryToRead = servicesTableRepositoryToRead;
		this.regNumberServiceRepositoryToRead = regNumberServiceRepositoryToRead;
		this.mailAdminRepositoryToRead=mailAdminRepositoryToRead;
	}

	public boolean isUserDashboardAdmin(String email) {
		Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
		for (String sMail : aliases) {
			Optional<DashboardAdminsFromSlave> dashBoardAdmin = dashboardAdminsRepositoryToRead.findFirstByEmail(sMail);
			if (dashBoardAdmin.isPresent()) {
				return true;
			}
		}
		return false;
	}

	public boolean isUserSupport(String remoteIp, String email, String mobile) {
		Optional<SupportFromSlave> support = supportRepositoryToRead.findFirstByIp(remoteIp);
		if (support.isPresent()) {
			Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
			mobile = mobile.trim();
			mobile = utilityService.transformMobile(mobile);
			for (String sMail : aliases) {
				if (utilityService.isSupportEmail(sMail)) {
					Optional<AdminsFromSlave> supportAdminTable = adminsRepositoryToRead.findByMobile(mobile);
					if (supportAdminTable.isPresent()) {
						System.out.println("first");
						return true;
					}
				} else {
					Optional<AdminsFromSlave> supportAdminTable = adminsRepositoryToRead.findByEmail(sMail);
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
				Optional<AdminsFromSlave> support = adminsRepositoryToRead.findByMobileAndVpnIp(mobile, remoteIp);
				if (support.isPresent()) {
					return true;
				}
			} else {
				Optional<AdminsFromSlave> support = adminsRepositoryToRead.findByEmailAndVpnIp(sMail, remoteIp);
				if (support.isPresent()) {
					return true;
				}
			}
		}
		return false;
	}

	public List<ServicesTableFromSlave> fetchInternalServices() {
		List<ServicesTableFromSlave> serviceTableList = servicesTableRepositoryToRead.findByServiceTypeAndStatusOrderByServiceOrder("internal", 1);
		if(serviceTableList != null) {
			return serviceTableList;
		}else {
			return new ArrayList<ServicesTableFromSlave>();
		}
	}

	public List<ServicesTableFromSlave> fetchExternalServices() {
		List<ServicesTableFromSlave> serviceTableList = servicesTableRepositoryToRead.findByServiceTypeAndStatusOrderByServiceOrder("external", 1);
		if(serviceTableList != null) {
			return serviceTableList;
		}else {
			return new ArrayList<ServicesTableFromSlave>();
		}
	}

	public Set<String> fetchAllowedForms(String email, String role) {
		Collection<Integer> finalServiceIds = new HashSet<>();
		Set<String> finalServices = new HashSet<>();

		if (role.equals("ROLE_SUPERADMIN")) {
			Set<String> services = servicesTableRepositoryToRead.findDistinctServices();
			if(services != null) {
				return services;
			}else {
				return finalServices;
			}
		}

		if (role.equals("ROLE_SUPPORT") || role.equals("ROLE_ADMIN")) {
			Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
			for (String email1 : aliases) {
				Set<Integer> serviceIds = serviceAdminRepositoryToRead.findByAdminEmail(email1);
				if(serviceIds != null)
				finalServiceIds.addAll(serviceIds);
			}

			List<ServicesTableFromSlave> services = servicesTableRepositoryToRead.findByIdIn(finalServiceIds);
			if (services != null) {
				for (ServicesTableFromSlave servicesTable : services) {
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
		Optional<RegNumberServiceFromSlave> serviceNameOptional = regNumberServiceRepositoryToRead
				.findByRegNumberFormat((registrationNo.toLowerCase().split("-form"))[0] + "-form");
		RegNumberServiceFromSlave serviceName = null;
		if (serviceNameOptional.isPresent()) {
			serviceName = serviceNameOptional.orElse(null);
			if (requestUri.contains(servicesTableRepositoryToRead.findServiceName(serviceName.getService()))) {
				return true;
			}
		}
		return false;
	}

	public String fetchServiceName(String registrationNo) {
		Optional<RegNumberServiceFromSlave> serviceNameOptional = regNumberServiceRepositoryToRead
				.findByRegNumberFormat((registrationNo.toLowerCase().split("-form"))[0] + "-form");
		RegNumberServiceFromSlave serviceName = null;
		if (serviceNameOptional.isPresent()) {
			serviceName = serviceNameOptional.orElse(null);
			return servicesTableRepositoryToRead.findServiceName(serviceName.getService());
		}
		return "";
	}
	
	public List<String> fetchMailAdmins(String field){
		String formtype="m_"+field;
		if(field.contains("single")) {
			return mailAdminRepositoryToRead.single(formtype);
		}
		else if(field.contains("bulk")) {
			return mailAdminRepositoryToRead.bulk(formtype);
		}
		else if(field.contains("nkn")) {
			return mailAdminRepositoryToRead.nkn(formtype);
		}
		
		else if(field.contains("ldap")) {
			return mailAdminRepositoryToRead.ldap(formtype);
		}
		
		else if(field.contains("dlist")) {
			return mailAdminRepositoryToRead.dlist(formtype);
		}
		else if(field.contains("sms")) {
			return mailAdminRepositoryToRead.sms(formtype);
		}
		else if(field.contains("ip")) {
			return mailAdminRepositoryToRead.ip(formtype);
		}
		
		else if(field.contains("imap")) {
			return mailAdminRepositoryToRead.imappop(formtype);
		}
		else if(field.contains("gem")) {
			return mailAdminRepositoryToRead.gem(formtype);
		}
		else if(field.contains("mobile")) {
			return mailAdminRepositoryToRead.mobile(formtype);
		}
		else if(field.contains("dns")) {
			return mailAdminRepositoryToRead.dns(formtype);
		}
		else if(field.contains("wifi")) {
			return mailAdminRepositoryToRead.wifi(formtype);
		}
		else if(field.contains("vpn")) {
			return mailAdminRepositoryToRead.vpn(formtype);
		}
		
		else if(field.contains("cloud")) {
			return mailAdminRepositoryToRead.cloud(formtype);
		}
		
		else if(field.contains("centralum")) {
			return mailAdminRepositoryToRead.centralum(formtype);
		}
		else if(field.contains("webcast")) {
			return mailAdminRepositoryToRead.webcast(formtype);
		}
		else if(field.contains("bulk")) {
			return mailAdminRepositoryToRead.bulk(formtype);
		}
		else if(field.contains("emailAct")) {
			return mailAdminRepositoryToRead.emailAct(formtype);
		}
		else if(field.contains("emailAct")) {
			return mailAdminRepositoryToRead.emailAct(formtype);
		}
		else if(field.contains("emailDeact")) {
			return mailAdminRepositoryToRead.emailDeact(formtype);
		}
		else if(field.contains("profile")) {
			return mailAdminRepositoryToRead.profile(formtype);
		}
		return null;
	}
}
