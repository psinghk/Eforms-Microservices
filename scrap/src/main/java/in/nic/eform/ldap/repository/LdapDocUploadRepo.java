package in.nic.eform.ldap.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapDocUploadDTO;

@Repository
public interface LdapDocUploadRepo extends CrudRepository<LdapDocUploadDTO, Long>,QueryByExampleExecutor<LdapDocUploadDTO>{

	 List<LdapDocUploadDTO> findByRegistrationnoAndRole(String regid,String role);
}
