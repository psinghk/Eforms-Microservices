package in.nic.eform.smsip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import in.nic.eform.smsip.dto.SmsIPDocUploadDTO;

@Repository
public interface SmsIPDocUploadRepo extends CrudRepository<SmsIPDocUploadDTO, Long>,QueryByExampleExecutor<SmsIPDocUploadDTO>{

	 List<SmsIPDocUploadDTO> findByRegistrationnoAndRole(String regid,String role);
}
