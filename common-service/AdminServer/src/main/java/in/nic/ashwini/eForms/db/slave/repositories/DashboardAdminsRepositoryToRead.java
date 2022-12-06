package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.DashboardAdminsFromSlave;

@Repository
public interface DashboardAdminsRepositoryToRead extends JpaRepository<DashboardAdminsFromSlave, Integer>{
	Optional<DashboardAdminsFromSlave> findFirstByEmail(String email);
}
