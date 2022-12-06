package in.nic.eform.smsip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import in.nic.eform.smsip.dto.SmsIPStatusDTO;

public interface SmsIPStatusListRepo extends CrudRepository<SmsIPStatusDTO, Integer>{
	
	List<SmsIPStatusDTO> findByStatregno(String regNo);
}

