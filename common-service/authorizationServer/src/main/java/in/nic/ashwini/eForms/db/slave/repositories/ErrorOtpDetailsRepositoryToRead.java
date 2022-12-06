package in.nic.ashwini.eForms.db.slave.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.ErrorOtpDetailFromSlave;

@Repository
public interface ErrorOtpDetailsRepositoryToRead extends JpaRepository<ErrorOtpDetailFromSlave, Integer> {

	List<ErrorOtpDetailFromSlave> findByEmailAndLoginTimeGreaterThanEqualAndRoleOrderById(String email, LocalDateTime newTime,
			String service);

	List<ErrorOtpDetailFromSlave> findByMobileAndLoginTimeGreaterThanEqualOrderById(String string, LocalDateTime newTime);
	}
