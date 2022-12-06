package in.nic.eform.relayip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapFinalAuditTrackDTO;
import in.nic.eform.relayip.dto.RelayIPFinalAuditTrackDTO;

@Repository
public interface RelayIPFinalAuditTrackListRepo extends CrudRepository<RelayIPFinalAuditTrackDTO, Integer>{
	
	List<RelayIPFinalAuditTrackDTO> findByRegistrationno(String regNo);
}

