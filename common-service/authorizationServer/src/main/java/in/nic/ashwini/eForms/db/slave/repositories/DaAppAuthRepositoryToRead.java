package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.DaAppAuthFromSlave;

@Repository
public interface DaAppAuthRepositoryToRead extends JpaRepository<DaAppAuthFromSlave, Integer> {
	List<DaAppAuthFromSlave> findByEmailInAndIpAndStatus(Set<String> email, String ip, Integer status);
}
