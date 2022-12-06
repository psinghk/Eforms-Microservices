package in.nic.eform.imappop.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.imappop.dto.ImappopBaseDTO;

@Repository
public interface ImappopBaseRepo extends CrudRepository<ImappopBaseDTO, Integer>{
	
	ImappopBaseDTO findByRegistrationno(String regNo);

	int countByregistrationno(String registration_no);
	
	List<ImappopBaseDTO> findByDatetimeLike(String d);
	
	//@Query(value="SELECT email FROM punjab_district_nodal_officers WHERE district = ?1", nativeQuery = true)
	//public String fetchPunjabNodalOfficers(String district);


	
	





}
