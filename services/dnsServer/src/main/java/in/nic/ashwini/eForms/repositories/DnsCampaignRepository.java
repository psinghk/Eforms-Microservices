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

import in.nic.ashwini.eForms.entities.DnsBulkCampaigns;

@Repository
public interface DnsCampaignRepository extends JpaRepository<DnsBulkCampaigns, Long>{
	DnsBulkCampaigns findByRegistrationNo(String registrationNo);
	List<DnsBulkCampaigns> findByOwnerEmailAndStatusAndDiscardStatus(String ownerEmail,String status, String discardStatus);
	Optional<DnsBulkCampaigns> findById(String id);
	@Transactional
	@Modifying
	@Query("update DnsBulkCampaigns d set  d.registrationNo = :regNumber where d.id = :campaignId")
	int updateRegNumberByCampaignId(@Param("campaignId") @NotEmpty Long campaignId, @Param("regNumber") @NotEmpty String regNumber);
}
