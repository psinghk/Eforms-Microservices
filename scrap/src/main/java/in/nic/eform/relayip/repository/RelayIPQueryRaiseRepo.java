package in.nic.eform.relayip.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapQueryRaiseDTO;
import in.nic.eform.relayip.dto.RelayIPQueryRaiseDTO;

@Repository
public interface RelayIPQueryRaiseRepo extends CrudRepository<RelayIPQueryRaiseDTO, Integer>{
	
	List<RelayIPQueryRaiseDTO> findByQrregno(String regNo);
	
}
