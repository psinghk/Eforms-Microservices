package in.nic.ashwini.eForms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.nic.ashwini.eForms.entities.UserProfile;
import in.nic.ashwini.eForms.entities.projections.UserBasic;

public interface UserRepository extends JpaRepository<UserProfile, Long>{
	Optional<UserProfile> findByEmail(String email);
	Optional<UserProfile> findFirstByRoEmailOrHodEmail(String roEmail,String hodEmail);
	List<UserBasic> findByRoEmailOrHodEmail(String roEmail,String hodEmail);
	Optional<UserProfile> findFirstByEmail(String email);
	Optional<UserProfile> findFirstByMobileContaining(String mobile);
	long countByEmail(String email);
	long countByMobile(String mobile);
}
