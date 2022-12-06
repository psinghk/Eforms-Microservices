package in.nic.ashwini.ldap.service;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;
import javax.validation.constraints.NotBlank;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import in.nic.ashwini.ldap.config.MyLdapProperties;
import in.nic.ashwini.ldap.data.AssociatedDomains;
import in.nic.ashwini.ldap.data.MobileAndName;
import in.nic.ashwini.ldap.data.Po;
import in.nic.ashwini.ldap.data.Po1;
import in.nic.ashwini.ldap.data.Quota;
import in.nic.ashwini.ldap.data.User;
import in.nic.ashwini.ldap.data.UserAttributes;
import in.nic.ashwini.ldap.data.UserForCreate;
import in.nic.ashwini.ldap.data.UserForCreateForAppUsers;
import in.nic.ashwini.ldap.data.UserForHodDetails;
import in.nic.ashwini.ldap.data.UserForHomePageDA;
import in.nic.ashwini.ldap.data.UserForSearch;
import in.nic.ashwini.ldap.data.UserFromZimbra;
import in.nic.ashwini.ldap.entities.AccessTokenEntity;
import in.nic.ashwini.ldap.utility.AESUtil;
import in.nic.ashwini.ldap.utility.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private MyLdapProperties ldapProperties;

	@Autowired
	private Utils utility;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AccessTokenEntity accessTokenEntity;

	@Value("${zimbra.ldap.url}")
	private String ZIMBRA_LDAP_URL;

	@Value("${zimbra.ldap-host.handshake}")
	private String HANDSHAKE_KEY;

	@Value("${zimbra.ldap.api.secretkey}")
	private String SECRET_KEY;

	// @HystrixCommand(fallbackMethod = "fallback_authenticate")
	public boolean authenticate(final String username, final String password) {
		String filter = "(&(|(uid=" + username + ")(mail=" + username + ")(mailequivalentaddress=" + username
				+ "))(&(inetuserstatus=active)(mailuserstatus=active)))";
		log.debug("Filter for authentication : {} ", filter);
		return ldapTemplate.authenticate(ldapProperties.getBaseDN(), filter, password);
	}

	private boolean fallback_authenticate(final String username, final String password) {
		log.warn("CALLING fallback_authenticate for user : {}", username);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_authenticateThroughZimbra")
	public boolean authenticateThroughZimbra(final String username, final String password) {
		log.debug("Authentication through zimbra");
		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "authenticate");

			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status)
					return true;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private boolean fallback_authenticateThroughZimbra(final String username, final String password) {
		log.warn("CALLING fallback_authenticateThroughZimbra for user : {}", username);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByUid")
	public UserForSearch findByUid(String uid) {
		List<UserForSearch> user = ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("uid").is(uid),
				UserForSearch.class);
		if (user.size() == 0) {
			log.debug("uid : {} could not be found", uid);
			return null;
		} else {
			return user.get(0);
		}
	}

	private UserForSearch fallback_findByUid(String uid) {
		log.warn("CALLING fallback_findByUid where uid = {}", uid);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByUidThroughZimbra")
	public UserForSearch findByUidThroughZimbra(String uid) {
		System.out.println("enter in the UserForSearch findByUidThroughZimbra:::"+uid);
		log.debug("Find by uid through zimbra :: {}", uid);
		Map<String, String> map = new HashMap<>();
		map.put("param", uid);
		map.put("findby", "uid");

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "find");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return null;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					JSONObject result = (JSONObject) handshakeRespJson.get("result");
					log.info("Response from Zimbra ::: {}", result);
					return convertZimbraResponseToCustomPojo(result);
					// UserForSearch userDetails = mapper.readValue(result.toString(),
					// UserForSearch.class);
				}
				return null;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return null;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private UserForSearch fallback_findByUidThroughZimbra(String uid) {
		log.warn("CALLING fallback_findByUidThroughZimbra where uid = {}", uid);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByMail")
	public UserForSearch findByMail(String mail) {
		List<UserForSearch> user = ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("mail").is(mail),
				UserForSearch.class);
		if (user.size() == 0) {
			log.debug("mail : {} could not be found", mail);
			return null;
		} else {
			return user.get(0);
		}
	}

	private UserForSearch fallback_findByMail(String mail) {
		log.warn("CALLING fallback_findByMail for user : {}", mail);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByMailThroughZimbra")
	public UserForSearch findByMailThroughZimbra(String mail) {
		log.debug("Find by mail through zimbra :: {}", mail);
		Map<String, String> map = new HashMap<>();
		map.put("param", mail);
		map.put("findby", "email");

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "find");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return null;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					JSONObject result = (JSONObject) handshakeRespJson.get("result");
					log.info("Response from Zimbra ::: {}", result);
					return convertZimbraResponseToCustomPojo(result);
					// UserForSearch userDetails = mapper.readValue(result.toString(),
					// UserForSearch.class);
				}
				return null;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return null;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private UserForSearch fallback_findByMailThroughZimbra(String mail) {
		log.warn("CALLING fallback_findByMailThroughZimbra for user : {}", mail);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByMailOrEquivalent")
	public UserForSearch findByMailOrEquivalent(String mail) {
		List<UserForSearch> user = ldapTemplate.find(
				query().base(ldapProperties.getBaseDN()).where("mail").is(mail).or("mailequivalentaddress").is(mail),
				UserForSearch.class);
		if (user.size() == 0) {
			log.debug("mail : {} could not be found", mail);
			return null;
		} else {
			return user.get(0);
		}
	}

	private UserForSearch fallback_findByMailOrEquivalent(String mail) {
		log.warn("CALLING fallback_findByMailOrEquivalent for user : {}", mail);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByMailOrEquivalentThroughZimbra")
	public UserForSearch findByMailOrEquivalentThroughZimbra(String mail) {
		log.debug("Find by mailequivalent through zimbra :: {}", mail);
		Map<String, String> map = new HashMap<>();
		map.put("param", mail);
		map.put("findby", "aliases");

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "find");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return null;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					JSONObject result = (JSONObject) handshakeRespJson.get("result");
					log.info("Response from Zimbra ::: {}", result);
					return convertZimbraResponseToCustomPojo(result);
					// UserForSearch userDetails = mapper.readValue(result.toString(),
					// UserForSearch.class);
				}
				return null;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return null;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private UserForSearch fallback_findByMailOrEquivalentThroughZimbra(String mail) {
		log.warn("CALLING fallback_findByMailOrEquivalentThroughZimbra for user : {}", mail);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_validateEmailAddress")
	public boolean validateEmailAddress(String mail) {
		List<UserForSearch> user = ldapTemplate.find(
				query().base(ldapProperties.getBaseDN()).where("mail").is(mail).or("mailequivalentaddress").is(mail),
				UserForSearch.class);
		if (user.size() == 0) {
			log.debug("mail : {} could not be found", mail);
			return false;
		} else {
			return true;
		}
	}

	private boolean fallback_validateEmailAddress(String mail) {
		log.warn("CALLING fallback_validateEmailAddress for user : {}", mail);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_validateEmailAddressThroughZimbra")
	public boolean validateEmailAddressThroughZimbra(String mail) {
		if (findByMailOrEquivalentThroughZimbra(mail) != null)
			return true;
		log.debug("mail : {} could not be found", mail);
		return false;
	}

	private boolean fallback_validateEmailAddressThroughZimbra(String mail) {
		log.warn("CALLING fallback_validateEmailAddressThroughZimbra for user : {}", mail);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByUidOrMailOrEquivalent")
	public UserForSearch findByUidOrMailOrEquivalent(String mail) {
		List<UserForSearch> user = ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("uid").is(mail)
				.or("mail").is(mail).or("mailequivalentaddress").is(mail), UserForSearch.class);
		if (user.size() == 0) {
			log.debug("mail : {} could not be found", mail);
			return null;
		} else {
			return user.get(0);
		}
	}

	private UserForSearch fallback_findByUidOrMailOrEquivalent(String mail) {
		log.warn("CALLING fallback_findByUidOrMailOrEquivalent for user : {}", mail);
		return null;
	}

	//@HystrixCommand(fallbackMethod = "fallback_findByUidOrMailOrEquivalentThroughZimbra")
	public UserForSearch findByUidOrMailOrEquivalentThroughZimbra(String mail) {
		log.debug("Find by uid or mail or mailequivalent through zimbra :: {}", mail);
		Map<String, String> map = new HashMap<>();
		map.put("param", mail);
		map.put("findby", "all");

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "find");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return null;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					JSONObject result = (JSONObject) handshakeRespJson.get("result");
					log.info("Response from Zimbra ::: {}", result);
					return convertZimbraResponseToCustomPojo(result);
				}
				return null;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return null;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private UserForSearch fallback_findByUidOrMailOrEquivalentThroughZimbra(String mail) {
		log.warn("CALLING fallback_findByUidOrMailOrEquivalentThroughZimbra for user : {}", mail);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_isEmailAvailable")
	public boolean isEmailAvailable(String mail) {
		log.info("Inside isEmailAvailable");
		List<UserForSearch> user = ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("uid").is(mail)
				.or("mail").is(mail).or("mailequivalentaddress").is(mail), UserForSearch.class);
		if (user.size() == 0) {
			log.debug("mail : {} could not be found", mail);
			return false;
		} else {
			return true;
		}
	}

	private boolean fallback_isEmailAvailable(String mail) {
		log.warn("FALLBACK METHOD CALLED : fallback_isEmailAvailable for mail :{}", mail);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_isEmailAvailableThroughZimbra")
	public boolean isEmailAvailableThroughZimbra(String mail) {
		log.info("Inside isEmailAvailable");
		if (findByMailOrEquivalentThroughZimbra(mail) != null)
			return true;
		log.debug("mail : {} could not be found", mail);
		return false;
	}

	private boolean fallback_isEmailAvailableThroughZimbra(String mail) {
		log.warn("FALLBACK METHOD CALLED : fallback_isEmailAvailableThroughZimbra for mail :{}", mail);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByMobile")
	public List<UserForSearch> findByMobile(String mobile) {
		return ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("mobile").like(mobile),
				UserForSearch.class);
	}

	private List<UserForSearch> fallback_findByMobile(String mobile) {
		log.warn("CALLING fallback_findByMobile for mobile : {}", mobile);
		return null;
	}

	// @HystrixCommand(fallbackMethod = "fallback_findByMobileThroughZimbra")
	public List<UserForSearch> findByMobileThroughZimbra(String mobile) {
		log.debug("Find by mobile through zimbra :: {}", mobile);
		List<UserForSearch> userList = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("param", mobile);
		map.put("findby", "mobile");

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "findByMobile");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return null;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					List<JSONObject> result = (List<JSONObject>) handshakeRespJson.get("result");
					log.info("Response from Zimbra ::: {}", result);
					for (JSONObject jsonObject : result) {
						UserForSearch user = new UserForSearch();
						user = convertZimbraResponseToCustomPojo(jsonObject);
						userList.add(user);
					}
					return userList;
				}
				return null;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return null;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<UserForSearch> fallback_findByMobileThroughZimbra(String mobile) {
		log.warn("CALLING fallback_findByMobileThroughZimbra for mobile : {}", mobile);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByAttribute")
	public List<UserForSearch> findByAttribute(String key, String val) {
		String ldapKey = fetchExactKey(key);
		log.debug("Attribute on which search is applied : {} ", key);
		return ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where(ldapKey).is(val), UserForSearch.class);
	}

	private List<UserForSearch> fallback_findByAttribute(String key, String val) {
		log.warn("CALLING fallback_findByAttribute where key = {} and val = {}", key, val);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_findByAttributeThroughZimbra")
	public List<UserForSearch> findByAttributeThroughZimbra(String key, String val) {
		log.debug("Attribute on which search is applied (through zimbra): {} ", key);
		List<UserForSearch> userList = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("param", val);
		map.put("findby", key);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "find");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return null;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					List<JSONObject> result = (List<JSONObject>) handshakeRespJson.get("result");
					log.info("Response from Zimbra ::: {}", result);
					for (JSONObject jsonObject : result) {
						UserForSearch user = new UserForSearch();
						user = convertZimbraResponseToCustomPojo(jsonObject);
						userList.add(user);
					}
					return userList;
				}
				return null;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return null;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<UserForSearch> fallback_findByAttributeThroughZimbra(String key, String val) {
		log.warn("CALLING fallback_findByAttributeThroughZimbra where key = {} and val = {}", key, val);
		return null;
	}

	private String fetchExactKey(String key) {
		if ((key.contains("First") && key.contains("name")) || (key.contains("common") && key.contains("name"))) {
			return "cn";
		} else if (key.contains("Last") && key.contains("name")) {
			return "sn";
		} else if (key.contains("display") && key.contains("name")) {
			return "displayName";
		} else if (key.contains("mobile") || key.contains("cellphone")) {
			return "mobile";
		} else if (key.contains("landline") || key.contains("telephone") || key.contains("phone")) {
			return "telephoneNumber";
		} else if (key.contains("employee")) {
			return "employeeNumber";
		} else {
			return key;
		}
	}

	@HystrixCommand(fallbackMethod = "fallback_findDn")
	public String findDn(String uid) {
		List<UserForSearch> user = ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("uid").is(uid)
				.or("mail").is(uid).or("mailequivalentaddress").is(uid), UserForSearch.class);
		if (user.size() == 0) {
			log.debug("DN for uid : {} could not be found", uid);
			return "";
		} else {
			return user.get(0).getDn();
		}
	}

	private String fallback_findDn(String uid) {
		log.warn("CALLING fallback_findDn for user : {}", uid);
		return "";
	}

	@HystrixCommand(fallbackMethod = "fallback_findDnThroughZimbra")
	public String findDnThroughZimbra(String uid) {
		UserForSearch user = findByUidOrMailOrEquivalentThroughZimbra(uid);
		if (user != null) {
			return user.getDn();
		}
		return "";
	}

	private String fallback_findDnThroughZimbra(String uid) {
		log.warn("CALLING fallback_findDnThroughZimbra for user : {}", uid);
		return "";
	}

	@HystrixCommand(fallbackMethod = "fallback_findAllowedDomains")
	public List<String> findAllowedDomains(String bo) {
		List<UserForSearch> user = ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("objectclass")
				.is("sunDelegatedOrganization").and("o").is(bo), UserForSearch.class);
		if (user.size() == 0) {
			log.debug("BO : {} could not be found", bo);
			return null;
		} else {
			return user.get(0).getAllowedDomains();
		}
	}

	private List<String> fallback_findAllowedDomains(String bo) {
		log.warn("CALLING fallback_findAllowedDomains for bo = {}", bo);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_findBoMembers")
	public List<UserForHomePageDA> findBoMembers(String baseDn) {
		return ldapTemplate.find(query().base(baseDn).where("uid").isPresent(), UserForHomePageDA.class);
	}

	private List<UserForHomePageDA> fallback_findBoMembers(String baseDn) {
		log.warn("CALLING fallback_findBoMembers for baseDn : {}", baseDn);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_createMailUsers")
	public void createMailUsers(UserForCreate user, String bo, String parentBo) {
		user.setDn(buildDn(user, bo, parentBo));
		ldapTemplate.create(user);
	}

	private void fallback_createMailUsers(UserForCreate user, String bo, String parentBo) {
		log.warn("CALLING fallback_createMailUsers for user: {}, bo: {}, parentBo:{}", user.getEmail(), bo, parentBo);
	}

	@HystrixCommand(fallbackMethod = "fallback_createAppUsers")
	public void createAppUsers(UserForCreateForAppUsers user, String bo, String parentBo) {
		user.setDn(buildDnForAppUsers(user, bo, parentBo));
		ldapTemplate.create(user);
	}

	private void fallback_createAppUsers(UserForCreateForAppUsers user, String bo, String parentBo) {
		log.warn("CALLING fallback_createAppUsers for appid : {}, bo: {}, parentBo:{}", user.getEmail(), bo, parentBo);
	}

	@HystrixCommand(fallbackMethod = "fallback_update")
	public void update(UserForCreate user) {
		LdapName dn = LdapNameBuilder.newInstance(findDn(user.getUsername())).build();
		user.setDn(dn);
		ldapTemplate.update(user);
	}

	private void fallback_update(UserForCreate user) {
		log.warn("CALLING fallback_update for user : {}", user.getEmail());
	}

	@HystrixCommand(fallbackMethod = "fallback_delete")
	public void delete(UserForCreate user) {
		LdapName dn = LdapNameBuilder.newInstance(findDn(user.getUsername())).build();
		user.setDn(dn);
		ldapTemplate.delete(user);
	}

	private void fallback_delete(UserForCreate user) {
		log.warn("CALLING fallback_delete for user : {}", user.getEmail());
	}

	@HystrixCommand(fallbackMethod = "fallback_inActivate")
	public void inActivate(String uid) {
		UserForSearch userRead = findByUid(uid);
		Name dn = LdapNameBuilder.newInstance(userRead.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("mailuserstatus", "inactive");
		context.setAttributeValue("inetuserstatus", "inactive");
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_inActivate(String uid) {
		log.warn("CALLING fallback_inActivate for uid :{}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_inActivateThroughZimbra")
	public boolean inActivateThroughZimbra(String uid) {
		log.debug("Inactivating user {} through zimbra", uid);
		Map<String, Object> map = new HashMap<>();
		map.put("param", uid);
		map.put("updateby", "uid");
		Map<String, String> userDetails = new HashMap<>();
		userDetails.put("mailuserstatus", "inactive");
		userDetails.put("inetuserstatus", "inactive");
		map.put("user", userDetails);
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "updateUser");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_inActivateThroughZimbra(String uid) {
		log.warn("CALLING fallback_inActivateThroughZimbra for uid :{}", uid);
		return false;
	}
	
	@HystrixCommand(fallbackMethod = "fallback_activateThroughZimbra")
	public boolean activateThroughZimbra(String uid) {
		log.debug("Inactivating user {} through zimbra", uid);
		Map<String, Object> map = new HashMap<>();
		map.put("param", uid);
		map.put("updateby", "uid");
		Map<String, String> userDetails = new HashMap<>();
		userDetails.put("mailuserstatus", "active");
		userDetails.put("inetuserstatus", "active");
		map.put("user", userDetails);
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "updateUser");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_activateThroughZimbra(String uid) {
		log.warn("CALLING fallback_activateThroughZimbra for uid : {}", uid);
		return false;
	}
	
	@HystrixCommand(fallbackMethod = "fallback_activate")
	public void activate(String uid) {
		UserForSearch userRead = findByUid(uid);
		Name dn = LdapNameBuilder.newInstance(userRead.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("mailuserstatus", "active");
		context.setAttributeValue("inetuserstatus", "active");
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_activate(String uid) {
		log.warn("CALLING fallback_activate for uid : {}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_buildDn")
	private Name buildDn(UserForCreate user, String bo, String parentBo) {
		return LdapNameBuilder.newInstance(ldapProperties.getBaseDN()).add("o", "nic.in").add("o", parentBo)
				.add("o", bo).add("ou", "People").add("uid", user.getUsername()).build();
		// LdapNameBuilder dnBuilder = "";
//		if (user.isNicEmployee()) {
//			dnBuilder = dnBuilder.add("o", "NIC Employees");
//		} else {
//			dnBuilder = dnBuilder.add("o", "NIC Support Outsourced");
//		}
//		return dnBuilder.add("ou", "People").add("uid", user.getUsername()).build();
	}

	private Name fallback_buildDn(UserForCreate user, String bo, String parentBo) {
		log.warn("CALLING fallback_findByUid for user:{}, bo:{} and parentBo:{}", user.getEmail(), bo, parentBo);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_buildDnForAppUsers")
	private Name buildDnForAppUsers(UserForCreateForAppUsers user, String bo, String parentBo) {
		return LdapNameBuilder.newInstance(ldapProperties.getBaseDN()).add("o", "nic.in").add("o", parentBo)
				.add("o", bo).add("ou", "People").add("uid", user.getUsername()).build();
	}

	private Name fallback_buildDnForAppUsers(UserForCreateForAppUsers user, String bo, String parentBo) {
		log.warn("CALLING fallback_findByUid for user:{}, bo:{} and parentBo:{}", user.getEmail(), bo, parentBo);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_updateUsingUid")
	public void updateUsingUid(UserAttributes userAttributes) {
		UserForSearch user = findByUid(userAttributes.getUsername());
		List<String> attributes = userAttributes.getAttributes();
		User dataToBeUpdated = userAttributes.getUser();
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);

		for (String attr : attributes) {
			switch (attr) {
			case "uid":
				context.setAttributeValue("uid", userAttributes.getUsername());
				break;
			case "password":
				context.setAttributeValue("userpassword", dataToBeUpdated.getPassword());
				break;
			case "firstName":
				context.setAttributeValue("givenname", dataToBeUpdated.getFirstName());
				break;
			case "middleName":
				context.setAttributeValue("nicmiddlename", dataToBeUpdated.getMiddleName());
				break;
			case "lastName":
				context.setAttributeValue("sn", dataToBeUpdated.getLastName());
				break;
			case "displayName":
				context.setAttributeValue("displayname", dataToBeUpdated.getDisplayName());
				break;
			case "cn":
				context.setAttributeValue("cn", dataToBeUpdated.getCn());
				break;
			case "email":
				context.setAttributeValue("mail", dataToBeUpdated.getEmail());
				break;
			case "mobile":
				context.setAttributeValue("mobile", dataToBeUpdated.getMobile());
				break;
			case "postingLocation":
				context.setAttributeValue("nicCity", dataToBeUpdated.getPostingLocation());
				break;
			case "telephoneNumber":
				context.setAttributeValue("telephoneNumber", dataToBeUpdated.getTelephoneNumber());
				break;
			case "officeAddress":
				context.setAttributeValue("postalAddress", dataToBeUpdated.getOfficeAddress());
				break;
			case "initials":
				context.setAttributeValue("initials", dataToBeUpdated.getInitials());
				break;
			case "homePhone":
				context.setAttributeValue("homephone", dataToBeUpdated.getHomePhone());
				break;
			case "state":
				context.setAttributeValue("st", dataToBeUpdated.getState());
				break;
			case "o":
				context.setAttributeValue("o", dataToBeUpdated.getOrganization());
				break;
			case "employeeCode":
				context.setAttributeValue("employeeNumber", dataToBeUpdated.getEmployeeCode());
				break;
			case "inetStatus":
				context.setAttributeValue("inetuserstatus", dataToBeUpdated.getUserInetStatus());
				break;
			case "mailStatus":
				context.setAttributeValue("mailuserstatus", dataToBeUpdated.getUserMailStatus());
				break;
			case "aliases":
				context.setAttributeValue("mailequivalentaddress", dataToBeUpdated.getAliases());
				break;
			case "dateOfBirth":
				context.setAttributeValue("nicDateOfBirth", dataToBeUpdated.getDateOfBirth());
				break;
			case "dateOfExpiry":
				context.setAttributeValue("nicAccountExpDate", dataToBeUpdated.getDateOfAccountExpiry());
				break;
			case "dateOfRetirement":
				context.setAttributeValue("nicDateOfRetirement", dataToBeUpdated.getDateOfAccountExpiry());
				break;
			case "remarks":
				context.setAttributeValue("inetsubscriberaccountid", userAttributes.getRemarks());
				break;
			case "nicwifi":
				context.setAttributeValue("nicwifi", dataToBeUpdated.getNicwifi());
				break;
			default:
				System.out.println("Invalid value in uid");
			}
		}
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_updateUsingUid(UserAttributes userAttributes) {
		log.warn("Calling fallback_updateUsingUid for attributes: {} ", userAttributes.getAttributes());
	}

	@HystrixCommand(fallbackMethod = "fallback_updateUsingUidThroughZimbra")
	public boolean updateUsingUidThroughZimbra(UserAttributes userAttributes) {
		UserForSearch user = findByUidThroughZimbra(userAttributes.getUsername());
		log.debug("Updating user attributes through uid (through zimbra): {} ", userAttributes.getUsername());
		List<String> attributes = userAttributes.getAttributes();
		User dataToBeUpdated = userAttributes.getUser();
		Map<String, Object> map = new HashMap<>();
		map.put("updateby", "uid");
		map.put("param", userAttributes.getUsername());
		Map<String, Object> userMap = new HashMap<>();

		for (String attr : attributes) {
			switch (attr) {
			case "uid":
				userMap.put("uid", userAttributes.getUsername());
				break;
			case "password":
				userMap.put("userpassword", dataToBeUpdated.getPassword());
				break;
			case "firstName":
				userMap.put("givenname", dataToBeUpdated.getFirstName());
				break;
			case "middleName":
				userMap.put("nicmiddlename", dataToBeUpdated.getMiddleName());
				break;
			case "lastName":
				userMap.put("sn", dataToBeUpdated.getLastName());
				break;
			case "displayName":
				userMap.put("displayname", dataToBeUpdated.getDisplayName());
				break;
			case "cn":
				userMap.put("cn", dataToBeUpdated.getCn());
				break;
			case "email":
				userMap.put("mail", dataToBeUpdated.getEmail());
				break;
			case "mobile":
				userMap.put("mobile", dataToBeUpdated.getMobile());
				break;
			case "postingLocation":
				userMap.put("nicCity", dataToBeUpdated.getPostingLocation());
				break;
			case "telephoneNumber":
				userMap.put("telephoneNumber", dataToBeUpdated.getTelephoneNumber());
				break;
			case "officeAddress":
				userMap.put("postalAddress", dataToBeUpdated.getOfficeAddress());
				break;
			case "initials":
				userMap.put("initials", dataToBeUpdated.getInitials());
				break;
			case "homePhone":
				userMap.put("homephone", dataToBeUpdated.getHomePhone());
				break;
			case "state":
				userMap.put("st", dataToBeUpdated.getState());
				break;
			case "o":
				userMap.put("o", dataToBeUpdated.getOrganization());
				break;
			case "employeeCode":
				userMap.put("employeeNumber", dataToBeUpdated.getEmployeeCode());
				break;
			case "inetStatus":
				userMap.put("inetuserstatus", dataToBeUpdated.getUserInetStatus());
				break;
			case "mailStatus":
				userMap.put("mailuserstatus", dataToBeUpdated.getUserMailStatus());
				break;
			case "aliases":
				userMap.put("mailequivalentaddress", dataToBeUpdated.getAliases());
				break;
			case "dateOfBirth":
				userMap.put("nicDateOfBirth", dataToBeUpdated.getDateOfBirth());
				break;
			case "dateOfExpiry":
				userMap.put("nicAccountExpDate", dataToBeUpdated.getDateOfAccountExpiry());
				break;
			case "dateOfRetirement":
				userMap.put("nicDateOfRetirement", dataToBeUpdated.getDateOfAccountExpiry());
				break;
			case "remarks":
				userMap.put("inetsubscriberaccountid", userAttributes.getRemarks());
				break;
			case "nicwifi":
				userMap.put("nicwifi", dataToBeUpdated.getNicwifi());
				break;
			default:
				System.out.println("Invalid value in uid");
			}
		}
		map.put("user", user);

		List<UserForSearch> userList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "updateUser");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_updateUsingUidThroughZimbra(UserAttributes userAttributes) {
		log.warn("Calling fallback_updateUsingUidThroughZimbra for attributes: {} ", userAttributes.getAttributes());
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_updateUsingMail")
	public void updateUsingMail(UserAttributes userAttributes) {
		UserForSearch user = findByMail(userAttributes.getEmail());
		List<String> attributes = userAttributes.getAttributes();
		User dataToBeUpdated = userAttributes.getUser();
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);

		for (String attr : attributes) {
			switch (attr) {
			case "uid":
				context.setAttributeValue("uid", userAttributes.getUsername());
				break;
			case "password":
				context.setAttributeValue("userpassword", dataToBeUpdated.getPassword());
				break;
			case "firstName":
				context.setAttributeValue("givenname", dataToBeUpdated.getFirstName());
				break;
			case "middleName":
				context.setAttributeValue("nicmiddlename", dataToBeUpdated.getMiddleName());
				break;
			case "lastName":
				context.setAttributeValue("sn", dataToBeUpdated.getLastName());
				break;
			case "displayName":
				context.setAttributeValue("displayname", dataToBeUpdated.getDisplayName());
				break;
			case "cn":
				context.setAttributeValue("cn", dataToBeUpdated.getCn());
				break;
			case "email":
				context.setAttributeValue("mail", dataToBeUpdated.getEmail());
				break;
			case "mobile":
				context.setAttributeValue("mobile", dataToBeUpdated.getMobile());
				break;
			case "postingLocation":
				context.setAttributeValue("nicCity", dataToBeUpdated.getPostingLocation());
				break;
			case "telephoneNumber":
				context.setAttributeValue("telephoneNumber", dataToBeUpdated.getTelephoneNumber());
				break;
			case "officeAddress":
				context.setAttributeValue("postalAddress", dataToBeUpdated.getOfficeAddress());
				break;
			case "initials":
				context.setAttributeValue("initials", dataToBeUpdated.getInitials());
				break;
			case "homePhone":
				context.setAttributeValue("homephone", dataToBeUpdated.getHomePhone());
				break;
			case "state":
				context.setAttributeValue("st", dataToBeUpdated.getState());
				break;
			case "o":
				context.setAttributeValue("o", dataToBeUpdated.getOrganization());
				break;
			case "employeeCode":
				context.setAttributeValue("employeeNumber", dataToBeUpdated.getEmployeeCode());
				break;
			case "inetStatus":
				context.setAttributeValue("inetuserstatus", dataToBeUpdated.getUserInetStatus());
				break;
			case "mailStatus":
				context.setAttributeValue("mailuserstatus", dataToBeUpdated.getUserMailStatus());
				break;
			case "aliases":
				context.setAttributeValue("mailequivalentaddress", dataToBeUpdated.getAliases());
				break;
			case "dateOfBirth":
				context.setAttributeValue("nicDateOfBirth", dataToBeUpdated.getDateOfBirth());
				break;
			case "dateOfExpiry":
				context.setAttributeValue("nicAccountExpDate", dataToBeUpdated.getDateOfAccountExpiry());
				break;
			case "dateOfRetirement":
				context.setAttributeValue("nicDateOfRetirement", dataToBeUpdated.getDateOfAccountExpiry());
				break;
			case "remarks":
				context.setAttributeValue("inetsubscriberaccountid", userAttributes.getRemarks());
				break;
			default:
				System.out.println("Invalid value in uid");
			}
		}
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_updateUsingMail(UserAttributes userAttributes) {
		log.warn("Calling fallback_updateUsingUid for attributes: {} ", userAttributes.getAttributes());
	}

	@HystrixCommand(fallbackMethod = "fallback_updateUsingMailThroughZimbra")
	public boolean updateUsingMailThroughZimbra(UserAttributes userAttributes) {
		UserForSearch user = findByMailOrEquivalentThroughZimbra(userAttributes.getEmail());
		log.debug("Updating user attributes through mail (through zimbra): {} ", userAttributes.getEmail());
		List<String> attributes = userAttributes.getAttributes();
		User dataToBeUpdated = userAttributes.getUser();
		Map<String, Object> map = new HashMap<>();
		map.put("updateby", "mail");
		map.put("param", userAttributes.getEmail());
		Map<String, Object> userMap = new HashMap<>();

		for (String attr : attributes) {
			switch (attr) {
			case "uid":
				userMap.put("uid", userAttributes.getUsername());
				break;
			case "password":
				userMap.put("userpassword", dataToBeUpdated.getPassword());
				break;
			case "firstName":
				userMap.put("givenname", dataToBeUpdated.getFirstName());
				break;
			case "middleName":
				userMap.put("nicmiddlename", dataToBeUpdated.getMiddleName());
				break;
			case "lastName":
				userMap.put("sn", dataToBeUpdated.getLastName());
				break;
			case "displayName":
				userMap.put("displayname", dataToBeUpdated.getDisplayName());
				break;
			case "cn":
				userMap.put("cn", dataToBeUpdated.getCn());
				break;
			case "email":
				userMap.put("mail", dataToBeUpdated.getEmail());
				break;
			case "mobile":
				userMap.put("mobile", dataToBeUpdated.getMobile());
				break;
			case "postingLocation":
				userMap.put("nicCity", dataToBeUpdated.getPostingLocation());
				break;
			case "telephoneNumber":
				userMap.put("telephoneNumber", dataToBeUpdated.getTelephoneNumber());
				break;
			case "officeAddress":
				userMap.put("postalAddress", dataToBeUpdated.getOfficeAddress());
				break;
			case "initials":
				userMap.put("initials", dataToBeUpdated.getInitials());
				break;
			case "homePhone":
				userMap.put("homephone", dataToBeUpdated.getHomePhone());
				break;
			case "state":
				userMap.put("st", dataToBeUpdated.getState());
				break;
			case "o":
				userMap.put("o", dataToBeUpdated.getOrganization());
				break;
			case "employeeCode":
				userMap.put("employeeNumber", dataToBeUpdated.getEmployeeCode());
				break;
			case "inetStatus":
				userMap.put("inetuserstatus", dataToBeUpdated.getUserInetStatus());
				break;
			case "mailStatus":
				userMap.put("mailuserstatus", dataToBeUpdated.getUserMailStatus());
				break;
			case "aliases":
				userMap.put("mailequivalentaddress", dataToBeUpdated.getAliases());
				break;
			case "dateOfBirth":
				userMap.put("nicDateOfBirth", dataToBeUpdated.getDateOfBirth());
				break;
			case "dateOfExpiry":
				userMap.put("nicAccountExpDate", dataToBeUpdated.getDateOfAccountExpiry());
				break;
			case "dateOfRetirement":
				userMap.put("nicDateOfRetirement", dataToBeUpdated.getDateOfAccountExpiry());
				break;
			case "remarks":
				userMap.put("inetsubscriberaccountid", userAttributes.getRemarks());
				break;
			case "nicwifi":
				userMap.put("nicwifi", dataToBeUpdated.getNicwifi());
				break;
			default:
				System.out.println("Invalid value in uid");
			}
		}
		map.put("user", user);

		List<UserForSearch> userList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "updateUser");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_updateUsingMailThroughZimbra(UserAttributes userAttributes) {
		log.warn("Calling fallback_updateUsingUidThroughZimbra for attributes: {} ", userAttributes.getAttributes());
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_fetchServicePackage")
	public List<Quota> fetchServicePackage(String bo) {
		return ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("objectclass")
				.is("sunDelegatedOrganization").and("o").is(bo), Quota.class);
	}

	private List<Quota> fallback_fetchServicePackage(String bo) {
		log.warn("CALLING fallback_fetchServicePackage for bo : {}", bo);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_fetchBosFromPo")
	public List<Po> fetchBosFromPo(String baseDn) {
		return ldapTemplate.find(query().base(baseDn).where("objectclass").is("sunDelegatedOrganization"), Po.class);
	}

	private List<Po> fallback_fetchBosFromPo(String baseDn) {
		log.warn("CALLING fallback_fetchBosFromPo for base dn: {}", baseDn);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_fetchPos")
	public List<Po1> fetchPos(String baseDn) {
		return ldapTemplate.find(query().base(baseDn).where("objectclass").is("sunmanagedprovider"), Po1.class);
	}

	private List<Po1> fallback_fetchPos(String baseDn) {
		log.warn("CALLING fallback_fetchPos for base dn: {}", baseDn);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_addAlias")
	public void addAlias(String uid, String alias) {
		UserForSearch user = findByUid(uid);
		List<String> aliases = user.getAliases();
		aliases.add(alias);
		Object[] aliasObjectArray = aliases.toArray();
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValues("mailequivalentaddress", aliasObjectArray);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_addAlias(String uid, String alias) {
		log.warn("CALLING fallback_addAlias for uid: {} and with new alias: {}", uid, alias);
	}

	@HystrixCommand(fallbackMethod = "fallback_addAliasThroughZimbra")
	public boolean addAliasThroughZimbra(String uid, String alias) {
		UserForSearch user = findByUidThroughZimbra(uid);
		String mail = user.getEmail();

		log.debug("Add aliases through zimbra: entered uid : {} and alias is {} ", uid, alias);
		List<UserForSearch> userList = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("uid", uid);
		map.put("alias", alias);
		map.put("mail", mail);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "addAlias");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_addAliasThroughZimbra(String uid, String alias) {
		log.warn("CALLING fallback_addAliasThroughZimbra for uid: {} and with new alias: {}", uid, alias);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_swapPrimaryWithAlias")
	public void swapPrimaryWithAlias(String uid, String aliasToBeSwapped) {
		UserForSearch user = findByUid(uid);
		String primaryEmail = user.getEmail();
		List<String> aliases = user.getAliases();
		aliases.remove(aliasToBeSwapped);
		aliases.add(primaryEmail);
		Object[] aliasObjectArray = aliases.toArray();
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValues("mailequivalentaddress", aliasObjectArray);
		context.setAttributeValue("mail", aliasToBeSwapped);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_swapPrimaryWithAlias(String uid, String aliasToBeSwapped) {
		log.warn("CALLING fallback_swapPrimaryWithAlias for uid : {} and alias :{}", uid, aliasToBeSwapped);
	}

	@HystrixCommand(fallbackMethod = "fallback_swapPrimaryWithAliasThroughZimbra")
	public boolean swapPrimaryWithAliasThroughZimbra(String uid, String aliasToBeSwapped) {
		UserForSearch user = findByUidThroughZimbra(uid);
		String mail = user.getEmail();

		log.debug("Add aliases through zimbra: entered uid : {} and alias is {} ", uid, aliasToBeSwapped);
		List<UserForSearch> userList = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("uid", uid);
		map.put("alias", aliasToBeSwapped);
		map.put("mail", mail);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "swapPrimaryWithAlias");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_swapPrimaryWithAliasThroughZimbra(String uid, String aliasToBeSwapped) {
		log.warn("CALLING fallback_swapPrimaryWithAliasThroughZimbra for uid : {} and alias :{}", uid,
				aliasToBeSwapped);
		return false;
	}

	public void updateMobile(String mail, String mobileToBeUpdated) {
		UserForSearch user = findByUidOrMailOrEquivalent(mail);
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("mobile", mobileToBeUpdated);
		ldapTemplate.modifyAttributes(context);
	}

	@HystrixCommand(fallbackMethod = "fallback_enableImap")
	public void enableImap(@NotBlank String uid) {
		UserForSearch user = findByUid(uid);

		String allowedServices = user.getMailallowedserviceaccess();
		List<String> services = new ArrayList<String>(Arrays.asList(allowedServices.split("\\$")));
		if (services.contains("-imaps:ALL")) {
			services.remove("-imaps:ALL");
		}
		if (!services.contains("+imaps:ALL") && !services.contains("imaps:ALL")) {
			services.add("+imaps:ALL");
		}
		String finalString = "";
		for (String string : services) {
			finalString += string + "$";
		}
		finalString = finalString.trim();
		finalString = finalString.substring(0, finalString.length() - 1);
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("mailallowedserviceaccess", finalString);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_enableImap(String uid) {
		log.warn("CALLING fallback_enableImap for uid: {}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_enableImapThroughZimbra")
	public boolean enableImapThroughZimbra(@NotBlank String uid) {
		UserForSearch user = findByUidThroughZimbra(uid);
		String mail = user.getEmail();

		log.debug("Enable Imap of uid {} through zimbra", uid);
		Map<String, String> map = new HashMap<>();
		map.put("uid", uid);
		map.put("mail", mail);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "enableImap");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_enableImapThroughZimbra(String uid) {
		log.warn("CALLING fallback_enableImapThroughZimbra for uid: {}", uid);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_disablePop")
	public void disablePop(@NotBlank String uid) {
		UserForSearch user = findByUid(uid);
		String allowedServices = user.getMailallowedserviceaccess();
		List<String> services = new ArrayList<String>(Arrays.asList(allowedServices.split("\\$")));
		if (services.contains("+pops:ALL")) {
			services.remove("+pops:ALL");
		} else {
			if (!services.contains("-pops:ALL") && services.contains("pops:ALL")) {
				services.remove("pops:ALL");
			}
		}
		if (!services.contains("-pops:ALL")) {
			services.add("-pops:ALL");
		}

		String finalString = "";
		for (String string : services) {
			finalString += string + "$";
		}
		finalString = finalString.trim();
		finalString = finalString.substring(0, finalString.length() - 1);
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("mailallowedserviceaccess", finalString);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_disablePop(String uid) {
		log.warn("CALLING fallback_disablePop for uid : {}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_disablePopThroughZimbra")
	public boolean disablePopThroughZimbra(@NotBlank String uid) {
		UserForSearch user = findByUidThroughZimbra(uid);
		String mail = user.getEmail();

		log.debug("Enable Imap of uid {} through zimbra", uid);
		Map<String, String> map = new HashMap<>();
		map.put("uid", uid);
		map.put("mail", mail);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "disablePop");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_disablePopThroughZimbra(String uid) {
		log.warn("CALLING fallback_disablePopThroughZimbra for uid : {}", uid);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_enablepop")
	public void enablePop(@NotBlank String uid) {
		UserForSearch user = findByUid(uid);
		String allowedServices = user.getMailallowedserviceaccess();
		List<String> services = new ArrayList<String>(Arrays.asList(allowedServices.split("\\$")));
		if (services.contains("-pops:ALL")) {
			services.remove("-pops:ALL");
		}
		if (!services.contains("+pops:ALL") && !services.contains("pops:ALL")) {
			services.add("+pops:ALL");
		}

		String finalString = "";
		for (String string : services) {
			finalString += string + "$";
		}
		finalString = finalString.trim();
		finalString = finalString.substring(0, finalString.length() - 1);
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("mailallowedserviceaccess", finalString);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_enablepop(String uid) {
		log.warn("CALLING fallback_enablepop for user : {}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_enablepopThroughZimbra")
	public boolean enablePopThroughZimbra(@NotBlank String uid) {
		UserForSearch user = findByUidThroughZimbra(uid);
		String mail = user.getEmail();

		log.debug("Enable Imap of uid {} through zimbra", uid);
		Map<String, String> map = new HashMap<>();
		map.put("uid", uid);
		map.put("mail", mail);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "enablePop");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_enablepopThroughZimbra(String uid) {
		log.warn("CALLING fallback_enablepopThroughZimbra for user : {}", uid);
		return false;
	}

	@HystrixCommand(fallbackMethod = "fallback_disableImap")
	public void disableImap(@NotBlank String uid) {
		UserForSearch user = findByUid(uid);
		String allowedServices = user.getMailallowedserviceaccess();
		List<String> services = new ArrayList<String>(Arrays.asList(allowedServices.split("\\$")));

		if (services.contains("+imaps:ALL")) {
			services.remove("+imaps:ALL");
		} else {
			if (!services.contains("-imaps:ALL") && services.contains("imaps:ALL")) {
				services.remove("imaps:ALL");
			}
		}
		if (!services.contains("-imaps:ALL")) {
			services.add("-imaps:ALL");
		}

		String finalString = "";
		for (String string : services) {
			finalString += string + "$";
		}
		finalString = finalString.trim();
		finalString = finalString.substring(0, finalString.length() - 1);

		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("mailallowedserviceaccess", finalString);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_disableImap(String uid) {
		log.warn("CALLING fallback_disableImap for uid: {}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_disableImapThroughZimbra")
	public boolean disableImapThroughZimbra(@NotBlank String uid) {
		UserForSearch user = findByUidThroughZimbra(uid);
		String mail = user.getEmail();

		log.debug("Enable Imap of uid {} through zimbra", uid);
		Map<String, String> map = new HashMap<>();
		map.put("uid", uid);
		map.put("mail", mail);

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "disableImap");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_disableImapThroughZimbra(String uid) {
		log.warn("CALLING fallback_disableImapThroughZimbra for uid: {}", uid);
		return false;
	}

	public String digest(String password) {
		String base64;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			System.out.println("Digets : " + digest);
			digest.update(password.getBytes());
			base64 = Base64.getEncoder().encodeToString(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return "{SHA}" + base64;
	}

	@HystrixCommand(fallbackMethod = "fallback_deletePartially")
	public void deletePartially(UserForSearch user) {
		UserForSearch userFinal = findByUid(user.getUsername());
		Name dn = LdapNameBuilder.newInstance(userFinal.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("inetuserstatus", "deleted");
		context.setAttributeValue("mailuserstatus", "deleted");
		context.setAttributeValue("inetsubscriberaccountid", user.getRemarks());
		context.setAttributeValue("userpassword", user.getPassword());
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_deletePartially(String uid) {
		log.warn("CALLING fallback_deletePartially uid: {}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_moveToRetiredBo")
	public void moveToRetiredBo(String uid) {
		UserForSearch user = findByUid(uid);
		String oldDn = user.getDn();
		String newDn = LdapNameBuilder.newInstance(ldapProperties.getBaseDN()).add("o", "nic.in")
				.add("o", "NIC Support").add("o", "retiredoficers").add("ou", "People").add("uid", uid).build()
				.toString();
		ldapTemplate.rename(oldDn, newDn);
		updateDateOfExpiry(uid);
	}

	private void fallback_moveToRetiredBo(String uid) {
		log.warn("CALLING fallback_moveToRetiredBo for uid: {}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_updateDateOfExpiry")
	public void updateDateOfExpiry(String uid) {
		UserForSearch user = findByUid(uid);
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		String finalDate = "";
		String expiryDate = user.getNicAccountExpDate();
		if (!expiryDate.isEmpty()) {
			String actualDate = utility.genericDateFormater(expiryDate);
			Calendar cal = Calendar.getInstance();
			Date date1;
			try {
				date1 = new SimpleDateFormat("yyyy-MM-dd").parse(actualDate);
				cal.setTime(date1);
				cal.add(Calendar.YEAR, 5);
				SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
				finalDate = format1.format(cal.getTime()) + "Z";
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Calendar cal = Calendar.getInstance();
			// cal.add(Calendar.DATE, 1);
			cal.add(Calendar.YEAR, 5);
			SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
			finalDate = format1.format(cal.getTime()) + "Z";
		}
		context.setAttributeValue("nicaccountexpdate", finalDate);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_updateDateOfExpiry(String uid) {
		log.warn("CALLING fallback_updateDateOfExpiry for uid:{}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_moveToContractualBo")
	public void moveToContractualBo(String uid) {
		UserForSearch user = findByUid(uid);
		String oldDn = user.getDn();
		String newDn = LdapNameBuilder.newInstance(ldapProperties.getBaseDN()).add("o", "nic.in")
				.add("o", "NIC Support").add("o", "contractemps-min.nic.in").add("ou", "People").add("uid", uid).build()
				.toString();
		ldapTemplate.rename(oldDn, newDn);
	}

	private void fallback_moveToContractualBo(String uid) {
		log.warn("CALLING fallback_moveToContractualBo for uid: {}", uid);
	}

	@HystrixCommand(fallbackMethod = "fallback_findHodDetails")
	public UserForHodDetails findHodDetails(String mail) {
		List<UserForHodDetails> user = ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("uid").is(mail)
				.or("mail").is(mail).or("mailequivalentaddress").is(mail), UserForHodDetails.class);
		if (user.size() == 0) {
			return null;
		} else {
			return user.get(0);
		}
	}

	private UserForHodDetails fallback_findHodDetails(String mail) {
		log.warn("CALLING fallback_findHodDetails for user : {}", mail);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_findHodDetailsThroughZimbra")
	public UserForHodDetails findHodDetailsThroughZimbra(String mail) {
		UserForSearch user = findByUidOrMailOrEquivalentThroughZimbra(mail);
		UserForHodDetails hod = new UserForHodDetails();
		if (user != null) {
			hod.setDesignation(user.getDesignation());
			hod.setDn(LdapNameBuilder.newInstance(user.getDn()).build());
			hod.setEmail(user.getEmail());
			hod.setFirstName(user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());
			hod.setMobile(user.getMobile());
			hod.setTelephoneNumber(user.getTelephoneNumber());
		}
		return hod;
	}

	private UserForHodDetails fallback_findHodDetailsThroughZimbra(String mail) {
		log.warn("CALLING fallback_findHodDetailsThroughZimbra for user : {}", mail);
		return null;
	}

	@HystrixCommand(fallbackMethod = "fallback_extendDateOfExpiry")
	public void extendDateOfExpiry(String uid, String dateOfExpiry) {
		UserForSearch user = findByUid(uid);
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("nicaccountexpdate", dateOfExpiry);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_extendDateOfExpiry(String uid, String dateOfExpiry) {
		log.warn("CALLING fallback_findByUid for uid:{} and date of expiry : {}", uid, dateOfExpiry);
	}

	@HystrixCommand(fallbackMethod = "fallback_updateSunAvailableServices")
	public void updateSunAvailableServices(String baseDn, String count) {
		Name dn = LdapNameBuilder.newInstance(baseDn).build();
		ModificationItem[] mods = new ModificationItem[1];
		mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sunAvailableServices", count));
		ldapTemplate.modifyAttributes(dn, mods);
	}

	private void fallback_updateSunAvailableServices(String baseDn, String count) {
		log.warn("CALLING fallback_updateSunAvailableServices for base dn : {} and new count : {}", baseDn, count);
	}

	@HystrixCommand(fallbackMethod = "fallback_resetPassword")
	public void resetPassword(String uid, String password) {
		UserForSearch user = findByUid(uid);
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("userpassword", password);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_resetPassword(String uid, String password) {
		log.warn("CALLING fallback_resetPassword for uid:{}", uid);
	}

	//@HystrixCommand(fallbackMethod = "fallback_resetPasswordThroughZimbra")
	public boolean resetPasswordThroughZimbra(String uid, String password) {
		log.debug("Resetting password of uid through zimbra :: {}", uid);
		Map<String, Object> map = new HashMap<>();
		map.put("param", uid);
		map.put("updateby", "uid");
		Map<String, String> userDetails = new HashMap<>();
		userDetails.put("userpassword", password);
		map.put("user", userDetails);
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
			String response = callZimbraApi(jsonResult, "updateUser");
			log.info("OUTCOME ::: {}", response);
			if (response.isEmpty())
				return false;

			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(response);
				boolean status = (boolean) handshakeRespJson.get("status");

				if (status) {
					return true;
				}
				return false;
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
				return false;
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean fallback_resetPasswordThroughZimbra(String uid, String password) {
		log.warn("CALLING fallback_resetPasswordThroughZimbra for uid:{}", uid);
		return false;
	}

	public MobileAndName fetchMobileAndName(@NotBlank String mail) {
		List<MobileAndName> user = ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("uid").is(mail)
				.or("mail").is(mail).or("mailequivalentaddress").is(mail), MobileAndName.class);
		if (user.size() == 0) {
			log.debug("mail : {} could not be found", mail);
			return null;
		} else {
			return user.get(0);
		}
	}

	public MobileAndName fetchMobileAndNameThroughZimbra(@NotBlank String mail) {
		UserForSearch user = findByUidOrMailOrEquivalentThroughZimbra(mail);
		MobileAndName mobileAndName = new MobileAndName();
		mobileAndName.setCn(user.getCn());
		mobileAndName.setMobile(user.getMobile());
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		mobileAndName.setDn(dn);
		return mobileAndName;
	}

	public AssociatedDomains fetchAssociatedDomains() {
		List<AssociatedDomains> associatedDomains = ldapTemplate.find(
				query().base(ldapProperties.getBaseDN()).where("associateddomain").like("*"), AssociatedDomains.class);
		if (associatedDomains.size() == 0) {
			log.debug("Aoociated Domains : {} could not be found");
			return null;
		} else {
			return associatedDomains.get(0);
		}
	}

	private String callZimbraApi(String jsonInput, String whichMethod) {
		String accessToken = accessTokenEntity.getAccessToken();
		if (accessToken == null)
			return "";
		if (accessToken.isEmpty())
			return "";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Bearer " + accessToken);
		String handShakeUrl = ZIMBRA_LDAP_URL + whichMethod;
		String encStr = AESUtil.encrypt(jsonInput.toString(), SECRET_KEY);
		HttpEntity<?> entity = new HttpEntity<>(encStr, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(handShakeUrl, entity, String.class);
		String handshakeRespStr = response.getBody();
		log.info("Response from ZIMBRA ::: {}", handshakeRespStr);
		if (handshakeRespStr != null && !handshakeRespStr.isEmpty()) {
			handshakeRespStr = handshakeRespStr.substring(1, handshakeRespStr.length() - 1);
			String handshakeDrcStr = AESUtil.decrypt(handshakeRespStr, SECRET_KEY);
			log.info("Decrypted Response ::: {}", handshakeDrcStr);
			return handshakeDrcStr;
		}
		return "";
	}

	private UserForSearch convertZimbraResponseToCustomPojo1(JSONObject jsonObject) {
		UserForSearch user = new UserForSearch();
		String dn1 = (String) jsonObject.get("dn");
		if (dn1 == null || dn1.isEmpty())
			return null;

		Name dn = LdapNameBuilder.newInstance(dn1).build();
		String username = (String) jsonObject.get("username");
		if (username == null)
			username = "";
		String firstName = (String) jsonObject.get("firstName");
		if (firstName == null)
			firstName = "";
		String middleName = (String) jsonObject.get("middleName");
		if (middleName == null)
			middleName = "";
		String lastName = (String) jsonObject.get("lastName");
		if (lastName == null)
			lastName = "";
		String designation = (String) jsonObject.get("designation");
		if (designation == null)
			designation = "";
		String cn = (String) jsonObject.get("cn");
		if (cn == null)
			cn = "";

		List<String> aliases = (List<String>) jsonObject.get("mailEquivalentAddress");
		if (aliases == null)
			aliases = new ArrayList<>();
		List<String> mailalternateaddress = (List<String>) jsonObject.get("mailalternateaddress");
		if (mailalternateaddress == null)
			aliases = new ArrayList<>();
		List<String> allowedDomains = (List<String>) jsonObject.get("allowedDomains");
		if (allowedDomains == null)
			aliases = new ArrayList<>();

		String inetUserStatus = (String) jsonObject.get("inetUserStatus");
		if (inetUserStatus == null)
			inetUserStatus = "";
		String mailhost = (String) jsonObject.get("mailhost");
		if (mailhost == null)
			mailhost = "";
		String mailallowedserviceaccess = (String) jsonObject.get("mailallowedserviceaccess");
		if (mailallowedserviceaccess == null)
			mailallowedserviceaccess = "";
		String inetsubscriberaccountid = (String) jsonObject.get("inetsubscriberaccountid");
		if (inetsubscriberaccountid == null)
			inetsubscriberaccountid = "";
		String mailUserStatus = (String) jsonObject.get("mailUserStatus");
		if (mailUserStatus == null)
			mailUserStatus = "";
		String mailmessagestore = (String) jsonObject.get("mailmessagestore");
		if (mailmessagestore == null)
			mailmessagestore = "";

		String o = (String) jsonObject.get("o");
		if (o == null)
			o = "";

		String nicDateOfRetirement = (String) jsonObject.get("nicDateOfRetirement");
		if (nicDateOfRetirement == null)
			nicDateOfRetirement = "";
		String nicDateOfBirth = (String) jsonObject.get("nicDateOfBirth");
		if (nicDateOfBirth == null)
			nicDateOfBirth = "";
		String nicAccountExpDate = (String) jsonObject.get("nicAccountExpDate");
		if (nicAccountExpDate == null)
			nicAccountExpDate = "";
		String officeAddress = (String) jsonObject.get("officeAddress");
		if (officeAddress == null)
			officeAddress = "";

		String mobile = (String) jsonObject.get("mobile");
		if (mobile == null)
			mobile = "";
		String zimotp = (String) jsonObject.get("zimotp");
		if (zimotp == null)
			zimotp = "";
		String homePhone = (String) jsonObject.get("homePhone");
		if (homePhone == null)
			homePhone = "";
		String initials = (String) jsonObject.get("initials");
		if (initials == null)
			initials = "";

		String davuniqueid = (String) jsonObject.get("davuniqueid");
		if (davuniqueid == null)
			davuniqueid = "";

		String nicnewuser = (String) jsonObject.get("nicnewuser");
		if (nicnewuser == null)
			nicnewuser = "";
		String state = (String) jsonObject.get("state");
		if (state == null)
			state = "";
		String createtimestamp = (String) jsonObject.get("createtimestamp");
		if (createtimestamp == null)
			createtimestamp = "";
		String nicwifi = (String) jsonObject.get("nicwifi");
		if (nicwifi == null)
			nicwifi = "";
		String nicLastLoginDetail = (String) jsonObject.get("nicLastLoginDetail");
		if (nicLastLoginDetail == null)
			nicLastLoginDetail = "";
		String employeeCode = (String) jsonObject.get("employeeCode");
		if (employeeCode == null)
			employeeCode = "";
		String description = (String) jsonObject.get("description");
		if (description == null)
			description = "";
		String nsroledn = (String) jsonObject.get("nsroledn");
		if (nsroledn == null)
			nsroledn = "";
		String telephoneNumber = (String) jsonObject.get("telephoneNumber");
		if (telephoneNumber == null)
			telephoneNumber = "";
		String mail = (String) jsonObject.get("mail");
		if (mail == null)
			mail = "";

		String postingLocation = (String) jsonObject.get("postingLocation");
		if (postingLocation == null)
			postingLocation = "";
		String displayName = (String) jsonObject.get("displayName");
		if (displayName == null)
			displayName = "";
		String remarks = (String) jsonObject.get("remarks");
		if (remarks == null)
			remarks = "";
		String associateddomain = (String) jsonObject.get("associateddomain");
		if (associateddomain == null)
			associateddomain = "";
		user.setDn(dn);
		user.setAliases(aliases);
		user.setAllowedDomains(allowedDomains);
		user.setAssociateddomain(associateddomain);
		user.setCn(cn);
		user.setCreatetimestamp(createtimestamp);
		user.setDavuniqueid(davuniqueid);
		user.setDescription(description);
		user.setDesignation(designation);
		user.setDisplayName(displayName);
		user.setEmail(mail);
		user.setEmployeeCode(employeeCode);
		user.setFirstName(firstName);
		user.setHomePhone(homePhone);
		user.setInetsubscriberaccountid(inetsubscriberaccountid);
		user.setInitials(initials);
		user.setLastName(lastName);
		user.setMailallowedserviceaccess(mailallowedserviceaccess);
		user.setMailalternateaddress(mailalternateaddress);
		user.setMailhost(mailhost);
		user.setMailmessagestore(mailmessagestore);
		user.setMiddleName(middleName);
		user.setMobile(mobile);
		user.setNicAccountExpDate(nicAccountExpDate);
		user.setNicDateOfBirth(nicDateOfBirth);
		user.setNicLastLoginDetail(nicLastLoginDetail);
		user.setNicnewuser(nicnewuser);
		user.setNicwifi(nicwifi);
		user.setNsroledn(nsroledn);
		user.setO(o);
		user.setOfficeAddress(officeAddress);
		user.setPassword("N/A");
		user.setPostingLocation(postingLocation);
		user.setRemarks(remarks);
		user.setState(state);
		user.setTelephoneNumber(telephoneNumber);
		user.setUserInetStatus(inetUserStatus);
		user.setUserMailStatus(mailUserStatus);
		user.setUsername(username);
		user.setZimotp(zimotp);
		return user;
	}

	private UserForSearch convertZimbraResponseToCustomPojo(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		UserForSearch user = null;
		UserFromZimbra zimbraUser;
		try {
			zimbraUser = mapper.readValue(jsonObject.toString(), UserFromZimbra.class);
			ModelMapper modelMapper = new ModelMapper();
			user = modelMapper.map(zimbraUser, UserForSearch.class);
			LdapName dn = LdapNameBuilder.newInstance(zimbraUser.getDn()).build();
			user.setDn(dn);
			user.setPassword("N/A");
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return user;
	}
}
