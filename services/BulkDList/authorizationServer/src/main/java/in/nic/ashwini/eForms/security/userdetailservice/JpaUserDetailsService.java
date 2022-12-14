package in.nic.ashwini.eForms.security.userdetailservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.nic.ashwini.eForms.service.EncryptionService;
import in.nic.ashwini.eForms.service.RolesService;
import in.nic.ashwini.eForms.service.UtilityService;

@Service
public class JpaUserDetailsService implements UserDetailsService {

	@Autowired
	private RolesService rolesService;

	@Autowired
	private UtilityService utilityService;

	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String username1 = EncryptionService.decrypt(username);
		String[] usernameAndMobile = username1.split(":");
		username = usernameAndMobile[0];
		String mobile = usernameAndMobile[1];
		if (mobile.isEmpty()) {
			throw new BadCredentialsException("Invalid mobile number!!!");
		}

		boolean govEmployee = utilityService.isGovEmployee(username);
		Set<String> aliases = utilityService.fetchAliasesFromLdap(username);

		List<GrantedAuthority> gaList = new ArrayList<>();
		gaList.add(new SimpleGrantedAuthority("ROLE_FULLY_AUTHENTICATED"));
		gaList.add(new SimpleGrantedAuthority("ROLE_USER"));
		boolean isUserRegistered = utilityService.isUserRegistered(username);
		if (isUserRegistered) {
			gaList.add(new SimpleGrantedAuthority("ROLE_OLD_USER"));
		}else {
			gaList.add(new SimpleGrantedAuthority("ROLE_NEW_USER"));
		}

		if (govEmployee) {
			gaList.add(new SimpleGrantedAuthority("ROLE_GOV_USER"));
		}

		if (!utilityService.isSupportEmail(username) && govEmployee) {
			mobile = utilityService.fetchMobile(username);
			if (mobile.contains(",")) {
				String[] arrMobile = mobile.split(",");
				mobile = arrMobile[0];
			}
			mobile = utilityService.transformMobile(mobile);
		} else {
//			if (isUserRegistered) {
//				mobile = utilityService.fetchMobileFromProfile(username);
//				mobile = utilityService.transformMobile(mobile);
//			} else {
				mobile = utilityService.transformMobile(mobile);
				//}
	
		}

		Set<String> roles = rolesService.fetchRoles(aliases, username, mobile, govEmployee);
		for (String role : roles) {
			gaList.add(new SimpleGrantedAuthority(role));
		}

		return new SecurityUser(username, gaList);
	}
}
