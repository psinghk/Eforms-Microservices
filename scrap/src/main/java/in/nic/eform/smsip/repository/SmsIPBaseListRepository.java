package in.nic.eform.smsip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.smsip.dto.SmsIPBaseDTO;


@Repository
public interface SmsIPBaseListRepository extends CrudRepository<SmsIPBaseDTO, Integer>{
	
	List<SmsIPBaseDTO> findByRegistrationno(String regNo);
	
	int countByregistrationno(String registration_no);
	
	List<SmsIPBaseDTO> findByDatetimeLike(String d);
}
