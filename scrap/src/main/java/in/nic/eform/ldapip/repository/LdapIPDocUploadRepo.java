package in.nic.eform.ldapip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import in.nic.eform.ldapip.dto.LdapIPDocUploadDTO;


@Repository
public interface LdapIPDocUploadRepo extends CrudRepository<LdapIPDocUploadDTO, Long>,QueryByExampleExecutor<LdapIPDocUploadDTO>{
	 List<LdapIPDocUploadDTO> findByRegistrationnoAndRole(String regid,String role);
}
