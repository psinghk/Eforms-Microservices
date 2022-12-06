package in.nic.eForms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.eForms.models.ChangeIMAPandPOPTrailBean;

@Repository
public interface ChangeIMAPandPOPRepository extends JpaRepository<ChangeIMAPandPOPTrailBean, LocalDateTime>{

	List<ChangeIMAPandPOPTrailBean> findByUid(String id);
		
	
}
