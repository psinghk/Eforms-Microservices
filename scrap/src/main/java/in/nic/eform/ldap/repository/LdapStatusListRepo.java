package in.nic.eform.ldap.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import in.nic.eform.ldap.dto.LdapStatusDTO;

public interface LdapStatusListRepo extends CrudRepository<LdapStatusDTO, Integer>{
	
	List<LdapStatusDTO> findByStatregno(String regNo);
}

