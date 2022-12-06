package in.nic.eform.updatemobile.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.updatemobile.dto.MobileStatusDTO;

@Repository
public interface MobileStatusListRepository extends CrudRepository<MobileStatusDTO, Integer>{
	
	List<MobileStatusDTO> findByStatregno(String registration_no);

	
}
