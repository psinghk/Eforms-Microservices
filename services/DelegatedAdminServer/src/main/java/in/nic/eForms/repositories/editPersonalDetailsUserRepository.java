package in.nic.eForms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.eForms.models.editPersonalDetailslBean;

@Repository
public interface editPersonalDetailsUserRepository extends JpaRepository<editPersonalDetailslBean, LocalDateTime>{

	List<editPersonalDetailslBean> findByUid(String uid);	
	
}
