package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.PunjabCoordinatorsFromSlave;

@Repository
public interface PunjabCoordinatorsRepositoryToRead extends JpaRepository<PunjabCoordinatorsFromSlave, Integer> {
	List<PunjabCoordinatorsFromSlave> findByDistrict(String district);
}
