package in.nic.eForms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.eForms.models.SwapSupportContractorTrailBean;

@Repository
public interface SwapSupportContractorRepository extends JpaRepository<SwapSupportContractorTrailBean, LocalDateTime>{

	List<SwapSupportContractorTrailBean> findByUid(String id);
		
	
}
