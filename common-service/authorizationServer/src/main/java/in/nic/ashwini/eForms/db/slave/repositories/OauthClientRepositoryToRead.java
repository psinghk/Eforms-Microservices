package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.OauthFromSlave;

@Repository
public interface OauthClientRepositoryToRead extends JpaRepository<OauthFromSlave, Integer>{
	Optional<OauthFromSlave> findFirstByClientId(String clientId);
}
