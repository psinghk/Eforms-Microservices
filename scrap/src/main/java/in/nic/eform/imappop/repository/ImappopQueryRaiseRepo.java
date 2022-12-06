package in.nic.eform.imappop.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.imappop.dto.ImappopQueryRaiseDTO;

@Repository
public interface ImappopQueryRaiseRepo extends CrudRepository<ImappopQueryRaiseDTO, Integer>{
	
	List<ImappopQueryRaiseDTO> findByQrregno(String regNo);
	
}
