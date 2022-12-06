package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.VpnCoordinatorsFromSlave;

@Repository
public interface VpnCoordinatorsRepositoryToRead extends JpaRepository<VpnCoordinatorsFromSlave, Integer>{
	Optional<VpnCoordinatorsFromSlave> findFirstByEmailAndStatus(String email, String status);
}
