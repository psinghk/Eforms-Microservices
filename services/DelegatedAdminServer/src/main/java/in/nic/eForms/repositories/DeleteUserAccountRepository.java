package in.nic.eForms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.eForms.models.DeleteUserAccountTrailBean;
import in.nic.eForms.models.ResetPasswordTrailBean;


@Repository
public interface DeleteUserAccountRepository extends JpaRepository<DeleteUserAccountTrailBean, LocalDateTime>{

	List<DeleteUserAccountTrailBean> findByUid(String id);
		
	
}
