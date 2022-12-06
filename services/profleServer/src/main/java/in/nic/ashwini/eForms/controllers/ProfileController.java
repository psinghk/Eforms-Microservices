package in.nic.ashwini.eForms.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.entities.UserProfile;
import in.nic.ashwini.eForms.entities.projections.UserBasic;
import in.nic.ashwini.eForms.exceptions.custom.ProfileNotCreated;
import in.nic.ashwini.eForms.models.HodDetailsDto;
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

	@Value("${message.somethingwentwrong}")
	private String SOME_THING_WENT_WRONG;

	@Value("${message.service.down}")
	private String SERVICE_IS_DOWN;

	@Autowired
	public ProfileController(ProfileService profileService, Util utilityService) {
		super();
		this.profileService = profileService;
		this.utilityService = utilityService;
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
		UserProfile userProfile = null;

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
		UserProfile userProfile = profileService.getProfileByEmail(email);

		if (userProfile != null) {
			log.debug("Profile is {}", userProfile);
			return ResponseEntity.ok().body(userProfile);
		} else {
			if (utilityService.isGovEmployee(email)) {
				log.info("{} is present in LDAP but not in eForms. Hence, fetching details from LDAP", email);
				return ResponseEntity.ok().body(utilityService.allLdapValues(email));
			} else {
				log.info("Something went wrong!!! Please try after some time.");
				return ResponseEntity.ok().body(SOME_THING_WENT_WRONG);
			}
		}
	}

	@GetMapping(path = "/fetchInBean", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserProfile fetchProfileByEmailInBean(@RequestParam("email") @NotEmpty String email) {
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
	public UserProfile fetchUserProfileFromDataBase(@RequestParam("email") @NotEmpty String email) {
		return profileService.getProfileByEmail(email);
	}

	@GetMapping("/fetchOthersProfileFromDataBase")
	public UserProfile fetchOthersProfileFromDataBase(@RequestParam("mail") @NotEmpty String email) {
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
	public Boolean isUserRo(@RequestParam("email") @NotEmpty String email){
		log.info("is User RO where user is {}", email);
		return profileService.isRoAvailable(email);
	}

	@GetMapping("/fetchUsersHavingRoAsApplicant")
	public List<UserBasic> fetchUsersHavingRoAsApplicant(@RequestParam("email") String email) {
		log.info("Fetching list of users having RO {}", email);
		return profileService.fetchUsersHavingRo(email);
	}

	@GetMapping("/fetchUsersHavingRo")
	public List<UserBasic> fetchUsersHavingRo(@RequestParam("mail") String email) {
		log.info("Fetching list of users having RO {}", email);
		return profileService.fetchUsersHavingRo(email);
	}

	@GetMapping("/fetchProfileByMobile")
	public UserProfile fetchProfileByMobile(@RequestParam @NotEmpty String mobile) {
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
		UserProfile userProfile = profileService.getProfileByEmail(email);
		if (userProfile != null)
			return userProfile.getMobile();
		else
			return "";
	}

}
