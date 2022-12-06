package in.nic.eform.imappop.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.imappop.dto.ImappopFinalAuditTrackDTO;
@Repository
public interface ImappopFinalAuditTrackListRepo extends CrudRepository<ImappopFinalAuditTrackDTO, Integer>{
	List<ImappopFinalAuditTrackDTO> findByRegistrationno(String regNo);
	
}
