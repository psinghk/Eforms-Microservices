package in.nic.eform.relayip.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import in.nic.eform.relayip.dto.RelayIPEmpCoordDTO;

public interface RelayIPEmpCoordRepo extends CrudRepository<RelayIPEmpCoordDTO, Integer>{
	
	List<RelayIPEmpCoordDTO> findByEmpcoordemailAndEmpadminemailAndEmpdept(String HIMACHAL_DA_ADMIN,String HIMACHAL_DA_ADMIN1,String dept);

	//Set<EmpCoordDTO> findFirstByEmpcoordemail(String email);
	RelayIPEmpCoordDTO findFirstByEmpcoordemail(String email);
	
	RelayIPEmpCoordDTO findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(String employment, String ministry, String department,String status);

	RelayIPEmpCoordDTO findFirstByEmpcoordemailAndEmpadminemail(String aliase,String aliase1);

}
