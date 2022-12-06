package in.nic.eform.Profile.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.eform.Profile.model.MinistryDepartment;

@Repository
public interface MinistryDepartmentRespository extends CrudRepository<MinistryDepartment, Long> {

	// Try to replace this Query with QueryMethod.
	
	@Query(value = "select distinct minDeptData.empMinistryStateOrganization from MinistryDepartment minDeptData where minDeptData.empCategory = :empCategory order by minDeptData.empMinistryStateOrganization")
	List<String> findByEmpCategory(@Param(value = "empCategory") String empCategory);

	@Query(value = "select distinct minDeptData.empDepartment from MinistryDepartment minDeptData where minDeptData.empMinistryStateOrganization = :ministry order by minDeptData.empDepartment")
	List<String> findByMinistry(@Param(value = "ministry") String ministry);
}
