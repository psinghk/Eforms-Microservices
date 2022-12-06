package in.nic.eForms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.eForms.models.AddAliasTrailBean;

@Repository
public interface AddAliasOnUserAccountRepository extends JpaRepository<AddAliasTrailBean, LocalDateTime>{

	List<AddAliasTrailBean> findByUid(String id);
		
	
}
