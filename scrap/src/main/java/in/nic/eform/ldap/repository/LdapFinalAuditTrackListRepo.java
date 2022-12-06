package in.nic.eform.ldap.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapFinalAuditTrackDTO;

@Repository
public interface LdapFinalAuditTrackListRepo extends CrudRepository<LdapFinalAuditTrackDTO, Integer>{
	
	List<LdapFinalAuditTrackDTO> findByRegistrationno(String regNo);
}

