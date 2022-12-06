package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.EmailCoordinators;

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
	
	@Query(value = "select distinct org.employmentCategory from EmailCoordinators org")
	List<String> findDistinctEmploymentCategory();
	
	@Query(value = "select distinct org.ministry from EmailCoordinators org where org.employmentCategory = :empCategory order by org.ministry")
	List<String> findMinistriesByEmpCategory(@Param(value = "empCategory") String empCategory);
	
	@Query(value = "select distinct org.department from EmailCoordinators org where org.ministry = :ministry order by org.department")
	List<String> findDepartmentsByMinistry(@Param(value = "ministry") String ministry);
	
	@Query(value = "select distinct org.employmentCategory from EmailCoordinators org")
	List<String> findDinstinctCategories();
	
	//update by sunny
	@Query(value = "select distinct org.domain from EmailCoordinators org where org.employmentCategory = :empCategory and org.ministry = :ministry and org.department = :empDept  order by org.domain")
	List<String> findByDomain(@Param(value = "empCategory") String empCategory,@Param(value = "ministry") String ministry,@Param(value = "empDept") String empDept);
	
	@Query(value = "select distinct org.bo from EmailCoordinators org where org.employmentCategory = :empCategory and org.ministry = :ministry and org.department = :empDept  order by org.bo")
	List<String> findBOs(@Param(value = "empCategory") String empCategory,@Param(value = "ministry") String ministry,@Param(value = "empDept") String empDept);
	
	@Query(value = "select distinct org.domain from EmailCoordinators org where org.employmentCategory = :empCategory and org.ministry = :ministry  order by org.domain")
	List<String> findByDomain1(@Param(value = "empCategory") String empCategory,@Param(value = "ministry") String ministry);

	@Query(value = "select distinct org.bo from EmailCoordinators org where org.employmentCategory = :empCategory and org.ministry = :ministry order by org.bo")
	List<String> findBOs(@Param(value = "empCategory") String empCategory,@Param(value = "ministry") String ministry);
	

@Query(value = "update employment_coordinator set emp_dept = :emp_new_dept where emp_category = :emp_category and emp_min_state_org = :emp_min_state_org and emp_dept= :emp_dept",nativeQuery = true)
	int updateEmploymentCoordinator(@Param(value = "emp_new_dept") String emp_new_dept,@Param(value = "emp_category") String emp_category,@Param(value = "emp_min_state_org") String emp_min_state_org,@Param(value = "emp_dept") String emp_dept);

	List<EmailCoordinators> findByEmploymentCategoryAndMinistryAndDepartment(
		String employmentCategory, String ministry, String department);
	
	Page<EmailCoordinators> findByEmploymentCategory(String employmentCategory, Pageable sortedByIdDesc);

	@Query(value = "select * from  employment_coordinator where emp_category= ?1 and emp_min_state_org = ?2 ", nativeQuery = true)
	Page<EmailCoordinators> findByOrgTypeAndMinType(String employmentCategory, String ministry, Pageable sortedByIdDesc);

	@Query(value = "select * from  employment_coordinator where emp_category = ?1 and emp_min_state_org = ?2 and emp_dept = ?3  ", nativeQuery = true)
	Page<EmailCoordinators> fetchFinalList(String employmentCategory, String ministry, String dept,
			Pageable sortedByIdDesc);

	@Query(value = "SELECT * FROM employment_coordinator where  emp_category like %?1% || emp_min_state_org like %?1% || emp_dept like %?1% || emp_coord_name like %?1% || emp_coord_email like %?1%", nativeQuery = true)
	Page<EmailCoordinators> findSearchData(String search , Pageable sortedByIdDesc);

	@Query(value = "select * from employment_coordinator where emp_category = ?1 and (emp_min_state_org like %?2% || emp_dept like %?2% || emp_coord_name like %?2% || emp_coord_email like %?2%) " , nativeQuery = true)
	Page<EmailCoordinators> findSearchOrgData(String orgType, String search, Pageable sortedByIdDesc);

	@Query(value = "select * from employment_coordinator where emp_category = ?1 and emp_min_state_org = ?2 and (emp_dept like %?3% ||  emp_coord_name like %?3% || emp_coord_email like %?3% )" , nativeQuery = true)
	Page<EmailCoordinators> findSearchMinOrgData(String orgType, String minType,String search, Pageable sortedByIdDesc);

	
	@Query(value = "select * from employment_coordinator where emp_category = ?1 and emp_min_state_org = ?2 and emp_dept = ?3 and (emp_coord_name like %?4% || emp_coord_email like %?4%)", nativeQuery = true)
	Page<EmailCoordinators> findSearchMinOrgDeptData(String orgType, String minType, String depType, String search,
			Pageable sortedByIdDesc);

	List<EmailCoordinators> findByEmploymentCategoryAndMinistry(String emp_category, String emp_min_state_org);
}


//select distinct emp_domain from employment_coordinator where emp_category = ? and emp_min_state_org =? and emp_dept =? order by emp_domain asc
//"select distinct emp_domain from employment_coordinator where emp_category = ? and emp_min_state_org = ? order by emp_domain asc";
