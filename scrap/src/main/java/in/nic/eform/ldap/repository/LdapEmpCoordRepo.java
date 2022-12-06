package in.nic.eform.ldap.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import in.nic.eform.ldap.dto.LdapEmpCoordDTO;


public interface LdapEmpCoordRepo extends CrudRepository<LdapEmpCoordDTO, Integer>{
	
	List<LdapEmpCoordDTO> findByEmpcoordemailAndEmpadminemailAndEmpdept(String HIMACHAL_DA_ADMIN,String HIMACHAL_DA_ADMIN1,String dept);

	//Set<EmpCoordDTO> findFirstByEmpcoordemail(String email);
	LdapEmpCoordDTO findFirstByEmpcoordemail(String email);
	
	LdapEmpCoordDTO findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(String employment, String ministry, String department,String status);

	LdapEmpCoordDTO findFirstByEmpcoordemailAndEmpadminemail(String aliase,String aliase1);

}
