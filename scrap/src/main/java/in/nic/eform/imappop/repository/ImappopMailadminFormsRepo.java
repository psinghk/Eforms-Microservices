package in.nic.eform.imappop.repository;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.nic.eform.imappop.dto.ImappopMailadminFormsDTO;


public interface ImappopMailadminFormsRepo extends CrudRepository<ImappopMailadminFormsDTO, Integer>{

	@Query(value="select distinct m_email FROM mailadmin_forms WHERE m_imappop = 'y'", nativeQuery = true)
	ArrayList<String> findDistinctByMimappop();
	//ArrayList<String> findDistinctByMimappop(char y);
	
	
	//Set<String> findFirstDistinctByMemailAndMimappop(String email,char mImappop);
	@Query(value="select distinct m_email FROM mailadmin_forms WHERE m_email = ?1 and m_imappop = 'y' limit 1", nativeQuery = true)
	public Set<String> findFirstDistinctByMemailAndMimappop(String email);
	
	
	
	
}
