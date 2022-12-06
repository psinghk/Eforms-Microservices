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

import in.nic.ashwini.eForms.entities.DnsBulkDmarc;

@Repository
public interface DnsBulkDmarcRepository extends JpaRepository<DnsBulkDmarc, Long>{
	
	List<DnsBulkDmarc> findByRegistrationNo(String registrationNo);
	List<DnsBulkDmarc> findByCampaignIdAndErrorStatusAndDeleteStatus(Long campaignId, String errorStatus, String deleteStatus);
	List<DnsBulkDmarc> findByCampaignIdAndDomainAndDmarcAndErrorStatusAndDeleteStatus(Long campaignId, String domain, String dmarc, String errorStatus, String deleteStatus);
	
	Optional<DnsBulkDmarc> findById(String id);
	@Transactional
	@Modifying
	@Query("update DnsBulkDmarc d set  d.registrationNo = :regNumber where d.campaignId = :campaignId")
	int updateRegNumberByCampaignId(@Param("campaignId") @NotEmpty Long campaignId, @Param("regNumber") @NotEmpty String regNumber);

}
