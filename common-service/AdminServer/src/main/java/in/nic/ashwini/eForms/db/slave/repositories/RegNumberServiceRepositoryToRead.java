package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.RegNumberServiceFromSlave;

@Repository
public interface RegNumberServiceRepositoryToRead extends JpaRepository<RegNumberServiceFromSlave, Long> {
	Optional<RegNumberServiceFromSlave> findByRegNumberFormat(String regNumberFormat);
}
