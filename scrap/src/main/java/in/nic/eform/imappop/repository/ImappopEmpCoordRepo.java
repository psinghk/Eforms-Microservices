package in.nic.eform.imappop.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import in.nic.eform.imappop.dto.ImappopEmpCoordDTO;

public interface ImappopEmpCoordRepo extends CrudRepository<ImappopEmpCoordDTO, Integer>{
	
	List<ImappopEmpCoordDTO> findByEmpcoordemailAndEmpadminemailAndEmpdept(String HIMACHAL_DA_ADMIN,String HIMACHAL_DA_ADMIN1,String dept);

	Set<ImappopEmpCoordDTO> findFirstByEmpcoordemail(String email);
	
	ImappopEmpCoordDTO findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(String employment, String ministry, String department,String status);



}
