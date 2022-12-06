package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.SupportAppAuth;

@Repository
public interface SupportAppRepo extends JpaRepository<SupportAppAuth, Long>{

	List<SupportAppAuth> findByEmailInAndIpAndStatus(Set<String> email, String ip, Integer status);
	
	}
