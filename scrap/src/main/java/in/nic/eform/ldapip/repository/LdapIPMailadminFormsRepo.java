package in.nic.eform.ldapip.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

import in.nic.eform.ldapip.dto.LdapIPMailadminFormsDTO;

public interface LdapIPMailadminFormsRepo extends CrudRepository<LdapIPMailadminFormsDTO, Integer>{

	//ArrayList<String> findDistinctByMldap(char mLdap);
	List<LdapIPMailadminFormsDTO> findDistinctByMldap(String mLdap);
	Set<String> findFirstDistinctByMemailAndMldap(String email,char mLdap);
	
}
