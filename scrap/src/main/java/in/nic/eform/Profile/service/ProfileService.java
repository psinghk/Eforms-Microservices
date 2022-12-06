package in.nic.eform.Profile.service;
import in.nic.eform.Profile.exception.custom.NoRecordFoundException;
import in.nic.eform.Profile.exception.custom.ProfileIsNotCreated;
import in.nic.eform.Profile.model.ProfileAllEmployee;
import in.nic.eform.Profile.model.mapper.ProfileDto;

public interface ProfileService {
	String createProfile(ProfileDto profile) throws ProfileIsNotCreated;
	ProfileAllEmployee getProfileByEmail(String email) throws NoRecordFoundException;

	
}
