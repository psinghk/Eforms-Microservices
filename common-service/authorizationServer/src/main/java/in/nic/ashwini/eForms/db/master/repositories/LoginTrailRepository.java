package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.LoginTrail;

@Repository
public interface LoginTrailRepository extends JpaRepository<LoginTrail, Long> {
	
	
	List<LoginTrail> findByEmailInAndTokenAndStatus(Set<String> alias,String token,Integer status);
	}
