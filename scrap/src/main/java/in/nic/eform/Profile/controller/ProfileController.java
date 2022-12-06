package in.nic.eform.Profile.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.nic.eform.Profile.exception.custom.NoRecordFoundException;
import in.nic.eform.Profile.exception.custom.ProfileIsNotCreated;
import in.nic.eform.Profile.model.ProfileAllEmployee;
import in.nic.eform.Profile.model.mapper.ProfileDto;
import in.nic.eform.Profile.service.ProfileService;
import in.nic.eform.Profile.utils.Util;

//Add controller level request mapping
@RestController
public class ProfileController {

	// configure logger at class level
	
	@Autowired
	private ProfileService profileService;

	@PostMapping(value = "/profile")
	public ResponseEntity<String> createProfileData(@Valid @RequestBody ProfileDto profile) throws ProfileIsNotCreated {
		Boolean isGovtEmp = Util.validateEmailForGovtEmployee(profile.getAuthEmail());
		if (isGovtEmp.booleanValue()) {
			for (String email : Util.aliases(profile.getAuthEmail())) {
				try {
					if (profileService.getProfileByEmail(email) != null) {
						return ResponseEntity.ok().body("Profile already exist!!");
					}
				} catch (NoRecordFoundException e) {
					continue;
				}
			}
		} else {
			try {
				if (profileService.getProfileByEmail(profile.getAuthEmail()) != null)
					return ResponseEntity.ok().body("Profile already exist!!");
			} catch (NoRecordFoundException e) {
			}
		}
		profileService.createProfile(profile);
		return ResponseEntity.ok().body(Util.SUCCESS);
	}

	@PutMapping(value = "/profile")
	public ResponseEntity<String> updateProfileData(@RequestBody @Valid ProfileDto profile) throws ProfileIsNotCreated {
		try {
			profileService.getProfileByEmail(profile.getAuthEmail());
		} catch (NoRecordFoundException e) {
			return ResponseEntity.ok().body("Profile does not exist!!");
		}

		profileService.createProfile(profile);
		return ResponseEntity.ok().body(Util.SUCCESS);
	}

	@GetMapping(path = "/profile/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProfileByEmail(@RequestParam(value = "email") String profile)
			throws NoRecordFoundException {
		boolean isExist = false;
		ProfileAllEmployee profileEmp = null;
		
		// shift this decision making logic to service layer
		
		boolean isGovtEmp = Util.validateEmailForGovtEmployee(profile);
		try { // do we really need this try block ???
			if (isGovtEmp) {
				for (String email : Util.aliases(profile)) {
					try {
						if (profileService.getProfileByEmail(email) != null) {
							profileEmp = profileService.getProfileByEmail(email);
							isExist = true;
							break;
						}
					} catch (NoRecordFoundException e) {
						continue;
					}
				}
			} else {
				try {
					if (profileService.getProfileByEmail(profile) != null)
						profileEmp = profileService.getProfileByEmail(profile);
					isExist = true;
				} catch (NoRecordFoundException e) {
					
					// Add event in catch
				}
			}

		} catch (Exception e) {

			// Add event in catch
			
		}

		if ((!isExist && isGovtEmp)) {
			return ResponseEntity.ok().body(Util.allLdapValues(profile));
		} else if ((isGovtEmp && isExist && (profileEmp != null)) || (!isGovtEmp && isExist)) {
			return ResponseEntity.ok().body(profileEmp);
		} else {
			return ResponseEntity.ok().body(Util.SOME_THING_WENT_WRONG);
		}

	}

}
