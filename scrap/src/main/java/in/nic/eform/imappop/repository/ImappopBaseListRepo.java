package in.nic.eform.imappop.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.imappop.dto.GeneratePdfBaseDTO;
import in.nic.eform.imappop.dto.ImappopBaseDTO;

@Repository
public interface ImappopBaseListRepo extends CrudRepository<ImappopBaseDTO, Integer>{
	
	List<ImappopBaseDTO> findByRegistrationno(String regid);





}
