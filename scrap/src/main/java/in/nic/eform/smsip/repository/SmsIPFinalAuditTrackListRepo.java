package in.nic.eform.smsip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.smsip.dto.SmsIPFinalAuditTrackDTO;

@Repository
public interface SmsIPFinalAuditTrackListRepo extends CrudRepository<SmsIPFinalAuditTrackDTO, Integer>{
	
	List<SmsIPFinalAuditTrackDTO> findByRegistrationno(String regNo);
}

