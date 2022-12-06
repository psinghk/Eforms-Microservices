package in.nic.eform.ldap.repository;

import org.springframework.data.repository.CrudRepository;
import in.nic.eform.ldap.dto.LdapEmpCoordDTO;


public interface LdapEmpCoordListRepo extends CrudRepository<LdapEmpCoordDTO, Integer>{
	LdapEmpCoordDTO findFirstByEmpcoordemail(String email);
	
	

}
