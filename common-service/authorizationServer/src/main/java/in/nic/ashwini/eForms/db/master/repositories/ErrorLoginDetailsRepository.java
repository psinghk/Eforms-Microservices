package in.nic.ashwini.eForms.db.master.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.ErrorLoginDetail;

@Repository
public interface ErrorLoginDetailsRepository extends JpaRepository<ErrorLoginDetail, Integer> {

	List<ErrorLoginDetail> findByEmailAndLoginTimeGreaterThanEqualAndRoleOrderById(String email, LocalDateTime newTime,
			String service);
	}
