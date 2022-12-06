package in.nic.eform.ldap.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapQueryRaiseDTO;

@Repository
public interface LdapQueryRaiseRepo extends CrudRepository<LdapQueryRaiseDTO, Integer>{
	
	List<LdapQueryRaiseDTO> findByQrregno(String regNo);
	
}
