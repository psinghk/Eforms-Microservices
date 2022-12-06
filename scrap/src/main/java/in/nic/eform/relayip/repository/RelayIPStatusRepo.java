package in.nic.eform.relayip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapStatusDTO;
import in.nic.eform.relayip.dto.RelayIPStatusDTO;

@Repository
public interface RelayIPStatusRepo extends CrudRepository<RelayIPStatusDTO, Integer>{
	
	RelayIPStatusDTO findByStatregno(String regNo);
	
	List<RelayIPStatusDTO> findFirstByStatregnoAndStatforwardedtoOrderByStatid(String registration_no,String trole);

	List<RelayIPStatusDTO> findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(String registration_no);
	
	List<RelayIPStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(String registration_no,String srole);
	
	List<RelayIPStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedto(String registration_no,String trole,String srole);
	
	List<RelayIPStatusDTO> findByStatregnoAndStatforwardedbyOrStatforwardedto(String registration_no, String srole,String trole);

}
