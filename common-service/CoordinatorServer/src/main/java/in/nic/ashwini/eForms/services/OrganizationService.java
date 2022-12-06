package in.nic.ashwini.eForms.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.db.slave.repositories.EmailCoordinatorsRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.StateRepositoryToRead;
import in.nic.ashwini.eForms.exceptions.custom.NoRecordFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizationService {

	@Autowired
	private StateRepositoryToRead stateRepositoryToRead;

	@Autowired
	private EmailCoordinatorsRepositoryToRead organizationRepositoryToRead;

	public List<?> empCategory() throws NoRecordFoundException {
		log.info("Fetching of Ministries started!!!");
		List<?> ministryDepartmentData = organizationRepositoryToRead.findDistinctEmploymentCategory();
		
		return ministryDepartmentData;
	}
	
	public List<?> findMinistriesByEmpCategory(String empCategory) throws NoRecordFoundException {
		log.info("Fetching of Ministries started!!!");
		List<?> ministryDepartmentData = organizationRepositoryToRead.findMinistriesByEmpCategory(empCategory);
		if(ministryDepartmentData.isEmpty()) {
			log.debug("ministries don't exist for category {}",empCategory);
			throw new NoRecordFoundException("empCategory", "ministries don't exist: " + empCategory);
		}
		return ministryDepartmentData;
	}

	public List<?> findDepartmentsByMinistry(String ministry) throws NoRecordFoundException {
		log.info("Fetching of departments started!!!");
		List<String> masteData = organizationRepositoryToRead.findDepartmentsByMinistry(ministry);
		List<String> finalListOfDepartments = new ArrayList<>();
		
		for (String department : masteData) {
			if(!department.isEmpty()) {
				finalListOfDepartments.add(department);
			}
		}
		
		if (finalListOfDepartments.isEmpty()) {
			log.debug("departments don't exist for ministry {}",ministry);
			throw new NoRecordFoundException("ministry","departments don't exist: " + ministry);
		}
		return finalListOfDepartments;
	}

	public List<String> findStates() throws NoRecordFoundException {
		log.info("Fetching of states started!!!");
		List<String> stateData = stateRepositoryToRead.getDistinctStateName();
		if (stateData.isEmpty()) {
			log.debug("states don't exist");
			throw new NoRecordFoundException("states","states don't exist");
		}
		return stateData;
	}

	public List<String> findDistrictsByState(String stateName) throws NoRecordFoundException {
		log.info("Fetching of districts started!!!");
		List<String> districtList = stateRepositoryToRead.getDistinctDistrictNameByStateName(stateName);
		if (districtList.isEmpty()) {
			log.debug("districts don't exist for state {}",stateName);
			throw new NoRecordFoundException("states","districts don't exist: " + stateName);
		}
		return districtList;
	}

}
