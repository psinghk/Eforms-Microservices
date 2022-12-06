package in.nic.eform.ldap.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

import in.nic.eform.ldap.dto.LdapMailadminFormsDTO;

public interface LdapMailadminFormsRepo extends CrudRepository<LdapMailadminFormsDTO, Integer>{

	//ArrayList<String> findDistinctByMldap(char mLdap);
	List<LdapMailadminFormsDTO> findDistinctByMldap(String mLdap);
	
	Set<String> findFirstDistinctByMemailAndMldap(String email,char mLdap);
	
}
