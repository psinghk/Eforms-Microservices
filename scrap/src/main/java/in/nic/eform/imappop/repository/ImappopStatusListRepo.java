package in.nic.eform.imappop.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.imappop.dto.ImappopStatusDTO;

@Repository
public interface ImappopStatusListRepo extends CrudRepository<ImappopStatusDTO, Integer>{
	
	List<ImappopStatusDTO> findByStatregno(String regNo);
	
}
