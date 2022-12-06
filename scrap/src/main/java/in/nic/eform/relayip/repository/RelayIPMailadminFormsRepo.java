package in.nic.eform.relayip.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

import in.nic.eform.ldap.dto.LdapMailadminFormsDTO;
import in.nic.eform.relayip.dto.RelayIPMailadminFormsDTO;

public interface RelayIPMailadminFormsRepo extends CrudRepository<RelayIPMailadminFormsDTO, Integer>{

	//ArrayList<String> findDistinctByMldap(char mLdap);
	List<RelayIPMailadminFormsDTO> findDistinctByMip(String mLdap);
	
	Set<String> findFirstDistinctByMemailAndMldap(String email,char mLdap);
	
}
