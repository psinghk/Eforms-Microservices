package in.nic.eform.Profile.service;

import java.util.List;

import in.nic.eform.Profile.exception.custom.NoRecordFoundException;

public interface MasterDataService {
	List<String> getMinistryList(String organization) throws NoRecordFoundException;

	List<String> getOrganizationMininstryBasedDepartments(String ministryId) throws NoRecordFoundException;

	List<String> getStateList() throws NoRecordFoundException;

	List<String> getDistrictList(String stname) throws NoRecordFoundException;

}
