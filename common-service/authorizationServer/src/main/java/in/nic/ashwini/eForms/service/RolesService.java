package in.nic.ashwini.eForms.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.nic.ashwini.eForms.db.slave.entities.CreateUserAuthFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.DaAppAuthFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.SupportAppAuthFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.CreateUserAuthRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.DaAppAuthRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.SupportAppRepoToRead;

@Service
public class RolesService {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;

	@Autowired
	private UtilityService utilityService;

	@Autowired
	private CreateUserAuthRepositoryToRead createUserAuthRepositoryToRead;

	@Autowired
	private SupportAppRepoToRead supportAppRepoToRead;



	@Autowired
	private DaAppAuthRepositoryToRead daAppAuthRepositoryToRead;

	@Value("${status.active}")
	private String status;

	public Set<String> fetchRoles(Set<String> aliases, String email, String mobile, boolean govEmployee) {
		/*
		 * Call the LDAP API and fetch all the aliases
		 * 
		 * Check admin table, if email exists, assign admin role (before assigning this
		 * role, Please ensure remote IP is allowed) Check Support table,if email
		 * exists, assign support role (before assigning this role, Please ensure remote
		 * IP is allowed) Check coordinator table, if email exists, assign coordinator
		 * role (before assigning this role, Please ensure remote IP is allowed) Check
		 * RO table, if email exists, assign RO role Always assign User role
		 * 
		 */

		Set<String> roles = new HashSet<>();
		//String remoteIp = utilityService.fetchClientIp(request);
		String remoteIp = request.getParameter("clientIp");
		//String remoteIp = request.getRemoteAddr();
		boolean nicEmployee = utilityService.isNicEmployee(email);

		if (nicEmployee) {
			roles.add("ROLE_NIC_USER");
		}

		//		HttpServletRequest request1 = 
		//				  ((ServletRequestAttributes) RequestContextHolder.
		//				    currentRequestAttributes()).
		//				    getRequest();
		//		String ip = request1.getRemoteAddr();

		if (utilityService.isUserRoForSettingRole(email)) {
			roles.add("ROLE_RO");
		}

		if (govEmployee) {
			if (utilityService.isUserDashboardAdmin(email)) {
				roles.add("ROLE_DASHBOARD");
			}

			if (utilityService.isUserSupport(remoteIp, email, mobile)) {
				roles.add("ROLE_SUPPORT");
			}

			if (utilityService.isUserCo(remoteIp, email)) {
				roles.add("ROLE_EMAIL_CO");
				roles.add("ROLE_CO");
			}

			if (utilityService.isUserVpnCo(email)) {
				roles.add("ROLE_VPN_CO");
				roles.add("ROLE_CO");
			}

			if (utilityService.isUserAdmin(remoteIp, email, mobile)) {
				roles.add("ROLE_ADMIN");
			}

			List<CreateUserAuthFromSlave> list = createUserAuthRepositoryToRead.findByEmailInAndIpAndStatus(aliases, remoteIp, 0);

			if(list != null) {
				roles.add("ROLE_CREATE_USER_APP");
			}

			List<SupportAppAuthFromSlave> slist = supportAppRepoToRead.findByEmailInAndIpAndStatus(aliases, remoteIp, 0);
			System.out.println("inside role assign");
			if(slist != null) {
				roles.add("ROLE_SUPPORT_APP");
			}
			List<DaAppAuthFromSlave> daList = daAppAuthRepositoryToRead.findByEmailInAndIpAndStatus(aliases, remoteIp, 0);

			if(daList != null) {
				roles.add("ROLE_DA_APP");
			}

			/*
			 * if (email.equalsIgnoreCase("tiwari.ashwini@nic.in") ||
			 * email.equalsIgnoreCase("prog13.nhq-dl@nic.in") ||
			 * email.equalsIgnoreCase("meenaxi.nhq@nic.in")) { roles.add("ROLE_SUPERADMIN");
			 * }
			 */
			//			if (email.equalsIgnoreCase("tiwari.ashwini@nic.in") || email.equalsIgnoreCase("prog13.nhq-dl@nic.in")
			//			|| email.equalsIgnoreCase("meenaxi.nhq@nic.in")) {
			//		roles.add("ROLE_SUPERADMIN");
			//	}

		}

		return roles;
	}



}
