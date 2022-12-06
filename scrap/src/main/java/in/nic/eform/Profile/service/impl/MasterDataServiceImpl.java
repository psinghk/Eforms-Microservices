package in.nic.eform.Profile.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.eform.Profile.exception.custom.NoRecordFoundException;
import in.nic.eform.Profile.repository.MinistryDepartmentRespository;
import in.nic.eform.Profile.repository.StateRepository;
import in.nic.eform.Profile.service.MasterDataService;

@Service
public class MasterDataServiceImpl implements MasterDataService {

	
	// integrate logger
	
	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private MinistryDepartmentRespository ministryDepartmentRepository;

	@Override
	public List<String> getMinistryList(String empCategory) throws NoRecordFoundException {

		List<String> ministryDepartmentData = ministryDepartmentRepository.findByEmpCategory(empCategory);

		if (ministryDepartmentData.isEmpty()) {
			throw new NoRecordFoundException("ministry doesn't exists: " + empCategory);
		}

		return ministryDepartmentData;

	}

	@Override
	public List<String> getOrganizationMininstryBasedDepartments(String minstry) throws NoRecordFoundException {
		List<String> masteData = ministryDepartmentRepository.findByMinistry(minstry);
		if (masteData.isEmpty()) {
			throw new NoRecordFoundException("department doesn't exists: " + minstry);
		}
		return masteData;
	}

	@Override
	public List<String> getStateList() throws NoRecordFoundException {
		List<String> stateData = stateRepository.findState();
		if (stateData.isEmpty()) {
			throw new NoRecordFoundException("state doesn't exists");
		}
		return stateData;
	}

	@Override
	public List<String> getDistrictList(String stateName) throws NoRecordFoundException {
		List<String> districtList = stateRepository.findDistrictByState(stateName);
		if (districtList == null) {
			throw new NoRecordFoundException("district doesn't exists: " + stateName);
		}
		return districtList;
	}

}
