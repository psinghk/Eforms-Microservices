package in.nic.eform.ldapip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldapip.dto.LdapIPBaseDTO;


@Repository
public interface LdapIPBaseRepository extends CrudRepository<LdapIPBaseDTO, Integer>{
	
	LdapIPBaseDTO findByRegistrationno(String regNo);
	
	int countByregistrationno(String registration_no);
	
	List<LdapIPBaseDTO> findByDatetimeLike(String d);
}
