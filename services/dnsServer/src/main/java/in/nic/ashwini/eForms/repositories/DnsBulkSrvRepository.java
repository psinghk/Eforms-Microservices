package in.nic.ashwini.eForms.repositories;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DnsBulkSrv;

@Repository
public interface DnsBulkSrvRepository extends JpaRepository<DnsBulkSrv, Long>{
	
	List<DnsBulkSrv> findByRegistrationNo(String registrationNo);
	List<DnsBulkSrv> findByCampaignIdAndErrorStatusAndDeleteStatus(Long campaignId, String errorStatus, String deleteStatus);
	List<DnsBulkSrv> findByCampaignIdAndDomainAndSrvAndErrorStatusAndDeleteStatus(Long campaignId, String domain, String srv, String errorStatus, String deleteStatus);
	Optional<DnsBulkSrv> findById(String id);
	@Transactional
	@Modifying
	@Query("update DnsBulkSrv d set  d.registrationNo = :regNumber where d.campaignId = :campaignId")
	int updateRegNumberByCampaignId(@Param("campaignId") @NotEmpty Long campaignId, @Param("regNumber") @NotEmpty String regNumber);
}
