package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.PunjabCoordinators;

@Repository
public interface PunjabCoordinatorsRepository extends JpaRepository<PunjabCoordinators, Integer> {
	List<PunjabCoordinators> findByDistrict(String district);
}
