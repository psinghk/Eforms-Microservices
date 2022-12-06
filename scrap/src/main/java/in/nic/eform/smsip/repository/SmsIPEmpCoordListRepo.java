package in.nic.eform.smsip.repository;

import org.springframework.data.repository.CrudRepository;
import in.nic.eform.smsip.dto.SmsIPEmpCoordDTO;


public interface SmsIPEmpCoordListRepo extends CrudRepository<SmsIPEmpCoordDTO, Integer>{
	
	SmsIPEmpCoordDTO findFirstByEmpcoordemail(String email);
	
	

}
