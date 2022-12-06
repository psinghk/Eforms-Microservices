package in.nic.ashwini.eForms.db.master.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.DashboardAdmins;

@Repository
public interface DashboardAdminsRepositoryToWrite extends JpaRepository<DashboardAdmins, Integer>{
	Optional<DashboardAdmins> findFirstByEmail(String email);
}
