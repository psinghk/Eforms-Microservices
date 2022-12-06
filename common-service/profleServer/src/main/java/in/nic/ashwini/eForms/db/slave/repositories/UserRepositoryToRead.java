package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.nic.ashwini.eForms.db.slave.entities.UserBasicFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.UserProfileFromSlave;

public interface UserRepositoryToRead extends JpaRepository<UserProfileFromSlave, Long>{
	Optional<UserProfileFromSlave> findByEmail(String email);
	Optional<UserProfileFromSlave> findFirstByRoEmailOrHodEmail(String roEmail,String hodEmail);
	List<UserBasicFromSlave> findByRoEmailOrHodEmail(String roEmail,String hodEmail);
	Optional<UserProfileFromSlave> findFirstByEmail(String email);
	Optional<UserProfileFromSlave> findFirstByMobileContaining(String mobile);
	long countByEmail(String email);
	long countByMobile(String mobile);
	@Query("select u.email from UserProfileFromSlave u where u.employment = :employment and u.ministry = :ministry")
	List<String> findByEmploymentAndMinistry(String employment, String ministry);
	@Query("select u.email from UserProfileFromSlave u where u.employment = :employment and u.ministry = :ministry and u.department = :department")
	List<String> findByEmploymentAndMinistryAndDepartment(String employment,String ministry,String department);
	@Query("select u.email from UserProfileFromSlave u where u.employment = :employment and u.state = :state and u.department = :department")
	List<String> findByEmploymentAndStateAndDepartment(String employment,String state,String department);
	@Query("select u.email from UserProfileFromSlave u where u.employment = :employment and u.organization = :organization")
	List<String> findByEmploymentAndOrganization(String employment,String organization);
	@Query("select u.email from UserProfileFromSlave u where u.employment = 'state' and u.state = :state")
	List<String> findByState(String state);
	
	@Query("select u from UserProfileFromSlave u where u.employment = :employment and u.ministry = :ministry and u.department = :department")
	List<UserProfileFromSlave> findByEmploymentAndMinistryAndDepartmentForUserProfile(String employment,String ministry,String department);
	@Query("select u.email from UserProfileFromSlave u where u.employment = :employment and u.state = :state and u.department = :department")
	List<UserProfileFromSlave> findByEmploymentAndStateAndDepartmentForUserProfile(String employment,String state,String department);
	@Query("select u.email from UserProfileFromSlave u where u.employment = :employment and u.organization = :organization")
	List<UserProfileFromSlave> findByEmploymentAndOrganizationForUserProfile(String employment,String organization);
	
}
