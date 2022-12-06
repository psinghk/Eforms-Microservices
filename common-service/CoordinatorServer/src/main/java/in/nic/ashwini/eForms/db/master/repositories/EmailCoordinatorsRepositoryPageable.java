package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.EmailCoordinators;
import in.nic.ashwini.eForms.db.master.entities.PunjabCoordinators;

@Repository
public interface EmailCoordinatorsRepositoryPageable extends JpaRepository<EmailCoordinators, Integer> {
	List<EmailCoordinators> findByEmailAndStatusAndVpnIpContaining(String email, String status, String vpnIp);

	
	Page<EmailCoordinators> findByEmploymentCategoryAndMinistryAndStatus(
			String employmentCategory, String ministry, String status,Pageable pageable);
	
}
