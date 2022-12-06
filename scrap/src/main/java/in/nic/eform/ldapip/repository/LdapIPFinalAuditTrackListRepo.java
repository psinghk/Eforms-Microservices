package in.nic.eform.ldapip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.ldapip.dto.LdapIPFinalAuditTrackDTO;


@Repository
public interface LdapIPFinalAuditTrackListRepo extends CrudRepository<LdapIPFinalAuditTrackDTO, Integer>{
	List<LdapIPFinalAuditTrackDTO> findByRegistrationno(String regNo);
}

