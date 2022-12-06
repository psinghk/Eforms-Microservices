package in.nic.ashwini.eForms.db.slave.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.ErrorLoginDetailFromSlave;

@Repository
public interface ErrorLoginDetailsRepositoryToRead extends JpaRepository<ErrorLoginDetailFromSlave, Integer> {

	List<ErrorLoginDetailFromSlave> findByEmailAndLoginTimeGreaterThanEqualAndRoleOrderById(String email, LocalDateTime newTime,
			String service);
	}
