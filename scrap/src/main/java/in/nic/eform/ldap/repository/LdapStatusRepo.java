package in.nic.eform.ldap.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.nic.eform.ldap.dto.LdapStatusDTO;

@Repository
public interface LdapStatusRepo extends CrudRepository<LdapStatusDTO, Integer>{
	
	//LdapStatusDTO findByStatregno(String regNo);
	LdapStatusDTO findByStatregnoAndStatforwardedto(String regNo,String role);
	
	List<LdapStatusDTO> findFirstByStatregnoAndStatforwardedtoOrderByStatid(String registration_no,String trole);

	List<LdapStatusDTO> findByStatregnoAndStatforwardedbyIsNullAndStatforwardedtoIsNullOrderByStatid(String registration_no);
	
	List<LdapStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedtoIsNullOrderByStatid(String registration_no,String srole);
	
	List<LdapStatusDTO> findByStatregnoAndStatforwardedbyAndStatforwardedto(String registration_no,String trole,String srole);
	
	List<LdapStatusDTO> findByStatregnoAndStatforwardedbyOrStatforwardedto(String registration_no, String srole,String trole);

}
