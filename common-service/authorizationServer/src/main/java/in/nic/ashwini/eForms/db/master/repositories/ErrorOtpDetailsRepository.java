package in.nic.ashwini.eForms.db.master.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.ErrorOtpDetail;

@Repository
public interface ErrorOtpDetailsRepository extends JpaRepository<ErrorOtpDetail, Integer> {

	List<ErrorOtpDetail> findByEmailAndLoginTimeGreaterThanEqualAndRoleOrderById(String email, LocalDateTime newTime,
			String service);

	List<ErrorOtpDetail> findByMobileAndLoginTimeGreaterThanEqualOrderById(String string, LocalDateTime newTime);
	}
