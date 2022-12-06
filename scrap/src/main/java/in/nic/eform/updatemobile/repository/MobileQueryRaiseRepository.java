package in.nic.eform.updatemobile.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.updatemobile.dto.MobileQueryRaiseDTO;

@Repository
public interface MobileQueryRaiseRepository extends CrudRepository<MobileQueryRaiseDTO, Integer>{
	
	List<MobileQueryRaiseDTO> findByQrregno(String regNo);
	
}
