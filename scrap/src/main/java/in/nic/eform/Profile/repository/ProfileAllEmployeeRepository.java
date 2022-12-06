package in.nic.eform.Profile.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.eform.Profile.model.ProfileAllEmployee;

@Repository
public interface ProfileAllEmployeeRepository extends CrudRepository<ProfileAllEmployee, Long> {

	// Try to replace this Query with QueryMethod.
	
	@Query(value = "select profile from ProfileAllEmployee profile where profile.authEmail = :email")
	ProfileAllEmployee findByEmail(@Param(value = "email") String email);

}
