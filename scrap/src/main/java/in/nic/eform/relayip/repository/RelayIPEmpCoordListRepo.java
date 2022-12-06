package in.nic.eform.relayip.repository;

import org.springframework.data.repository.CrudRepository;

import in.nic.eform.relayip.dto.RelayIPEmpCoordDTO;


public interface RelayIPEmpCoordListRepo extends CrudRepository<RelayIPEmpCoordDTO, Integer>{
	
	RelayIPEmpCoordDTO findFirstByEmpcoordemail(String email);
	
	

}
