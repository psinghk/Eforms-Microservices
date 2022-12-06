package in.nic.eform.ldapip.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import in.nic.eform.ldapip.dto.LdapIPEmpCoordDTO;


public interface LdapIPEmpCoordRepo extends CrudRepository<LdapIPEmpCoordDTO, Integer>{
	
	List<LdapIPEmpCoordDTO> findByEmpcoordemailAndEmpadminemailAndEmpdept(String HIMACHAL_DA_ADMIN,String HIMACHAL_DA_ADMIN1,String dept);

	//Set<EmpCoordDTO> findFirstByEmpcoordemail(String email);
	LdapIPEmpCoordDTO findFirstByEmpcoordemail(String email);
	
	LdapIPEmpCoordDTO findByEmpcategoryAndEmpminstateorgAndEmpdeptAndEmpstatus(String employment, String ministry, String department,String status);

	LdapIPEmpCoordDTO findFirstByEmpcoordemailAndEmpadminemail(String aliase,String aliase1);

}
