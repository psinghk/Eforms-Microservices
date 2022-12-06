package in.nic.eform.updatemobile.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import in.nic.eform.updatemobile.dto.MobileEmpCoordDTO;

public interface MobileEmpCoordRepository extends CrudRepository<MobileEmpCoordDTO, Integer>{
	
	List<MobileEmpCoordDTO> findByEmpcoordemailAndEmpadminemailAndEmpdept(String HIMACHAL_DA_ADMIN,String HIMACHAL_DA_ADMIN1,String dept);

	Set<MobileEmpCoordDTO> findFirstByEmpcoordemail(String email);
	
	MobileEmpCoordDTO findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(String employment, String ministry, String department,String status);



}
