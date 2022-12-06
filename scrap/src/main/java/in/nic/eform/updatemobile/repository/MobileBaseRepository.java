package in.nic.eform.updatemobile.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.updatemobile.dto.MobileBaseDTO;

@Repository
public interface MobileBaseRepository extends CrudRepository<MobileBaseDTO, Integer>{
	
	MobileBaseDTO findByRegistrationno(String regNo);
	
	List<MobileBaseDTO> findByregistrationno(String regid);

	int countByregistrationno(String registration_no);
	
	List<MobileBaseDTO> findByDatetimeLike(String d);
	
	//@Query(value="SELECT email FROM punjab_district_nodal_officers WHERE district = ?1", nativeQuery = true)
	//public String fetchPunjabNodalOfficers(String district);








}
