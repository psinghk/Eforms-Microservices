package in.nic.ashwini.eForms.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.config.ConfigPropertiesForMessage;
import in.nic.ashwini.eForms.db.master.entities.UserProfile;
import in.nic.ashwini.eForms.db.master.repositories.UserRepository;
import in.nic.ashwini.eForms.db.slave.entities.UserBasicFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.UserProfileFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.UserRepositoryToRead;
import in.nic.ashwini.eForms.exceptions.custom.ProfileNotCreated;
import in.nic.ashwini.eForms.models.HodDetailsDto;
import in.nic.ashwini.eForms.models.MinistryStatsDto;
import in.nic.ashwini.eForms.models.ProfileDto;
import in.nic.ashwini.eForms.utils.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfileService {

	private final UserRepository userRepository;
	private final UserRepositoryToRead userRepositoryToRead;
	private final Util utilityService;
	private final ConfigPropertiesForMessage configPropertiesForMessage;
	
	@PersistenceContext
	EntityManager em;
	
	@Autowired
	public ProfileService(UserRepository userRepository, UserRepositoryToRead userRepositoryToRead, Util utilityService,
			ConfigPropertiesForMessage configPropertiesForMessage) {
		super();
		this.userRepository = userRepository;
		this.userRepositoryToRead = userRepositoryToRead;
		this.utilityService = utilityService;
		this.configPropertiesForMessage = configPropertiesForMessage;
	}
	

	//@HystrixCommand(fallbackMethod = "fallback_createProfile")
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
			throw new ProfileNotCreated(configPropertiesForMessage.getSomethingwentwrong());
		}

	}

	private boolean fallback_createProfile(ProfileDto profileModel, boolean forUpdate) throws ProfileNotCreated {
		throw new ProfileNotCreated(configPropertiesForMessage.getServicedown());
	}

	//@HystrixCommand(fallbackMethod = "fallback_getProfileByEmail")
	public UserProfileFromSlave getProfileByEmail(String email) {
		log.info("Fetching profile of user {}", email);
		List<String> aliases = null;
		Optional<UserProfileFromSlave> profile = null;

		if (utilityService.isGovEmployee(email)) {
			aliases = utilityService.aliases(email);
		} else {
			aliases = Arrays.asList(email);
		}

		log.info("Aliases = {}", aliases);

		for (String mail : aliases) {
			profile = userRepositoryToRead.findByEmail(mail);
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

	//@HystrixCommand(fallbackMethod = "fallback_fetchRoDetails")
	public HodDetailsDto fetchRoDetails(String email) {
		if (utilityService.isGovEmployee(email)) {
			log.info("{} is present in LDAP. Hence, fetching details from LDAP", email);
			return utilityService.getHodValues(email);
		}
		UserProfileFromSlave userProfile = null;
		HodDetailsDto hodProfile = new HodDetailsDto();
		hodProfile.setEmail(email);
		Optional<UserProfileFromSlave> hodDetails = userRepositoryToRead.findFirstByRoEmailOrHodEmail(email, email);
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
		UserProfileFromSlave userProfile = null;
		Optional<UserProfileFromSlave> hodDetails = userRepositoryToRead.findFirstByRoEmailOrHodEmail(email, email);
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
			Optional<UserProfileFromSlave> hodDetails = userRepositoryToRead.findFirstByRoEmailOrHodEmail(email1, email1);
			if (hodDetails.isPresent()) {
				return true;
			}
		}
		return false;
	}

	public List<UserBasicFromSlave> fetchUsersHavingRo(String email) {
		List<UserBasicFromSlave> userList = new ArrayList<>();
		if (utilityService.isGovEmployee(email)) {
			List<String> aliases = utilityService.aliases(email);
			for (String mail : aliases) {
				List<UserBasicFromSlave> userSubList = userRepositoryToRead.findByRoEmailOrHodEmail(mail, mail);
				if (userSubList != null) {
					userList.addAll(userSubList);
				}
			}
			return userList;
		}
		return userRepositoryToRead.findByRoEmailOrHodEmail(email, email);
	}

	public UserProfileFromSlave fetchProfileByMobile(String mobile) {
		Optional<UserProfileFromSlave> userProfileOptional = userRepositoryToRead.findFirstByMobileContaining(mobile);
		if (userProfileOptional.isPresent()) {
			return userProfileOptional.orElse(null);
		}
		return null;
	}
	
	public MinistryStatsDto fetchApplicantsByEmploymentAndMinistry(String employment,String ministry) {
		MinistryStatsDto ministryStatsDto = new MinistryStatsDto();
		List<String> applicants = userRepositoryToRead.findByEmploymentAndMinistry(employment, ministry);
		Set<String> set = new HashSet<>(applicants);
		List<String> applicantsList = new ArrayList<>(set);
		ministryStatsDto.setEmployment(employment);
		ministryStatsDto.setMinistry(ministry);
		ministryStatsDto.setApplicants(applicantsList);
		if(applicants != null) {
			if(applicants.isEmpty())
				ministryStatsDto.setTotalApplicants(0);
			ministryStatsDto.setTotalApplicants(applicantsList.size());
		}
		return ministryStatsDto;
	}
	
	public MinistryStatsDto fetchApplicantsByState(String ministry) {
		MinistryStatsDto ministryStatsDto = new MinistryStatsDto();
		List<String> applicants = userRepositoryToRead.findByState(ministry);
		Set<String> set = new HashSet<>(applicants);
		List<String> applicantsList = new ArrayList<>(set);
		ministryStatsDto.setEmployment("state");
		ministryStatsDto.setMinistry(ministry);
		ministryStatsDto.setApplicants(applicantsList);
		if(applicants != null) {
			if(applicants.isEmpty())
				ministryStatsDto.setTotalApplicants(0);
			ministryStatsDto.setTotalApplicants(applicantsList.size());
		}
		return ministryStatsDto;
	}
	//newly added
	
	public MinistryStatsDto fetchApplicantsByEmploymentAndMinistryAndDepartment(String category,String ministry,String department) {
		MinistryStatsDto ministryStatsDto = new MinistryStatsDto();
		List<String> applicants = userRepositoryToRead.findByEmploymentAndMinistryAndDepartment(category, ministry, department);
		Set<String> set = new HashSet<>(applicants);
		List<String> applicantsList = new ArrayList<>(set);
		ministryStatsDto.setEmployment(category);
		ministryStatsDto.setMinistry(ministry);
		ministryStatsDto.setDepartment(department);
		ministryStatsDto.setApplicants(applicantsList);
		if(applicants != null) {
			if(applicants.isEmpty())
				ministryStatsDto.setTotalApplicants(0);
			ministryStatsDto.setTotalApplicants(applicantsList.size());
		}
		return ministryStatsDto;
	}
	
	public MinistryStatsDto fetchApplicantsByEmploymentAndStateAndDepartment(String category,String state,String department) {
		MinistryStatsDto ministryStatsDto = new MinistryStatsDto();
		List<String> applicants = userRepositoryToRead.findByEmploymentAndStateAndDepartment(category, state, department);
		Set<String> set = new HashSet<>(applicants);
		List<String> applicantsList = new ArrayList<>(set);
		ministryStatsDto.setEmployment(category);
		ministryStatsDto.setMinistry(state);
		ministryStatsDto.setDepartment(department);
		ministryStatsDto.setApplicants(applicantsList);
		if(applicants != null) {
			if(applicants.isEmpty())
				ministryStatsDto.setTotalApplicants(0);
			ministryStatsDto.setTotalApplicants(applicantsList.size());
		}
		return ministryStatsDto;
	}
	
	public MinistryStatsDto fetchApplicantsByEmploymentAndOrganization(String category,String organization) {
		MinistryStatsDto ministryStatsDto = new MinistryStatsDto();
		List<String> applicants = userRepositoryToRead.findByEmploymentAndOrganization(category, organization);
		Set<String> set = new HashSet<>(applicants);
		List<String> applicantsList = new ArrayList<>(set);
		ministryStatsDto.setEmployment(category);
		ministryStatsDto.setMinistry(organization);
		ministryStatsDto.setDepartment("");
		ministryStatsDto.setApplicants(applicantsList);
		if(applicants != null) {
			if(applicants.isEmpty())
				ministryStatsDto.setTotalApplicants(0);
			ministryStatsDto.setTotalApplicants(applicantsList.size());
		}
		return ministryStatsDto;
	}
	
	public UserProfile saveData(UserProfile userprofile) {
		return userRepository.save(userprofile);
	}
	public int updateUserProfileDepartments(String emp_new_dept,String emp_category,String emp_min_state_org,String emp_dept) {
		int retval=0;
		List<UserProfile> listtobeUpdated = new ArrayList<>();
		if(emp_category.equalsIgnoreCase("central") || emp_category.equalsIgnoreCase("ut")) {
			listtobeUpdated = userRepository.findByEmploymentAndMinistryAndDepartmentForUserProfile(emp_category,emp_min_state_org,emp_dept);
		} else if(emp_category.equalsIgnoreCase("state")) {
			listtobeUpdated = userRepository.findByEmploymentAndStateAndDepartmentForUserProfile(emp_category,emp_min_state_org,emp_dept);
		}
		
		if(!listtobeUpdated.isEmpty()) {
		for(UserProfile uprofile:listtobeUpdated) {
			uprofile.setDepartment(emp_new_dept);
			userRepository.save(uprofile);
		}
		retval=1;
		}
		return retval;
	}
	
	
	
	
}
