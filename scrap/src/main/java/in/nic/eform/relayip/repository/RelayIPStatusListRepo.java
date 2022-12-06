package in.nic.eform.relayip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import in.nic.eform.ldap.dto.LdapStatusDTO;
import in.nic.eform.relayip.dto.RelayIPStatusDTO;

public interface RelayIPStatusListRepo extends CrudRepository<RelayIPStatusDTO, Integer>{
	
	List<RelayIPStatusDTO> findByStatregno(String regNo);
}

