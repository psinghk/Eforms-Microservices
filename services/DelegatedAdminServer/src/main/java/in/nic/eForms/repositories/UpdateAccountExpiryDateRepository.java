package in.nic.eForms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import in.nic.eForms.models.UpdateDateOfExpiryTrailBean;

@Repository
public interface UpdateAccountExpiryDateRepository extends JpaRepository<UpdateDateOfExpiryTrailBean, LocalDateTime>{

	List<UpdateDateOfExpiryTrailBean> findByUid(String id);
		
	
}
