package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.SupportFromSlave;

@Repository
public interface SupportRepositoryToRead extends JpaRepository<SupportFromSlave, Integer>{
	Optional<SupportFromSlave> findFirstByIp(String ip);
}
