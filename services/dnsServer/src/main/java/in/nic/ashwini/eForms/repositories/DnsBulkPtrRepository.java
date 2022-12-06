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

import in.nic.ashwini.eForms.entities.DnsBulkPtr;

@Repository
public interface DnsBulkPtrRepository extends JpaRepository<DnsBulkPtr, Long>{
	
	List<DnsBulkPtr> findByRegistrationNo(String registrationNo);
	List<DnsBulkPtr> findByCampaignIdAndErrorStatusAndDeleteStatus(Long campaignId, String errorStatus, String deleteStatus);
	List<DnsBulkPtr> findByCampaignIdAndDomainAndPtrAndErrorStatusAndDeleteStatus(Long campaignId, String domain, String ptr, String errorStatus, String deleteStatus);
	Optional<DnsBulkPtr> findById(String id);
	@Transactional
	@Modifying
	@Query("update DnsBulkPtr d set  d.registrationNo = :regNumber where d.campaignId = :campaignId")
	int updateRegNumberByCampaignId(@Param("campaignId") @NotEmpty Long campaignId, @Param("regNumber") @NotEmpty String regNumber);

}
