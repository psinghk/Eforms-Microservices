package in.nic.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.nic.eForms.entities.SingleEmailBase;

@Repository
public interface SingleBaseRepo extends JpaRepository<SingleEmailBase, Long>{
	
	SingleEmailBase findByRegistrationNo(String registrationNo);
	List<SingleEmailBase> findByDatetimeLike(String datetime);
	

	@Query(value = "select registration_no from single_registration where date(datetime)=date(now()) group by registration_no desc limit 1", nativeQuery = true)
	String findLatestRegistrationNo();
	
	@Query(value="select distinct(emp_bo_id) as bo FROM employment_coordinator WHERE emp_category = ?1 AND emp_min_state_org = ?2 AND emp_dept = ?3 AND emp_bo_id IS NOT NULL", nativeQuery = true)
	public String fetchPunjabNodalOfficers(String district);
	
	//@Query(value="SELECT email FROM punjab_district_nodal_officers WHERE district = ?1", nativeQuery = true)
	//public String fetchPunjabNodalOfficers(String district);
}
