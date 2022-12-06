package in.nic.eForms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.eForms.models.DeactivateTrailBean;

@Repository
public interface DeactivateUserAccountRespository extends JpaRepository<DeactivateTrailBean, LocalDateTime>{

	List<DeactivateTrailBean> findByUid(String id);
		
	
}
