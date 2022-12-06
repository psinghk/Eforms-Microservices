package in.nic.ashwini.eForms.security.tokenenhancer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.client.RestTemplate;

import in.nic.ashwini.eForms.config.ConfigProperties;
import in.nic.ashwini.eForms.db.master.entities.LoginTrail;
import in.nic.ashwini.eForms.db.master.repositories.LoginTrailRepository;
import in.nic.ashwini.eForms.db.slave.entities.LoginDetailFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.LoginDetailsRepositoryToRead;
import in.nic.ashwini.eForms.security.userdetailservice.SecurityUser;
import in.nic.ashwini.eForms.service.EncryptionService;

public class CustomTokenConverter extends JwtAccessTokenConverter {

	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;
	
	@Autowired
	private LoginTrailRepository loginTrailRepository;
	@Autowired
	private LoginDetailsRepositoryToRead loginDetailsRepositoryToRead;
	private ConfigProperties configProperties;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		final Map<String, Object> additionalInfo = new HashMap<>();
		Collection<GrantedAuthority> gaList = authentication.getAuthorities();
		SecurityUser userDetails = null;
		try {
			userDetails = (SecurityUser) authentication.getPrincipal();
		} catch (ClassCastException e) {
			if(!gaList.contains(new SimpleGrantedAuthority("ROLE_PASSAPP_FULLY_AUTHENTICATED"))) {
				return super.enhance(accessToken, authentication);
			}
		}
		LoginTrail loginTrail = new LoginTrail();

		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FULLY_AUTHENTICATED")) || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PASSAPP_FULLY_AUTHENTICATED"))) {
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_LOGIN_THROUGH_PARICHAY"))) {
				String userNameForSSO = EncryptionService.decrypt(userDetails.getUserDetailsForSSO());
				String[] userNameForSSOArray = userNameForSSO.split(";");
				String email = userNameForSSOArray[0];
				additionalInfo.put("email", email);
				additionalInfo.put("mobile", userNameForSSOArray[1]);
				String localTokenID = userNameForSSOArray[2].split("=>")[1];
				String username = userNameForSSOArray[3].split("=>")[1];
				String service = userNameForSSOArray[4].split("=>")[1];
				String browserId = userNameForSSOArray[5].split("=>")[1];
				String sessionId = userNameForSSOArray[6].split("=>")[1];
				String ip = userNameForSSOArray[7].split("=>")[1];
				additionalInfo.put("localTokenId", localTokenID);
				additionalInfo.put("username", username);
				additionalInfo.put("service", service);
				additionalInfo.put("browserId", browserId);
				additionalInfo.put("sessionId", sessionId);
				additionalInfo.put("ip", ip);
				
				Set<String> allowedForms = fetchAllowedForms(email, gaList);
				String allowedFormsInString = String.join(":", allowedForms);
				additionalInfo.put("allowedForms", EncryptionService.encrypt(allowedFormsInString));
				loginTrail.setEmail(username);
				loginTrail.setIp(ip);
				loginTrail.setToken(sessionId);
				loginTrail.setLoginRemarks("Login with Parichay");
				loginTrail.setServiceName("coordinator portal");
				String roles = "";
				Collection<GrantedAuthority> authorities = authentication.getAuthorities();
				for (GrantedAuthority grantedAuthority : authorities) {
					roles += grantedAuthority.getAuthority()+",";
				}
				roles = roles.replaceAll(",$", "");
				roles = roles.trim();
				loginTrail.setRole(roles);
				loginTrail.setMobile(userNameForSSOArray[1]);
				loginTrailRepository.save(loginTrail);
			} else if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PASSAPP_FULLY_AUTHENTICATED"))){
				Set<String> allowedForms = new HashSet<>();
				String allowedFormsInString = "";
				additionalInfo.put("allowedForms", EncryptionService.encrypt(allowedFormsInString));
				LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("GMT+05:30"));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyhhmmss");
				String formattedDateTime = formatter.format(localDateTime);
				String uniqueId = String.format("%s-%s", RandomStringUtils.randomAlphanumeric(14),
						UUID.randomUUID().toString().replace("-", ""));
				String finalUniqueId = formattedDateTime+"-"+uniqueId;
				additionalInfo.put("sessionId", finalUniqueId);
				String usernameAndIp = authentication.getName();
				String[] userNameIpArray = usernameAndIp.split(";");
				additionalInfo.put("username", userNameIpArray[0]);
				additionalInfo.put("service", "passApp");
				additionalInfo.put("ip", userNameIpArray[1]);
			} else {
				Set<String> allowedForms = fetchAllowedForms(userDetails.getUsername(), gaList);
				String allowedFormsInString = String.join(":", allowedForms);
				additionalInfo.put("allowedForms", EncryptionService.encrypt(allowedFormsInString));
				LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("GMT+05:30"));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyhhmmss");
				String formattedDateTime = formatter.format(localDateTime);
				String uniqueId = String.format("%s-%s", RandomStringUtils.randomAlphanumeric(14),
						UUID.randomUUID().toString().replace("-", ""));
				String finalUniqueId = formattedDateTime+"-"+uniqueId;
				additionalInfo.put("sessionId", finalUniqueId);
				
				String userNameForSSO = EncryptionService.decrypt(userDetails.getUserDetailsForSSO());
				String[] userNameForSSOArray = userNameForSSO.split(";");
				String email = userNameForSSOArray[0];
				additionalInfo.put("email", email);
				additionalInfo.put("mobile", userNameForSSOArray[1]);
				String username = userNameForSSOArray[2].split("=>")[1];
				String service = userNameForSSOArray[3].split("=>")[1];
				String ip = userNameForSSOArray[4].split("=>")[1];
				additionalInfo.put("username", username);
				additionalInfo.put("service", service);
				additionalInfo.put("ip", ip);
				
				//additionalInfo.put("username", userDetails.getUsername());
//				
//				LoginDetailFromSlave loginDetailFromSlave = loginDetailsRepositoryToRead.findFirstByEmailAndStatusOrderByIdDesc(userDetails.getUsername(), 1);
//				additionalInfo.put("service", loginDetailFromSlave.getRole());
//				additionalInfo.put("ip", loginDetailFromSlave.getIp());
			}
		}
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		return super.enhance(accessToken, authentication);
	}

//	@Override
//	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
//		final OAuth2Authentication authentication =    super.extractAuthentication(map);
//	    final Map<String, Object> details = new HashMap<>();
//	    details.put("allowedForms", map.get("allowedForms"));
//	    authentication.setDetails(details);
//	    return authentication;
//	}

	private Set<String> fetchAllowedForms(String email, Collection<GrantedAuthority> authorities) {
		String uri = "";
		if(authorities.contains(new SimpleGrantedAuthority("ROLE_SUPERADMIN"))) {
			uri = configProperties.getAdminUrl() + "/fetchAllowedForms?email=" + email +"&role=" + "ROLE_SUPERADMIN";
			return restTemplate.getForObject(uri, Set.class);
		}else if(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			uri = configProperties.getAdminUrl() + "/fetchAllowedForms?email=" + email +"&role=" + "ROLE_ADMIN";
			return restTemplate.getForObject(uri, Set.class);
		} else if(authorities.contains(new SimpleGrantedAuthority("ROLE_SUPPORT"))) {
			uri = configProperties.getAdminUrl() + "/fetchAllowedForms?email=" + email +"&role=" + "ROLE_SUPPORT";
			return restTemplate.getForObject(uri, Set.class);
		} else {
			return new HashSet<>();
		}
	}
}
