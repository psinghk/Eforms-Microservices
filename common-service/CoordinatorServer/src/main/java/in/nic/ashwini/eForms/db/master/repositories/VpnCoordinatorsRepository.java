package in.nic.ashwini.eForms.db.master.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.VpnCoordinators;

@Repository
public interface VpnCoordinatorsRepository extends JpaRepository<VpnCoordinators, Integer>{
	Optional<VpnCoordinators> findFirstByEmailAndStatus(String email, String status);
}
