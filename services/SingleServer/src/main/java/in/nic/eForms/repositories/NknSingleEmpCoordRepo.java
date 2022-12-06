package in.nic.eForms.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import in.nic.eForms.entities.NknSingleEmpCoord;

@Repository
public interface NknSingleEmpCoordRepo extends JpaRepository<NknSingleEmpCoord, Long>{
	

	
	@Query(value="select distinct(emp_bo_id) as bo FROM employment_coordinator WHERE emp_category = ?1 AND emp_min_state_org = ?2 AND emp_dept = ?3 AND emp_bo_id IS NOT NULL", nativeQuery = true)
	public Set<String> fetchByMinistry(String employment,String ministry,String department);

	@Query(value="select distinct(emp_bo_id) as bo FROM employment_coordinator WHERE emp_category = ?1 AND emp_min_state_org = ?2  AND emp_dept = ?3 AND emp_bo_id IS NOT NULL", nativeQuery = true)
	public Set<String> fetchByState(String employment,String state,String department);
	
	@Query(value="select distinct(emp_bo_id) as bo FROM employment_coordinator WHERE emp_category = ?1 AND emp_min_state_org = ?2 AND emp_bo_id IS NOT NULL", nativeQuery = true)
	public Set<String> fetchByOrg(String employment,String organization);
	
	

	@Query(value="select distinct domain from nkn_domain", nativeQuery = true)
	public Set<String> fetchdistDomain();


}

