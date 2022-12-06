package in.nic.ashwini.eForms.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.MobileEmpCoord;

@Repository
public interface MobileEmpCoordRepo extends JpaRepository<MobileEmpCoord, Long>{
	

	
	@Query(value="select distinct(emp_bo_id) as bo FROM employment_coordinator WHERE emp_category = ?1 AND emp_min_state_org = ?2 AND emp_dept = ?3 AND emp_bo_id IS NOT NULL", nativeQuery = true)
	public Set<String> fetchByMinistry(String employment,String ministry,String department);

	@Query(value="select distinct(emp_bo_id) as bo FROM employment_coordinator WHERE emp_category = ?1 AND emp_min_state_org = ?2  AND emp_dept = ?3 AND emp_bo_id IS NOT NULL", nativeQuery = true)
	public Set<String> fetchByState(String employment,String state,String department);
	
	@Query(value="select distinct(emp_bo_id) as bo FROM employment_coordinator WHERE emp_category = ?1 AND emp_min_state_org = ?2 AND emp_bo_id IS NOT NULL", nativeQuery = true)
	public Set<String> fetchByOrg(String employment,String organization);
}

