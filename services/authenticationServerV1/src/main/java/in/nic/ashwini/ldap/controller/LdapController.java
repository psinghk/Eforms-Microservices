package in.nic.ashwini.ldap.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.ldap.data.AssociatedDomains;
import in.nic.ashwini.ldap.data.MobileAndName;
import in.nic.ashwini.ldap.data.Po;
import in.nic.ashwini.ldap.data.Po1;
import in.nic.ashwini.ldap.data.Quota;
import in.nic.ashwini.ldap.data.UserAttributes;
import in.nic.ashwini.ldap.data.UserForCreate;
import in.nic.ashwini.ldap.data.UserForCreateForAppUsers;
import in.nic.ashwini.ldap.data.UserForHodDetails;
import in.nic.ashwini.ldap.data.UserForHomePageDA;
import in.nic.ashwini.ldap.data.UserForSearch;
import in.nic.ashwini.ldap.service.UserService;
import in.nic.ashwini.ldap.validation.CustomException;
import in.nic.ashwini.ldap.validation.Uid;
import in.nic.ashwini.ldap.validation.UsernameEmail;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
public class LdapController {

	@Autowired
	private UserService userRepo;

	@RequestMapping("/authenticate")
	public boolean authenticate(@RequestParam @NotBlank @UsernameEmail final String username,
			@RequestParam @NotBlank final String password) {
		log.debug("Authentication begins for email : {} ", username);
		return userRepo.authenticate(username, password);
	}

	@RequestMapping("/findByUid")
	public UserForSearch findByUid(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("Finding user through uid : {} ", uid);
		return userRepo.findByUid(uid);
	}

	@RequestMapping("/findByMail")
	public UserForSearch findByMail(@RequestParam @NotBlank @Email final String mail) {
		log.debug("Finding user through mail : {} ", mail);
		return userRepo.findByMail(mail);
	}

	@RequestMapping("/fetchUid")
	public String fetchUid(@RequestParam @NotBlank @Email final String mail) {
		String uid = "";
		log.debug("Fetching details of user : {} ", mail);
		UserForSearch user = userRepo.findByMailOrEquivalent(mail);
		if (user != null) {
			uid = user.getUsername();
		}
		return uid;
	}

	@RequestMapping("/fetchAliases")
	public List<String> fetchAliases(@RequestParam @NotBlank @UsernameEmail final String mail) {
		List<String> aliases = new ArrayList<>();
		log.debug("Fetching aliases of user : {} ", mail);
		UserForSearch user = userRepo.findByUidOrMailOrEquivalent(mail);
		if (user != null) {
			aliases = user.getAliases();
			if (aliases.size() > 10) {
				aliases = Arrays.asList(mail);
			}
		}
		return aliases;
	}

	@RequestMapping("/fetchAllAliases")
	public List<String> fetchAllAliases(@RequestParam @NotBlank @UsernameEmail final String mail) {
		List<String> aliases = new ArrayList<>();
		log.debug("Fetching aliases of user : {} ", mail);
		UserForSearch user = userRepo.findByUidOrMailOrEquivalent(mail);
		if (user != null) {
			aliases = user.getAliases();
		}
		return aliases;
	}

	@RequestMapping("/fetchAliasesAlongWithPrimary")
	public List<String> fetchAliasesAlongWithPrimary(@RequestParam @NotBlank @UsernameEmail final String mail) {
		List<String> aliases = new ArrayList<>();
		log.debug("Fetching aliases with primary email of user : {} ", mail);
		UserForSearch user = userRepo.findByUidOrMailOrEquivalent(mail);
		if (user != null) {
			aliases = user.getAliases();
		}
		aliases.add(mail);
		return aliases;
	}

	@RequestMapping("/fetchMobile")
	public String fetchMobile(@RequestParam @NotBlank @UsernameEmail final String mail) {
		log.debug("Fetching mobile number of user : {} ", mail);
		UserForSearch user = userRepo.findByUidOrMailOrEquivalent(mail);
		if (user != null) {
			return user.getMobile();
		}
		return "";
	}

	@RequestMapping("/findByMailOrEquivalent")
	public UserForSearch findByMailOrEquivalent(@RequestParam @NotBlank @Email final String mail) {
		System.out.println("hello delhi");
		log.debug("Finding user through mail or equivalent : {} ", mail);
		return userRepo.findByMailOrEquivalent(mail);
	}

	@RequestMapping("/findByUidOrMailOrEquivalent")
	public UserForSearch findByUidOrMailOrEquivalent(@RequestParam @NotBlank @UsernameEmail final String mail) {
		log.debug("Finding user through uid or mail or equivalent : {} ", mail);
		return userRepo.findByUidOrMailOrEquivalent(mail);
	}

	@RequestMapping("/fetchMobileAndName")
	public MobileAndName fetchMobileAndName(@RequestParam @NotBlank @UsernameEmail final String mail) {
		log.debug("Finding mobile and name through uid or mail or equivalent : {} ", mail);
		return userRepo.fetchMobileAndName(mail);
	}

	@RequestMapping("/findHodDetails")
	public UserForHodDetails findHodDetails(@RequestParam @NotBlank @UsernameEmail final String mail) {
		log.debug("Finding HOD details for email : {} ", mail);
		UserForHodDetails hodDetails = userRepo.findHodDetails(mail);
		hodDetails.setEmail(mail);
		return hodDetails;
	}

	@RequestMapping("/findByMobile")
	public List<UserForSearch> findByMobile(
			@RequestParam @NotBlank @Pattern(regexp = "^[+0-9]{10,13}$", message = "Only digits with length from 10 to 13 are allowed") final String mobile) {
		log.debug("Finding user through mobile : {} ", mobile);
		return userRepo.findByMobile(mobile);
	}

	@RequestMapping("/isMobileAvailable")
	public boolean isMobileAvailable(
			@RequestParam @NotBlank @Pattern(regexp = "^[+0-9]{10,13}$", message = "Only digits with length from 10 to 13 are allowed") final String mobile) {
		boolean result = false;
		List<UserForSearch> user = userRepo.findByMobile(mobile);
		if (user.size() > 0) {
			log.debug("Mobile number is available");
			result = true;
		}
		return result;
	}

	@RequestMapping("/isEmailAvailable")
	public boolean isEmailAvailable(@RequestParam @NotBlank @UsernameEmail final String mail) {
		return userRepo.isEmailAvailable(mail);
	}

	@RequestMapping("/isNicEmployee")
	public boolean isNicEmployee(@RequestParam @NotBlank @UsernameEmail final String mail) {
		boolean result = false;
		UserForSearch user = userRepo.findByUidOrMailOrEquivalent(mail);
		if (user != null) {
			String dn = user.getDn();
			String[] exploded = dn.split(",");
			String user_bo = exploded[2];
			String final_bo = user_bo.replace("o=", "");
			System.out.println("final_bo::::::::" + final_bo);
			if ((final_bo.equalsIgnoreCase("NIC Employees")) || (final_bo.equalsIgnoreCase("dio"))
					|| (final_bo.equalsIgnoreCase("nic-official-id"))) {
				log.debug("User {} is NIC employee", mail);
				result = true;
			}
		}
		return result;
	}

	@RequestMapping("/findByAttribute")
	public List<UserForSearch> findByAttribute(@RequestParam @NotBlank final String key,
			@RequestParam @NotBlank final String value) {
		return userRepo.findByAttribute(key, value);
	}

	@RequestMapping("/validateEmail")
	public boolean validateEmail(@RequestParam @NotBlank @Email final String mail) {
		log.debug("Validating email address where email is {}", mail);
		return userRepo.validateEmailAddress(mail);
	}

	@RequestMapping("/findDn")
	public String findDn(@RequestParam @NotBlank @UsernameEmail final String uid) {
		log.debug("Finding DN of user : {}", uid);
		return userRepo.findDn(uid);
	}

	@RequestMapping("/findDomains")
	public List<String> findDomains(@RequestParam @NotBlank final String bo) {
		log.debug("Finding all the domains for BO : {} ", bo);
		return userRepo.findAllowedDomains(bo);
	}

	@RequestMapping("/findBoMembers")
	public List<UserForHomePageDA> findBoMembers(@RequestParam @NotBlank final String baseDn) {
		log.debug("Finding BO members for dn : {}", baseDn);
		return userRepo.findBoMembers(baseDn);
	}

	@RequestMapping("/fetchServicePackage")
	public List<Quota> fetchServicePackage(@RequestParam @NotBlank final String bo) {
		System.out.println("INSIDE LDAP FETCHSERVICE METHOD :::::::::::::");
		log.debug("Fetching service package for BO : {}", bo);
		return userRepo.fetchServicePackage(bo);
	}

	@RequestMapping("/fetchBos")
	public List<Po> fetchBos(@RequestParam @NotBlank final String base) {
		log.debug("Fetching BOs for base : {}", base);
		return userRepo.fetchBosFromPo(base);
	}
	
	@RequestMapping("/fetchPos")
	public List<Po1> fetchPos(@RequestParam @NotBlank final String base) {
		log.debug("Fetching BOs for base : {}", base);
		return userRepo.fetchPos(base);
	}

	@RequestMapping("/addAlias")
	public boolean addAlias(@RequestParam @NotBlank @Uid final String uid,
			@RequestParam @NotBlank @Email final String alias) {
		log.debug("Adding alias {} for user : {} ", alias, uid);
		userRepo.addAlias(uid, alias);
		return true;
	}
	
	@RequestMapping("/removeAlias")
	public boolean removeAlias(@RequestParam @NotBlank @Uid final String uid,
			@RequestParam @NotBlank @Email final String alias) {
		log.debug("Removing alias {} for user : {} ", alias, uid);
		userRepo.removeAlias(uid, alias);
		return true;
	}

	@RequestMapping("/swapPrimaryWithAlias")
	public boolean swapPrimaryWithAlias(@RequestParam @NotBlank @Uid final String uid,
			@RequestParam @NotBlank @Email final String aliasToBeSwapped) {
		log.debug("Swapping alias {} with primary email for user : {} ", aliasToBeSwapped, uid);
		userRepo.swapPrimaryWithAlias(uid, aliasToBeSwapped);
		return true;
	}

	@RequestMapping("/enableImap")
	public boolean enableImap(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("enabling IMAP for user : {}", uid);
		userRepo.enableImap(uid);
		return true;
	}

	@RequestMapping("/enablePop")
	public boolean enablePop(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("enabling POP for user : {}", uid);
		userRepo.enablePop(uid);
		return true;
	}
	
	@RequestMapping("/enableSmime")
	public boolean enableSmime(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("enabling SMIME for user : {}", uid);
		userRepo.enableSmime(uid);
		return true;
	}
	
	@RequestMapping("/enableSmtp")
	public boolean enableSmtp(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("enabling SMTP for user : {}", uid);
		userRepo.enableSmtp(uid);
		return true;
	}

	@RequestMapping("/disablePop")
	public boolean disablePop(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("disabling POP for user : {}", uid);
		userRepo.disablePop(uid);
		return true;
	}

	@RequestMapping("/disableImap")
	public boolean disableImap(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("disabling IMAP for user : {}", uid);
		userRepo.disableImap(uid);
		return true;
	}
	
	@RequestMapping("/disableSmime")
	public boolean disableSmime(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("disabling SMIME for user : {}", uid);
		userRepo.disableSmime(uid);
		return true;
	}
	
	@RequestMapping("/disableSmtp")
	public boolean disableSmtp(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("disabling SMTP for user : {}", uid);
		userRepo.disableSmtp(uid);
		return true;
	}

	@RequestMapping("/createMailUsers")
	public boolean createMailUsers(@RequestBody UserForCreate user, @RequestParam("bo") final String bo,
			@RequestParam("parentBo") final String parentBo) {
		System.out.println("HELLOOO create user");
		log.debug("Creating mail user {} with bo {} in parentBo {}", user.getEmail(), bo, parentBo);
		userRepo.createMailUsers(user, bo, parentBo);
		return true;
	}

	@RequestMapping("/createAppUsers")
	public boolean createAppUsers(@RequestBody UserForCreateForAppUsers user, @RequestParam @NotBlank final String bo,
			@RequestParam @NotBlank final String parentBo) {
		System.out.println("inside of createAppUser:::::::::::::::: bo"+bo+"po"+parentBo);
		log.debug("Creating App user {} with bo {} in parentBo {}", user.getEmail(), bo, parentBo);
		userRepo.createAppUsers(user, bo, parentBo);
		return true;
	}

	@RequestMapping("/updateAll")
	public boolean updateAll(@RequestBody UserForCreate user) {
		log.debug("Updating user details for user : {}", user.getEmail());
		userRepo.update(user);
		return true;
	}

	@RequestMapping("/updateMobile")
	public boolean updateAll(@RequestParam @NotBlank String mail, @RequestParam @NotBlank String mobile) {
		log.debug("Updating Mobile number for user : {}", mail);
		mobile = mobile.trim();
		if(!mobile.startsWith("+")) {
			mobile = "+"+mobile;
		}
		userRepo.updateMobile(mail,mobile);
		return true;
	}
	
	@RequestMapping("/updateThroughUid")
	public boolean updateThroughUid(@Valid @NonNull @RequestBody UserAttributes userAttributes) {
		if (userAttributes.getUsername() == null) {
			log.debug("Updating user through uid : But username is missing");
			throw new CustomException("username", "Username is missing");
		}
		if (userAttributes.getUsername().isEmpty()) {
			log.debug("Updating user through uid : But username is empty");
			throw new CustomException("username", "Username can not be blank");
		}
		log.debug("Updating user details for user : {}", userAttributes.getUsername());
		userRepo.updateUsingUid(userAttributes);
		return true;
	}

	@RequestMapping("/updateThroughMail")
	public boolean updateThroughMail(@Valid @NonNull @RequestBody UserAttributes userAttributes) {
		if (userAttributes.getEmail() == null) {
			log.debug("Updating user through email : But email is missing");
			throw new CustomException("email", "Email is missing");
		}
		if (userAttributes.getEmail().isEmpty()) {
			log.debug("Updating user through email : But email is empty");
			throw new CustomException("email", "Email can not be blank");
		}
		log.debug("Updating user details for user : {}", userAttributes.getEmail());
		userRepo.updateUsingMail(userAttributes);
		return true;
	}

	@RequestMapping("/updateDateOfExpiry")
	public boolean updateDateOfExpiry(@Valid @NonNull @RequestBody UserAttributes userAttributes) {
		if (userAttributes.getUser().getDateOfAccountExpiry() == null) {
			log.debug("Updating date of expiry : But Expiry date is missing");
			throw new CustomException("dateOfAccountExpiry", "Expiry date is missing");
		}
		if (userAttributes.getUser().getDateOfAccountExpiry().isEmpty()) {
			log.debug("Updating date of expiry : But Expiry date is blank");
			throw new CustomException("dateOfAccountExpiry", "Expiry date can not be blank");
		}
		log.debug("Updating date of expiry for user {} with {} : ", userAttributes.getAttributes(),
				userAttributes.getUser().getDateOfAccountExpiry());
		userRepo.updateUsingUid(userAttributes);
		return true;
	}

	@RequestMapping("/deleteCompletely")
	public boolean deleteCompletely(@RequestBody UserForCreate user) {
		log.debug("Deleting user {} completely", user.getEmail());
		userRepo.delete(user);
		return true;
	}

	@RequestMapping("/deletePartially")
	public boolean deletePartially(@RequestBody UserForSearch user) {
		log.debug("Inactivating user {} instead of deleting ", user.getEmail());
		userRepo.deletePartially(user);
		return true;
	}

	@RequestMapping("/inActivate")
	public boolean inActivate(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("Inactivating user {} ", uid);
		userRepo.inActivate(uid);
		return true;
	}

	@RequestMapping("/activate")
	public boolean activate(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("Activating user {} ", uid);
		userRepo.activate(uid);
		return true;
	}

	@RequestMapping("/moveToRetiredBo")
	public boolean moveToRetiredBo(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("Moving user {} to retired bo ", uid);
		userRepo.moveToRetiredBo(uid);
		return true;
	}

	@RequestMapping("/moveToContractualBo")
	public boolean moveToContractualBo(@RequestParam @NotBlank @Uid final String uid) {
		log.debug("Moving user {} to contractual bo ", uid);
		userRepo.moveToContractualBo(uid);
		return true;
	}

	@RequestMapping("/extendDateOfExpiry")
	public boolean extendDateOfExpiry(@RequestParam @NotBlank @Uid final String uid,
			@RequestParam @NotBlank final String dateOfExpiry) {
		log.debug("extending date of account expiry for user {} and account expiry date is {}", uid, dateOfExpiry);
		userRepo.extendDateOfExpiry(uid, dateOfExpiry);
		return true;
	}

	@RequestMapping("/updateSunAvailableServices")
	public boolean updateSunAvailableServices(@RequestParam @NotBlank final String baseDn,
			@RequestParam @NotBlank final String count) {
		log.debug("Updating service package for dn {} with count = {}", baseDn, count);
		userRepo.updateSunAvailableServices(baseDn, count);
		return true;
	}

	@RequestMapping("/resetPassword")
	public boolean resetPassword(@RequestParam @NotBlank @Uid final String uid,
			@RequestParam @NotBlank final String password) {
		log.debug("Resetting password for user {}", uid);
		userRepo.resetPassword(uid, password);
		return true;
	}

	@RequestMapping("/fetchEmailsAgainstMobile")
	public List<String> fetchEmailsAgainstMobile(@RequestParam @NotBlank final String mobile) {
		log.debug("fetching email addresses against mobile {}", mobile);
		List<UserForSearch> users = userRepo.findByMobile(mobile);
		List<String> emails = new ArrayList<>();
		if (users != null && users.size()>0) {
			for (UserForSearch userForSearch : users) {
				emails.add(userForSearch.getEmail());
			}
		}
		return emails;
	}
	
	@RequestMapping("/isNicOutsourced")
	public boolean isNicOutsourced(@RequestParam @NotBlank @UsernameEmail final String mail) {
		boolean result = false;
		UserForSearch user = userRepo.findByUidOrMailOrEquivalent(mail);
		if (user != null) {
			String dn = user.getDn();
			String[] exploded = dn.split(",");
			String user_bo = exploded[2];
			String final_bo = user_bo.replace("o=", "");
			System.out.println("final_bo::::::::" + final_bo);
			if ((final_bo.equalsIgnoreCase("nic support outsourced")) || (final_bo.equalsIgnoreCase("nationalknowledgenetwork"))) {
				log.debug("User {} is NIC Outsourced employee",mail);
				result = true;
			}
		}
		return result;
	}
	
	@RequestMapping("/fetchAssociatedDomains")
	public AssociatedDomains fetchAssociatedDomains() {
		log.debug("fetching associated domains");
		return userRepo.fetchAssociatedDomains();
	}
}
