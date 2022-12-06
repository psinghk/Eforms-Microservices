package in.nic.eform.smsip.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import in.nic.eform.smsip.dto.SmsIPMailadminFormsDTO;

public interface SmsIPMailadminFormsRepo extends CrudRepository<SmsIPMailadminFormsDTO, Integer>{

	//ArrayList<String> findDistinctByMldap(char mLdap);
	List<SmsIPMailadminFormsDTO> findDistinctByMldap(String mLdap);
	
	Set<String> findFirstDistinctByMemailAndMldap(String email,char mLdap);
	
}
