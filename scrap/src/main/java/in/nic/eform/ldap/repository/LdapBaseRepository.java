package in.nic.eform.ldap.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapBaseDTO;

@Repository
public interface LdapBaseRepository extends CrudRepository<LdapBaseDTO, Integer>{
	
	LdapBaseDTO findByRegistrationno(String regNo);
	
	int countByregistrationno(String registration_no);
	
	List<LdapBaseDTO> findByDatetimeLike(String d);
}
