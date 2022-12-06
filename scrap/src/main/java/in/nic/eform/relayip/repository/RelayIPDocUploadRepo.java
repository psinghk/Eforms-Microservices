package in.nic.eform.relayip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapDocUploadDTO;
import in.nic.eform.relayip.dto.RelayIPDocUploadDTO;

@Repository
public interface RelayIPDocUploadRepo extends CrudRepository<RelayIPDocUploadDTO, Long>,QueryByExampleExecutor<RelayIPDocUploadDTO>{

	 List<RelayIPDocUploadDTO> findByRegistrationnoAndRole(String regid,String role);
}
