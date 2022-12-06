package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.EmailCoordinatorsFromSlave;

@Repository
public interface EmailCoordinatorsRepositoryPageableToRead extends JpaRepository<EmailCoordinatorsFromSlave, Integer> {
	List<EmailCoordinatorsFromSlave> findByEmailAndStatusAndVpnIpContaining(String email, String status, String vpnIp);

	
	Page<EmailCoordinatorsFromSlave> findByEmploymentCategoryAndMinistryAndStatus(
			String employmentCategory, String ministry, String status,Pageable pageable);
	
}
