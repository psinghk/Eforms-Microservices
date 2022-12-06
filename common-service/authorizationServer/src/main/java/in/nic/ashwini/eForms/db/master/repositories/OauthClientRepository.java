package in.nic.ashwini.eForms.db.master.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.Oauth;

@Repository
public interface OauthClientRepository extends JpaRepository<Oauth, Integer>{
	Optional<Oauth> findFirstByClientId(String clientId);
}
