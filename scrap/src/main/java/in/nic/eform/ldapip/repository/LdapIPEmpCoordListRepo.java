package in.nic.eform.ldapip.repository;

import org.springframework.data.repository.CrudRepository;
import in.nic.eform.ldapip.dto.LdapIPEmpCoordDTO;


public interface LdapIPEmpCoordListRepo extends CrudRepository<LdapIPEmpCoordDTO, Integer>{
	LdapIPEmpCoordDTO findFirstByEmpcoordemail(String email);
	
	

}
