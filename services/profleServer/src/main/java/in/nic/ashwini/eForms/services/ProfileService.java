package in.nic.ashwini.eForms.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import in.nic.ashwini.eForms.entities.UserProfile;
import in.nic.ashwini.eForms.entities.projections.UserBasic;
import in.nic.ashwini.eForms.exceptions.custom.ProfileNotCreated;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.repositories.UserRepository;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfileService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private Util utilityService;

	@Value("${message.somethingwentwrong}")
	private String SOME_THING_WENT_WRONG;

	@Value("${message.service.down}")
	private String SERVICE_IS_DOWN;

	@PersistenceContext
	EntityManager em;

	@HystrixCommand(fallbackMethod = "fallback_createProfile")
	public boolean createProfile(ProfileDto profileModel, boolean forUpdate) throws ProfileNotCreated {
		log.debug("Received data from client = {}", profileModel);
		ModelMapper modelMapper = new ModelMapper();
		UserProfile userProfile = modelMapper.map(profileModel, UserProfile.class);
		if (utilityService.isGovEmployee(userProfile.getHodEmail())) {
			log.info("HOD {} belongs to LDAP", userProfile.getHodEmail());
			HodDetailsDto hodDetail = utilityService.getHodValues(profileModel.getHodEmail());
			userProfile.setHodName(hodDetail.getFirstName());
			userProfile.setHodDesignation(hodDetail.getDesignation());
			userProfile.setHodMobile(hodDetail.getMobile());
			userProfile.setHodTelephone(hodDetail.getTelephoneNumber());
			userProfile.setRoMobile(hodDetail.getMobile());
			log.info("HOD's details in user's profile updated with LDAP values");
		}
		if (!forUpdate) {
			log.info("Request received for new registration for user {}", userProfile.getEmail());
			if(userProfile.getMobile().equalsIgnoreCase(userProfile.getHodMobile()) || userProfile.getMobile().equalsIgnoreCase(userProfile.getRoMobile())) {
				throw new ProfileNotCreated("User's mobile number can not be same as RO mobile number");
			}
			LocalDateTime currentTime = LocalDateTime.now();
			userProfile.setCreationTimeStamp(currentTime);
		}
		UserProfile empData = userRepository.save(userProfile);
		if (empData != null) {
			return true;
		} else {
			throw new ProfileNotCreated(SOME_THING_WENT_WRONG);
		}

	}

	private boolean fallback_createProfile(ProfileDto profileModel, boolean forUpdate) throws ProfileNotCreated {
		throw new ProfileNotCreated(SERVICE_IS_DOWN);
	}

	@HystrixCommand(fallbackMethod = "fallback_getProfileByEmail")
	public UserProfile getProfileByEmail(String email) {
		log.info("Fetching profile of user {}", email);
		List<String> aliases = null;
		Optional<UserProfile> profile = null;

		if (utilityService.isGovEmployee(email)) {
			aliases = utilityService.aliases(email);
		} else {
			aliases = Arrays.asList(email);
		}

		log.info("Aliases = {}", aliases);

		for (String mail : aliases) {
			profile = userRepository.findByEmail(mail);
			if (profile.isPresent())
				break;
		}
		if (profile.isPresent()) {
			return profile.orElse(null);
		} else {
			return null;
		}
		// return profile.orElseThrow(()-> new NoRecordFoundException("Profile for Email
		// doesn't exist" + email));
	}

	private UserProfile fallback_getProfileByEmail(String email) {
		log.warn("INSIDE FALLBACK for getProfileByEmail Method");
		Optional<UserProfile> profile = userRepository.findByEmail(email);
		if (profile.isPresent()) {
			return profile.orElse(null);
		} else {
			return null;
		}
	}

	@HystrixCommand(fallbackMethod = "fallback_fetchRoDetails")
	public HodDetailsDto fetchRoDetails(String email) {
		if (utilityService.isGovEmployee(email)) {
			log.info("{} is present in LDAP. Hence, fetching details from LDAP", email);
			return utilityService.getHodValues(email);
		}
		UserProfile userProfile = null;
		HodDetailsDto hodProfile = new HodDetailsDto();
		hodProfile.setEmail(email);
		Optional<UserProfile> hodDetails = userRepository.findFirstByRoEmailOrHodEmail(email, email);
		if (hodDetails.isPresent()) {
			userProfile = hodDetails.orElse(null);
		}

		if (userProfile != null) {
			if (userProfile.getHodName() != null && !userProfile.getHodName().isEmpty()) {
				hodProfile.setFirstName(userProfile.getHodName());
			} else if (userProfile.getRoName() != null && !userProfile.getRoName().isEmpty()) {
				hodProfile.setFirstName(userProfile.getRoName());
			} else {
				hodProfile.setFirstName("");
			}

			if (userProfile.getHodMobile() != null && !userProfile.getHodMobile().isEmpty()) {
				hodProfile.setMobile(userProfile.getHodMobile());
			} else if (userProfile.getRoMobile() != null && !userProfile.getRoMobile().isEmpty()) {
				hodProfile.setMobile(userProfile.getRoMobile());
			} else {
				hodProfile.setMobile("");
			}

			if (userProfile.getHodDesignation() != null && !userProfile.getHodDesignation().isEmpty()) {
				hodProfile.setDesignation(userProfile.getHodDesignation());
			} else if (userProfile.getRoDesignation() != null && !userProfile.getRoDesignation().isEmpty()) {
				hodProfile.setDesignation(userProfile.getRoDesignation());
			} else {
				hodProfile.setDesignation("");
			}

			if (userProfile.getHodTelephone() != null && !userProfile.getHodTelephone().isEmpty()) {
				hodProfile.setTelephoneNumber(userProfile.getHodTelephone());
			} else {
				hodProfile.setTelephoneNumber("");
			}
			return hodProfile;
		} else {
			return null;
		}
	}

	private HodDetailsDto fallback_fetchRoDetails(String email) {
		HodDetailsDto hodProfile = new HodDetailsDto();
		hodProfile.setEmail(email);
		UserProfile userProfile = null;
		Optional<UserProfile> hodDetails = userRepository.findFirstByRoEmailOrHodEmail(email, email);
		if (hodDetails.isPresent()) {
			userProfile = hodDetails.orElse(null);
		}

		if (userProfile != null) {
			if (userProfile.getHodName() != null && !userProfile.getHodName().isEmpty()) {
				hodProfile.setFirstName(userProfile.getHodName());
			} else if (userProfile.getRoName() != null && !userProfile.getRoName().isEmpty()) {
				hodProfile.setFirstName(userProfile.getRoName());
			} else {
				hodProfile.setFirstName("");
			}

			if (userProfile.getHodMobile() != null && !userProfile.getHodMobile().isEmpty()) {
				hodProfile.setMobile(userProfile.getHodMobile());
			} else if (userProfile.getRoMobile() != null && !userProfile.getRoMobile().isEmpty()) {
				hodProfile.setMobile(userProfile.getRoMobile());
			} else {
				hodProfile.setMobile("");
			}

			if (userProfile.getHodDesignation() != null && !userProfile.getHodDesignation().isEmpty()) {
				hodProfile.setDesignation(userProfile.getHodDesignation());
			} else if (userProfile.getRoDesignation() != null && !userProfile.getRoDesignation().isEmpty()) {
				hodProfile.setDesignation(userProfile.getRoDesignation());
			} else {
				hodProfile.setDesignation("");
			}

			if (userProfile.getHodTelephone() != null && !userProfile.getHodTelephone().isEmpty()) {
				hodProfile.setTelephoneNumber(userProfile.getHodTelephone());
			} else {
				hodProfile.setTelephoneNumber("");
			}
			return hodProfile;
		} else {
			return null;
		}

	}

	public Boolean isRoAvailable(String email) {
		List<String> aliases = utilityService.aliases(email);
		for (String email1 : aliases) {
			Optional<UserProfile> hodDetails = userRepository.findFirstByRoEmailOrHodEmail(email1, email1);
			if (hodDetails.isPresent()) {
				return true;
			}
		}
		return false;
	}

	public List<UserBasic> fetchUsersHavingRo(String email) {
		List<UserBasic> userList = new ArrayList<>();
		if (utilityService.isGovEmployee(email)) {
			List<String> aliases = utilityService.aliases(email);
			for (String mail : aliases) {
				List<UserBasic> userSubList = userRepository.findByRoEmailOrHodEmail(mail, mail);
				if (userSubList != null) {
					userList.addAll(userSubList);
				}
			}
			return userList;
		}
		return userRepository.findByRoEmailOrHodEmail(email, email);
	}

	public UserProfile fetchProfileByMobile(String mobile) {
		Optional<UserProfile> userProfileOptional = userRepository.findFirstByMobileContaining(mobile);
		if (userProfileOptional.isPresent()) {
			return userProfileOptional.orElse(null);
		}
		return null;
	}

}
