package in.nic.eForms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.eForms.models.DeactivateTrailBean;
import in.nic.eForms.models.ExchangePrimaryEquivelantTrailBean;

@Repository
public interface ExchangePrimaryWithEquivalent extends JpaRepository<ExchangePrimaryEquivelantTrailBean, LocalDateTime>{

	List<ExchangePrimaryEquivelantTrailBean> findByUid(String id);
		
	
}
