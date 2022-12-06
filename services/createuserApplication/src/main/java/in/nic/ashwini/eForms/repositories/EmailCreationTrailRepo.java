package in.nic.ashwini.eForms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.EmailCreationTrail;

@Repository
public interface EmailCreationTrailRepo extends JpaRepository<EmailCreationTrail, Long> {

	
}
