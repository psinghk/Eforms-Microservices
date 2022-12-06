package in.nic.eform.ldapip.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.ldapip.dto.LdapIPQueryRaiseDTO;

@Repository
public interface LdapIPQueryRaiseRepo extends CrudRepository<LdapIPQueryRaiseDTO, Integer>{
	List<LdapIPQueryRaiseDTO> findByQrregno(String regNo);
	
}
