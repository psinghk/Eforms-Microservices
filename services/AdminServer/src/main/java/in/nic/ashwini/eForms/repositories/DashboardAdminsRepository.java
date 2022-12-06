package in.nic.ashwini.eForms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DashboardAdmins;

@Repository
public interface DashboardAdminsRepository extends JpaRepository<DashboardAdmins, Integer>{
	Optional<DashboardAdmins> findFirstByEmail(String email);
}
