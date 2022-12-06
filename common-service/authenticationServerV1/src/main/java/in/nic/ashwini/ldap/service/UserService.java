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
import java.util.Date;
import java.util.List;

import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

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

	// @HystrixCommand(fallbackMethod = "fallback_findByUid")
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

	// @HystrixCommand(fallbackMethod = "fallback_findByMail")
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

	// @HystrixCommand(fallbackMethod = "fallback_findByMailOrEquivalent")
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

	// @HystrixCommand(fallbackMethod = "fallback_validateEmailAddress")
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

	// @HystrixCommand(fallbackMethod = "fallback_findByUidOrMailOrEquivalent")
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

	// @HystrixCommand(fallbackMethod = "fallback_fetchUidByUidOrMailOrEquivalent")
	public List<String> fetchUidByUidOrMailOrEquivalent(String baseDn, String mail) {
		List<UserForSearch> user = ldapTemplate.find(query().base(baseDn).where("uid").like(mail).or("mail").like(mail)
				.or("mailequivalentaddress").like(mail), UserForSearch.class);
		if (user.size() == 0) {
			log.debug("mail : {} could not be found", mail);
			return new ArrayList<>();
		} else {
			List<String> uids = new ArrayList<>();
			for (UserForSearch userForSearch : user) {
				uids.add(userForSearch.getUsername());
			}
			return uids;
		}
	}

	private List<String> fallback_fetchUidByUidOrMailOrEquivalent(String mail) {
		log.warn("CALLING fallback_fetchUidByUidOrMailOrEquivalent for user : {}", mail);
		return null;
	}

	// @HystrixCommand(fallbackMethod = "fallback_isEmailAvailable")
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

	// @HystrixCommand(fallbackMethod = "fallback_findByMobile")
	public List<UserForSearch> findByMobile(String mobile) {
		return ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("mobile").like(mobile),
				UserForSearch.class);
	}

	private List<UserForSearch> fallback_findByMobile(String mobile) {
		log.warn("CALLING fallback_findByMobile for mobile : {}", mobile);
		return null;
	}

	// @HystrixCommand(fallbackMethod = "fallback_findByAttribute")
	public List<UserForSearch> findByAttribute(String key, String val) {
		String ldapKey = fetchExactKey(key);
		log.debug("Attribute on which search is applied : {} ", key);
		return ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where(ldapKey).is(val), UserForSearch.class);
	}

	private List<UserForSearch> fallback_findByAttribute(String key, String val) {
		log.warn("CALLING fallback_findByAttribute where key = {} and val = {}", key, val);
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

	// @HystrixCommand(fallbackMethod = "fallback_findDn")
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

	// @HystrixCommand(fallbackMethod = "fallback_findAllowedDomains")
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

	// @HystrixCommand(fallbackMethod = "fallback_findBoMembers")
	public List<UserForHomePageDA> findBoMembers(String baseDn) {
		return ldapTemplate.find(query().base(baseDn).where("uid").isPresent(), UserForHomePageDA.class);
	}

	private List<UserForHomePageDA> fallback_findBoMembers(String baseDn) {
		log.warn("CALLING fallback_findBoMembers for baseDn : {}", baseDn);
		return null;
	}

	// @HystrixCommand(fallbackMethod = "fallback_createMailUsers")
	public void createMailUsers(UserForCreate user, String bo, String parentBo) {
		user.setDn(buildDn(user, bo, parentBo));
		ldapTemplate.create(user);
	}

	private void fallback_createMailUsers(UserForCreate user, String bo, String parentBo) {
		log.warn("CALLING fallback_createMailUsers for user: {}, bo: {}, parentBo:{}", user.getEmail(), bo, parentBo);
	}

	// @HystrixCommand(fallbackMethod = "fallback_createAppUsers")
	public void createAppUsers(UserForCreateForAppUsers user, String bo, String parentBo) {
		user.setDn(buildDnForAppUsers(user, bo, parentBo));
		ldapTemplate.create(user);
	}

	private void fallback_createAppUsers(UserForCreateForAppUsers user, String bo, String parentBo) {
		log.warn("CALLING fallback_createAppUsers for appid : {}, bo: {}, parentBo:{}", user.getEmail(), bo, parentBo);
	}

	// @HystrixCommand(fallbackMethod = "fallback_update")
	public void update(UserForCreate user) {
		LdapName dn = LdapNameBuilder.newInstance(findDn(user.getUsername())).build();
		user.setDn(dn);
		ldapTemplate.update(user);
	}

	private void fallback_update(UserForCreate user) {
		log.warn("CALLING fallback_update for user : {}", user.getEmail());
	}

	// @HystrixCommand(fallbackMethod = "fallback_delete")
	public void delete(UserForCreate user) {
		LdapName dn = LdapNameBuilder.newInstance(findDn(user.getUsername())).build();
		user.setDn(dn);
		ldapTemplate.delete(user);
	}

	private void fallback_delete(UserForCreate user) {
		log.warn("CALLING fallback_delete for user : {}", user.getEmail());
	}

	// @HystrixCommand(fallbackMethod = "fallback_inActivate")
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

	// @HystrixCommand(fallbackMethod = "fallback_activate")
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

	// @HystrixCommand(fallbackMethod = "fallback_buildDn")
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

	// @HystrixCommand(fallbackMethod = "fallback_buildDnForAppUsers")
	private Name buildDnForAppUsers(UserForCreateForAppUsers user, String bo, String parentBo) {
		return LdapNameBuilder.newInstance(ldapProperties.getBaseDN()).add("o", "nic.in").add("o", parentBo)
				.add("o", bo).add("ou", "People").add("uid", user.getUsername()).build();
	}

	private Name fallback_buildDnForAppUsers(UserForCreateForAppUsers user, String bo, String parentBo) {
		log.warn("CALLING fallback_findByUid for user:{}, bo:{} and parentBo:{}", user.getEmail(), bo, parentBo);
		return null;
	}

	// @HystrixCommand(fallbackMethod = "fallback_updateUsingUid")
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
			case "title":
				context.setAttributeValue("title", dataToBeUpdated.getDesignation());
				break;
			case "zimotp":
				context.setAttributeValue("zimotp", dataToBeUpdated.getZimotp());
				break;
			case "dn":
				context.setAttributeValue("dn", dataToBeUpdated.getDn());
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

	// @HystrixCommand(fallbackMethod = "fallback_updateUsingMail")
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
			case "nicwifi":
				context.setAttributeValue("nicwifi", dataToBeUpdated.getNicwifi());
				break;
			case "title":
				context.setAttributeValue("title", dataToBeUpdated.getDesignation());
				break;
			case "zimotp":
				context.setAttributeValue("zimotp", dataToBeUpdated.getZimotp());
				break;
			case "dn":
				context.setAttributeValue("dn", dataToBeUpdated.getDn());
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

	// @HystrixCommand(fallbackMethod = "fallback_fetchServicePackage")
	public List<Quota> fetchServicePackage(String bo) {
		return ldapTemplate.find(query().base(ldapProperties.getBaseDN()).where("objectclass")
				.is("sunDelegatedOrganization").and("o").is(bo), Quota.class);
	}

	private List<Quota> fallback_fetchServicePackage(String bo) {
		log.warn("CALLING fallback_fetchServicePackage for bo : {}", bo);
		return null;
	}

	// @HystrixCommand(fallbackMethod = "fallback_fetchBosFromPo")
	public List<Po> fetchBosFromPo(String baseDn) {
		return ldapTemplate.find(query().base(baseDn).where("objectclass").is("sunDelegatedOrganization"), Po.class);
	}

	private List<Po> fallback_fetchBosFromPo(String baseDn) {
		log.warn("CALLING fallback_fetchBosFromPo for base dn: {}", baseDn);
		return null;
	}

	// @HystrixCommand(fallbackMethod = "fallback_fetchPos")
	public List<Po1> fetchPos(String baseDn) {
		return ldapTemplate.find(query().base(baseDn).where("objectclass").is("sunmanagedprovider"), Po1.class);
	}

	private List<Po1> fallback_fetchPos(String baseDn) {
		log.warn("CALLING fallback_fetchPos for base dn: {}", baseDn);
		return null;
	}

	// @HystrixCommand(fallbackMethod = "fallback_addAlias")
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

	// @HystrixCommand(fallbackMethod = "fallback_removeAlias")
	public void removeAlias(String uid, String alias) {
		UserForSearch user = findByUid(uid);
		List<String> aliases = user.getAliases();
		aliases.remove(alias);
		Object[] aliasObjectArray = aliases.toArray();
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValues("mailequivalentaddress", aliasObjectArray);
		ldapTemplate.modifyAttributes(context);
	}

	private void fallback_removeAlias(String uid, String alias) {
		log.warn("CALLING fallback_removeAlias for uid: {} and with new alias: {}", uid, alias);
	}

	// @HystrixCommand(fallbackMethod = "fallback_swapPrimaryWithAlias")
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

	public void updateMobile(String mail, String mobileToBeUpdated) {
		UserForSearch user = findByUidOrMailOrEquivalent(mail);
		Name dn = LdapNameBuilder.newInstance(user.getDn()).build();
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		context.setAttributeValue("mobile", mobileToBeUpdated);
		ldapTemplate.modifyAttributes(context);
	}

	// @HystrixCommand(fallbackMethod = "fallback_enableImap")
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

	// @HystrixCommand(fallbackMethod = "fallback_disablePop")
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

	// @HystrixCommand(fallbackMethod = "fallback_enablepop")
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

	// @HystrixCommand(fallbackMethod = "fallback_disableImap")
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

	// @HystrixCommand(fallbackMethod = "fallback_deletePartially")
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

	// @HystrixCommand(fallbackMethod = "fallback_moveToRetiredBo")
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

	// @HystrixCommand(fallbackMethod = "fallback_updateDateOfExpiry")
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

	// @HystrixCommand(fallbackMethod = "fallback_moveToContractualBo")
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

	// @HystrixCommand(fallbackMethod = "fallback_findHodDetails")
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

	// @HystrixCommand(fallbackMethod = "fallback_extendDateOfExpiry")
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

	// @HystrixCommand(fallbackMethod = "fallback_updateSunAvailableServices")
	public void updateSunAvailableServices(String baseDn, String count) {
		Name dn = LdapNameBuilder.newInstance(baseDn).build();
		ModificationItem[] mods = new ModificationItem[1];
		mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sunAvailableServices", count));
		ldapTemplate.modifyAttributes(dn, mods);
	}

	private void fallback_updateSunAvailableServices(String baseDn, String count) {
		log.warn("CALLING fallback_updateSunAvailableServices for base dn : {} and new count : {}", baseDn, count);
	}

	// @HystrixCommand(fallbackMethod = "fallback_resetPassword")
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
}
