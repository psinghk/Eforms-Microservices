package in.nic.eform.ldapip.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapStatusDTO;
import in.nic.eform.ldapip.dto.LdapIPStatusDTO;

@Repository
public interface LdapIPStatusRepo extends CrudRepository<LdapIPStatusDTO, Integer>{
	
	LdapIPStatusDTO findByStatregno(String regNo);
	
	List<LdapIPStatusDTO> findFirstByStatregnoAndStatforwardedtoOrderByStatid(String registration_no,String trole);

	List<LdapIPStatusDTO> findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(String registration_no);
	
	List<LdapIPStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(String registration_no,String srole);
	
	List<LdapIPStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedto(String registration_no,String trole,String srole);
	
	List<LdapIPStatusDTO> findByStatregnoAndStatforwardedbyOrStatforwardedto(String registration_no, String srole,String trole);

}
