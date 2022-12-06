package in.nic.eform.updatemobile.repository;

import java.util.ArrayList;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import in.nic.eform.updatemobile.dto.MobileMailadminFormsDTO;


public interface MobileMailadminFormsRepository extends CrudRepository<MobileMailadminFormsDTO, Integer>{

	//@Query(value="select distinct m_email FROM mailadmin_forms WHERE m_mobile = 'y'", nativeQuery = true)
					//ArrayList<String> findDistinctByMmobile();
//	//ArrayList<String> findDistinctByMimappop(char y);
//	
//	
//	//Set<String> findFirstDistinctByMemailAndMimappop(String email,char mImappop);
//	@Query(value="select distinct m_email FROM mailadmin_forms WHERE m_email = ?1 and m_mobile = 'y' limit 1", nativeQuery = true)
//	public Set<String> findFirstDistinctByMemailAndMmobile(String email,String mobile);
//	
				//ArrayList<String> findDistinctByMmobile(char mmobile);
	
	//Set<String> findFirstDistinctByMemailAndMldap(String email,char mLdap);
					//Set<String> findFirstDistinctByMemailAndMmobile(String email, char mobile);
	
	//*******************Start fetch************************
	
	@Query(value="select distinct m_email FROM mailadmin_forms WHERE m_imappop = 'y'", nativeQuery = true)
	ArrayList<String> findDistinctByMmobile();
	//ArrayList<String> findDistinctByMimappop(char y);
	
	@Query(value="select distinct m_email FROM mailadmin_forms WHERE m_email = ?1 and m_imappop = 'y' limit 1", nativeQuery = true)
	public Set<String> findFirstDistinctByMemailAndMmobile(String email);
	
	//*************************EOF***************************
	
}
