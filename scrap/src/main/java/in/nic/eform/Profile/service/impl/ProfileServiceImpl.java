package in.nic.eform.Profile.service.impl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.eform.Profile.exception.custom.NoRecordFoundException;
import in.nic.eform.Profile.exception.custom.ProfileIsNotCreated;
import in.nic.eform.Profile.model.ProfileAllEmployee;
import in.nic.eform.Profile.model.mapper.HodDetailsDto;
import in.nic.eform.Profile.model.mapper.ProfileDto;
import in.nic.eform.Profile.repository.ProfileAllEmployeeRepository;
import in.nic.eform.Profile.service.ProfileService;
import in.nic.eform.Profile.utils.Util;

@Service
public class ProfileServiceImpl implements ProfileService {

	private static final String SUCCESS = "Success";
	@Autowired
	private ProfileAllEmployeeRepository profileAllEmployeeRepository;

	@PersistenceContext
	EntityManager em;

	@Override
	public String createProfile(ProfileDto profileModel) throws ProfileIsNotCreated {
		ModelMapper modelMapper = new ModelMapper();
		ProfileAllEmployee profileAllEmployee = modelMapper.map(profileModel, ProfileAllEmployee.class);
		if (Util.validateEmailForGovtEmployee(profileAllEmployee.getHodEmail()).booleanValue()) {
			HodDetailsDto hodDetail = Util.getHodValues(profileModel.getHodEmail());
			profileAllEmployee.setHodName(hodDetail.getFirstName());
			profileAllEmployee.setHodDesig(hodDetail.getDesignation());
			profileAllEmployee.setHodMobile(hodDetail.getMobile());
			profileAllEmployee.setHodTelephone(hodDetail.getTelephoneNumber());
		}
		ProfileAllEmployee empData = profileAllEmployeeRepository.save(profileAllEmployee);
		if (empData != null) {
			return SUCCESS;
		} else {
			throw new ProfileIsNotCreated(Util.SOME_THING_WENT_WRONG);
		}

	}

	@Override
	public ProfileAllEmployee getProfileByEmail(String email) throws NoRecordFoundException {
		ProfileAllEmployee profile = profileAllEmployeeRepository.findByEmail(email);
		if (profile == null) {
			throw new NoRecordFoundException("Profile for Email doesn't exists" + email);
		}
		return profile;
	}

}
