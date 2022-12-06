package in.nic.eform.smsip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.smsip.dto.SmsIPStatusDTO;

@Repository
public interface SmsIPStatusRepo extends CrudRepository<SmsIPStatusDTO, Integer>{
	
	SmsIPStatusDTO findByStatregno(String regNo);
	
	List<SmsIPStatusDTO> findFirstByStatregnoAndStatforwardedtoOrderByStatid(String registration_no,String trole);

	List<SmsIPStatusDTO> findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(String registration_no);
	
	List<SmsIPStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(String registration_no,String srole);
	
	List<SmsIPStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedto(String registration_no,String trole,String srole);
	
	List<SmsIPStatusDTO> findByStatregnoAndStatforwardedbyOrStatforwardedto(String registration_no, String srole,String trole);

}
