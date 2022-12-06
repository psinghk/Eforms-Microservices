package in.nic.ashwini.eForms.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.config.ConfigPropertiesForMessage;
import in.nic.ashwini.eForms.db.master.entities.UserProfile;
import in.nic.ashwini.eForms.db.slave.entities.UserBasicFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.UserProfileFromSlave;
import in.nic.ashwini.eForms.exceptions.custom.ProfileNotCreated;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.MinistryStatsDto;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.services.ProfileService;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/profile")
public class ProfileController {

	private final ProfileService profileService;
	private final Util utilityService;
	private final ConfigPropertiesForMessage configPropertiesForMessage;

	@Autowired
	public ProfileController(ProfileService profileService, Util utilityService, ConfigPropertiesForMessage configPropertiesForMessage) {
		super();
		this.profileService = profileService;
		this.utilityService = utilityService;
		this.configPropertiesForMessage = configPropertiesForMessage;
	}

	@PostMapping(value = "/create")
	public Map<String, String> createProfile(@Valid @RequestBody ProfileDto profile,
			@RequestParam("email") @NotEmpty String email) throws ProfileNotCreated {
		Map<String, String> response = new HashMap<>();

		if (profileService.getProfileByEmail(email) != null) {
			log.info("Profile of {} already exists!!!", profile.getEmail());
			response.put("status", "Profile already exists!!!");
			return response;
		}

		if (profileService.createProfile(profile, false)) {
			log.info("Profile created successfully!!!");
			response.put("status", "Profile created successfully!!!");
			return response;
		} else {
			log.info("Profile could not be created!!! Please try after some time.");
			response.put("status", "Profile could not be created!!! Please try after some time.");
			return new HashMap<>();
		}

	}

	@PutMapping(value = "/update")
	public Map<String, String> updateProfileData(@RequestBody @Valid ProfileDto profile,
			@RequestParam("email") @NotEmpty String email) throws ProfileNotCreated {
		Map<String, String> response = new HashMap<>();
		UserProfileFromSlave userProfile = null;

		userProfile = profileService.getProfileByEmail(email);
		if (userProfile != null) {
			profile.setId(userProfile.getId());
			profile.setEmail(userProfile.getEmail());
			profile.setCreationTimeStamp(userProfile.getCreationTimeStamp());
			LocalDateTime currentTime = LocalDateTime.now();
			profile.setUpdationTimeStamp(currentTime);
		} else {
			log.info("Profile does not exist!!!");
			response.put("status", "Profile does not exist!!!");
			return response;
		}

		if (profileService.createProfile(profile, true)) {
			log.info("Profile of {} updated successfully!!!", profile.getEmail());
			response.put("status", "Profile updated successfully!!!");
			return response;
		} else {
			log.info("Profile could not be created!!! Please try after some time.");
			response.put("status", "Profile could not be updated!!! Please try after some time.");
			return new HashMap<>();
		}
	}

	@GetMapping(path = "/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> fetchProfileByEmail(@RequestParam("email") @NotEmpty String email) {
		UserProfileFromSlave userProfile = profileService.getProfileByEmail(email);

		if (userProfile != null) {
			log.debug("Profile is {}", userProfile);
			return ResponseEntity.ok().body(userProfile);
		} else {
			if (utilityService.isGovEmployee(email)) {
				log.info("{} is present in LDAP but not in eForms. Hence, fetching details from LDAP", email);
				return ResponseEntity.ok().body(utilityService.allLdapValues(email));
			} else {
				log.info("Something went wrong!!! Please try after some time.");
				return ResponseEntity.ok().body(configPropertiesForMessage.getSomethingwentwrong());
			}
		}
	}

	@GetMapping(path = "/fetchInBean", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserProfileFromSlave fetchProfileByEmailInBean(@RequestParam("email") @NotEmpty String email) {
		return profileService.getProfileByEmail(email);
	}

	@GetMapping("/isUserRegistered")
	public Boolean isUserRegistered(@RequestParam("email") @NotEmpty String email) {
		if (profileService.getProfileByEmail(email) != null)
			return true;
		else
			return false;
	}

	@GetMapping("/fetchUserProfileFromDataBase")
	public UserProfileFromSlave fetchUserProfileFromDataBase(@RequestParam("email") @NotEmpty String email) {
		return profileService.getProfileByEmail(email);
	}

	@GetMapping("/fetchOthersProfileFromDataBase")
	public UserProfileFromSlave fetchOthersProfileFromDataBase(@RequestParam("mail") @NotEmpty String email) {
		return profileService.getProfileByEmail(email);
	}

	@GetMapping(path = "/fetchUsersDetailsAsRo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> fetchUsersDetailsAsRo(@RequestParam("email") @NotEmpty String email) {
		HodDetailsDto hodDetails = null;
		log.info("Fetching (HoD)related details for hod email {}", email);
		hodDetails = profileService.fetchRoDetails(email);
		if (hodDetails != null) {
			return ResponseEntity.ok().body(hodDetails);
		} else {
			log.info("RO email {} does not exist in our repository!!!", email);
			Map<String, String> error = new HashMap<>();
			error.put("message", "RO email " + email + " does not exist in our repository!!!");
			return ResponseEntity.ok().body(error);
		}
	}

	@GetMapping(path = "/fetchRoDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> fetchRoDetails(@RequestParam("mail") @NotEmpty String email) {
		HodDetailsDto hodDetails = null;
		log.info("Fetching (HoD)related details for hod email {}", email);
		hodDetails = profileService.fetchRoDetails(email);
		if (hodDetails != null) {
			return ResponseEntity.ok().body(hodDetails);
		} else {
			log.info("RO email {} does not exist in our repository!!!", email);
			Map<String, String> error = new HashMap<>();
			error.put("message", "RO email " + email + " does not exist in our repository!!!");
			return ResponseEntity.ok().body(error);
		}
	}

	@GetMapping("/fetchRoDetailsInBean")
	public HodDetailsDto fetchRoDetails1(@RequestParam("mail") @NotEmpty String email) {
		log.info("Fetching (HoD)related details for hod email {}", email);
		return profileService.fetchRoDetails(email);
	}

	@GetMapping(path = "/isUserRo")
	public Boolean isUserRo(@RequestParam("email") @NotEmpty String email) {
		log.info("is User RO where user is {}", email);
		return profileService.isRoAvailable(email);
	}

	@GetMapping("/fetchUsersHavingRoAsApplicant")
	public List<UserBasicFromSlave> fetchUsersHavingRoAsApplicant(@RequestParam("email") String email) {
		log.info("Fetching list of users having RO {}", email);
		return profileService.fetchUsersHavingRo(email);
	}

	@GetMapping("/fetchUsersHavingRo")
	public List<UserBasicFromSlave> fetchUsersHavingRo(@RequestParam("mail") String email) {
		log.info("Fetching list of users having RO {}", email);
		return profileService.fetchUsersHavingRo(email);
	}

	@GetMapping("/fetchProfileByMobile")
	public UserProfileFromSlave fetchProfileByMobile(@RequestParam @NotEmpty String mobile) {
		return profileService.fetchProfileByMobile(mobile);
	}

	@GetMapping("/isMobileRegisteredInEforms")
	public boolean isMobileRegisteredInEforms(@RequestParam @NotEmpty String mobile) {
		if (profileService.fetchProfileByMobile(mobile) != null) {
			return true;
		}
		return false;
	}

	@GetMapping("fetchMobileFromProfile")
	public String fetchMobileFromProfile(@RequestParam("email") @NotEmpty String email) {
		UserProfileFromSlave userProfile = profileService.getProfileByEmail(email);
		if (userProfile != null)
			return userProfile.getMobile();
		else
			return "";
	}

	@GetMapping("fetchApplicantsByCategoryAndMinistry")
	public MinistryStatsDto fetchApplicantsByCategoryAndMinistry(@RequestParam("category") @NotEmpty String category,
			@RequestParam("ministry") @NotEmpty String ministry) {
		try {
			MinistryStatsDto ministryStatsDto = profileService.fetchApplicantsByEmploymentAndMinistry(category, URLDecoder.decode(ministry, StandardCharsets.UTF_8.toString()));
			return ministryStatsDto;	
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("fetchApplicantsByState")
	public MinistryStatsDto fetchApplicantsByState(@RequestParam("ministry") @NotEmpty String ministry) {
		try {
			return profileService.fetchApplicantsByState(URLDecoder.decode(ministry, StandardCharsets.UTF_8.toString()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// new added

	@GetMapping("/fetchApplicantsByCategoryAndMinistryAndDepartment")
	public MinistryStatsDto fetchApplicantsByCategoryAndMinistryAndDepartment(@RequestParam("category") String category,
			@RequestParam("ministry") String ministry, @RequestParam("department") String department) {
		try {
			return profileService.fetchApplicantsByEmploymentAndMinistryAndDepartment(category, URLDecoder.decode(ministry, StandardCharsets.UTF_8.toString()),
					URLDecoder.decode(department, StandardCharsets.UTF_8.toString()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/fetchApplicantsByCategoryAndStateAndDepartment")
	public MinistryStatsDto fetchApplicantsByCategoryAndStateAndDepartment(@RequestParam("category") String category,
			@RequestParam("state") String state, @RequestParam("department") String department) {
		try {
			return profileService.fetchApplicantsByEmploymentAndStateAndDepartment(category, URLDecoder.decode(state, StandardCharsets.UTF_8.toString()),
					URLDecoder.decode(department, StandardCharsets.UTF_8.toString()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/fetchApplicantsByCategoryAndOrganization")
	public MinistryStatsDto fetchApplicantsByCategoryAndOrganization(@RequestParam("category") String category,
			@RequestParam("organization") String organization) {
		try {
			return profileService.fetchApplicantsByEmploymentAndOrganization(category, URLDecoder.decode(organization, StandardCharsets.UTF_8.toString()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@PostMapping("/saveData")
	public UserProfile saveData(UserProfile userprofile) {
		return profileService.saveData(userprofile);
	}
	@PostMapping("/updateUserProfileDepartments")
	public int updateUserProfileDepartments(@RequestParam("new_dept") String emp_new_dept,@RequestParam("category") String emp_category,@RequestParam("ministry") String emp_min_state_org,@RequestParam("department") String emp_dept) {
		
		return profileService.updateUserProfileDepartments(emp_new_dept, emp_category, emp_min_state_org, emp_dept);
	}
}
