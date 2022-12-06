package in.nic.ashwini.eForms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.Support;

@Repository
public interface SupportRepository extends JpaRepository<Support, Integer>{
	Optional<Support> findFirstByIp(String ip);
}
