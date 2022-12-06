package in.nic.ashwini.eForms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.RegNumberService;

@Repository
public interface RegNumberServiceRepository extends JpaRepository<RegNumberService, Long> {
	Optional<RegNumberService> findByRegNumberFormat(String regNumberFormat);
}
