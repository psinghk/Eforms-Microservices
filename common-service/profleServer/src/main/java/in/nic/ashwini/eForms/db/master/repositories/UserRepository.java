package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.nic.ashwini.eForms.db.master.entities.UserBasic;
import in.nic.ashwini.eForms.db.master.entities.UserProfile;


public interface UserRepository extends JpaRepository<UserProfile, Long>{
	Optional<UserProfile> findByEmail(String email);
	Optional<UserProfile> findFirstByRoEmailOrHodEmail(String roEmail,String hodEmail);
	List<UserBasic> findByRoEmailOrHodEmail(String roEmail,String hodEmail);
	Optional<UserProfile> findFirstByEmail(String email);
	Optional<UserProfile> findFirstByMobileContaining(String mobile);
	long countByEmail(String email);
	long countByMobile(String mobile);
	@Query("select u.email from UserProfile u where u.employment = :employment and u.ministry = :ministry")
	List<String> findByEmploymentAndMinistry(String employment, String ministry);
	@Query("select u.email from UserProfile u where u.employment = :employment and u.ministry = :ministry and u.department = :department")
	List<String> findByEmploymentAndMinistryAndDepartment(String employment,String ministry,String department);
	@Query("select u.email from UserProfile u where u.employment = :employment and u.state = :state and u.department = :department")
	List<String> findByEmploymentAndStateAndDepartment(String employment,String state,String department);
	@Query("select u.email from UserProfile u where u.employment = :employment and u.organization = :organization")
	List<String> findByEmploymentAndOrganization(String employment,String organization);
	@Query("select u.email from UserProfile u where u.employment = 'state' and u.state = :state")
	List<String> findByState(String state);
	
	@Query("select u from UserProfile u where u.employment = :employment and u.ministry = :ministry and u.department = :department")
	List<UserProfile> findByEmploymentAndMinistryAndDepartmentForUserProfile(String employment,String ministry,String department);
	@Query("select u.email from UserProfile u where u.employment = :employment and u.state = :state and u.department = :department")
	List<UserProfile> findByEmploymentAndStateAndDepartmentForUserProfile(String employment,String state,String department);
	@Query("select u.email from UserProfile u where u.employment = :employment and u.organization = :organization")
	List<UserProfile> findByEmploymentAndOrganizationForUserProfile(String employment,String organization);
	
}
