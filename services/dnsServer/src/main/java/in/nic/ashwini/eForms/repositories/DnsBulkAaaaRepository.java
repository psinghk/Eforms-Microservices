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

import in.nic.ashwini.eForms.entities.DnsBulkUpload;

@Repository
public interface DnsBulkAaaaRepository extends JpaRepository<DnsBulkUpload, Long> {
	List<DnsBulkUpload> findByRegistrationNo(String registrationNo);

	List<DnsBulkUpload> findByCampaignIdAndErrorStatusAndDeleteStatus(Long campaignId, String errorStatus,
			String deleteStatus);

	List<DnsBulkUpload> findByCampaignIdAndDomainAndErrorStatusAndDeleteStatus(Long campaignId, String domain,
			String errorStatus, String deleteStatus);

	Optional<DnsBulkUpload> findById(Long id);

	List<DnsBulkUpload> findByCampaignIdAndDomainAndNewIpAndErrorStatusAndDeleteStatus(long campaignId, String domain, String newIp,
			String errorStatus, String deleteStatus);
	@Transactional
	@Modifying
	@Query("update DnsBulkUpload d set  d.registrationNo = :regNumber where d.campaignId = :campaignId")
	int updateRegNumberByCampaignId(@Param("campaignId") @NotEmpty Long campaignId, @Param("regNumber") @NotEmpty String regNumber);
}
