package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.EmailCoordinators;

@Repository
public interface EmailCoordinatorsRepository extends JpaRepository<EmailCoordinators, Integer> {
	List<EmailCoordinators> findByEmailAndStatusAndVpnIpContaining(String email, String status, String vpnIp);

	@Query(value = "select emp_coord_email from employment_coordinator where emp_coord_email!='kaushal.shailender@nic.in' and emp_admin_email='kaushal.shailender@nic.in' and emp_dept= :department", nativeQuery = true)
	List<String> findHimachalCoordinators(@Param("department") String department);

	List<EmailCoordinators> findByEmploymentCategoryAndMinistryAndDepartmentAndStatus(
			String employmentCategory, String ministry, String department, String status);

	List<EmailCoordinators> findByEmploymentCategoryAndMinistryAndStatus(
			String employmentCategory, String ministry, String status);
	
	List<EmailCoordinators> findByBo(String bo);
	
	@Query(value = "select distinct org.ministry from EmailCoordinators org where org.employmentCategory = :empCategory order by org.ministry")
	List<String> findMinistriesByEmpCategory(@Param(value = "empCategory") String empCategory);
	
	@Query(value = "select distinct org.department from EmailCoordinators org where org.ministry = :ministry order by org.department")
	List<String> findDepartmentsByMinistry(@Param(value = "ministry") String ministry);
	
	@Query(value = "select distinct org.department from EmailCoordinators org where org.employmentCategory = :empCategory and org.ministry = :ministry order by org.department")
	List<String> findDepartmentsByMinistryAndCategory(@Param(value = "empCategory") String empCategory, @Param(value = "ministry") String ministry);
	
	@Query(value = "select distinct org.employmentCategory from EmailCoordinators org")
	List<String> findDinstinctCategories();
	
	//update by sunny
	@Query(value = "select distinct org.domain from EmailCoordinators org where org.employmentCategory = :empCategory and org.ministry = :ministry and org.department = :empDept  order by org.domain")
	List<String> findByDomain(@Param(value = "empCategory") String empCategory,@Param(value = "ministry") String ministry,@Param(value = "empDept") String empDept);
	
	@Query(value = "select distinct org.domain from EmailCoordinators org where org.employmentCategory = :empCategory and org.ministry = :ministry  order by org.domain")
	List<String> findByDomain1(@Param(value = "empCategory") String empCategory,@Param(value = "ministry") String ministry);
}
//select distinct emp_domain from employment_coordinator where emp_category = ? and emp_min_state_org =? and emp_dept =? order by emp_domain asc
//"select distinct emp_domain from employment_coordinator where emp_category = ? and emp_min_state_org = ? order by emp_domain asc";
