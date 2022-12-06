package in.nic.ashwini.eForms.db.master.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.RegNumberService;

@Repository
public interface RegNumberServiceRepositoryToWrite extends JpaRepository<RegNumberService, Long> {
	Optional<RegNumberService> findByRegNumberFormat(String regNumberFormat);
}
