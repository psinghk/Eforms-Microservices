package in.nic.eform.updatemobile.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.updatemobile.dto.MobileFinalAuditTrackDTO;
@Repository
public interface MobileFinalAuditTrackListRepository extends CrudRepository<MobileFinalAuditTrackDTO, Integer>{
	
	List<MobileFinalAuditTrackDTO> findByRegistrationno(String regNo);
	
	
	
}
