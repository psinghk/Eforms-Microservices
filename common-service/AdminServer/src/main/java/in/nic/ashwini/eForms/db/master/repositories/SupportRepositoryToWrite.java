package in.nic.ashwini.eForms.db.master.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.Support;

@Repository
public interface SupportRepositoryToWrite extends JpaRepository<Support, Integer>{
	Optional<Support> findFirstByIp(String ip);
}
