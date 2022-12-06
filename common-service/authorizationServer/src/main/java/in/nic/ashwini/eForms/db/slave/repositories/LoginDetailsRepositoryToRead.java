package in.nic.ashwini.eForms.db.slave.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.LoginDetail;
import in.nic.ashwini.eForms.db.slave.entities.LoginDetailFromSlave;

@Repository
public interface LoginDetailsRepositoryToRead extends JpaRepository<LoginDetailFromSlave, Integer> {
	List<LoginDetailFromSlave> findByEmailInAndSessionIdAndStatus(Set<String> email, String sessionId, Integer status);
	
	List<LoginDetailFromSlave> findByEmailAndStatus(String username, int i);
	LoginDetailFromSlave findFirstByEmailAndStatusOrderByIdDesc(String username, int i);
	List<LoginDetailFromSlave> findByEmailAndStatusAndLoginTimeGreaterThanEqualOrderById(String username, int i, LocalDateTime date);

	List<LoginDetailFromSlave> findByEmailAndStatusAndLoginTimeGreaterThanEqualAndRoleOrderById(String username, int i,
			LocalDateTime newTime, String service);

	LoginDetailFromSlave findFirstByEmailAndStatusAndLoginTimeGreaterThanEqualAndRoleOrderByIdDesc(String email, int i,
			LocalDateTime newTime, String service);
}
