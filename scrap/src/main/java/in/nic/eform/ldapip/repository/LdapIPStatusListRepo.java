package in.nic.eform.ldapip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import in.nic.eform.ldapip.dto.LdapIPStatusDTO;


public interface LdapIPStatusListRepo extends CrudRepository<LdapIPStatusDTO, Integer>{
	List<LdapIPStatusDTO> findByStatregno(String regNo);
}

