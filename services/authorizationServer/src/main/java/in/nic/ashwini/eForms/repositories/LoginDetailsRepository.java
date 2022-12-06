package in.nic.ashwini.eForms.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.LoginDetail;

@Repository
public interface LoginDetailsRepository extends JpaRepository<LoginDetail, Integer> {
	List<LoginDetail> findByEmailInAndSessionIdAndStatus(Set<String> email, String sessionId, Integer status);

	List<LoginDetail> findByEmailAndStatus(String username, int i);
	List<LoginDetail> findByEmailAndStatusAndLoginTimeGreaterThanEqualOrderById(String username, int i, LocalDateTime date);

	List<LoginDetail> findByEmailAndStatusAndLoginTimeGreaterThanEqualAndRoleOrderById(String username, int i,
			LocalDateTime newTime, String service);

	LoginDetail findFirstByEmailAndStatusAndLoginTimeGreaterThanEqualAndRoleOrderByIdDesc(String email, int i,
			LocalDateTime newTime, String service);
}
