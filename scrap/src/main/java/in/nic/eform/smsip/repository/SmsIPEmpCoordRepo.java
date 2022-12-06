package in.nic.eform.smsip.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import in.nic.eform.smsip.dto.SmsIPEmpCoordDTO;

public interface SmsIPEmpCoordRepo extends CrudRepository<SmsIPEmpCoordDTO, Integer>{
	
	List<SmsIPEmpCoordDTO> findByEmpcoordemailAndEmpadminemailAndEmpdept(String HIMACHAL_DA_ADMIN,String HIMACHAL_DA_ADMIN1,String dept);

	//Set<EmpCoordDTO> findFirstByEmpcoordemail(String email);
	SmsIPEmpCoordDTO findFirstByEmpcoordemail(String email);
	
	SmsIPEmpCoordDTO findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(String employment, String ministry, String department,String status);

	SmsIPEmpCoordDTO findFirstByEmpcoordemailAndEmpadminemail(String aliase,String aliase1);

}
