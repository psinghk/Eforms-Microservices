package in.nic.eform.updatemobile.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.updatemobile.dto.MobileBaseDTO;

@Repository
public interface MobileBaseListRepository extends CrudRepository<MobileBaseDTO, Integer>{
	
	List<MobileBaseDTO> findByRegistrationno(String regid);






}
