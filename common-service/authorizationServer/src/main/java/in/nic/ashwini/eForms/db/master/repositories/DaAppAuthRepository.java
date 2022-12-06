package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.DaAppAuth;

@Repository
public interface DaAppAuthRepository extends JpaRepository<DaAppAuth, Integer> {
	List<DaAppAuth> findByEmailInAndIpAndStatus(Set<String> email, String ip, Integer status);
}
