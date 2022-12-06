package in.nic.eform.smsip.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.smsip.dto.SmsIPQueryRaiseDTO;

@Repository
public interface SmsIPQueryRaiseRepo extends CrudRepository<SmsIPQueryRaiseDTO, Integer>{
	
	List<SmsIPQueryRaiseDTO> findByQrregno(String regNo);
	
}
