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

import in.nic.ashwini.eForms.entities.DnsBulkMx;

@Repository
public interface DnsBulkMxRepository extends JpaRepository<DnsBulkMx, Long>{
	
	List<DnsBulkMx> findByRegistrationNo(String registrationNo);
	List<DnsBulkMx> findByCampaignIdAndErrorStatusAndDeleteStatus(Long campaignId, String errorStatus, String deleteStatus);
	List<DnsBulkMx> findByCampaignIdAndDomainAndMxAndErrorStatusAndDeleteStatus(Long campaignId, String domain, String mx, String errorStatus, String deleteStatus);
	Optional<DnsBulkMx> findById(String id);
	@Transactional
	@Modifying
	@Query("update DnsBulkMx d set  d.registrationNo = :regNumber where d.campaignId = :campaignId")
	int updateRegNumberByCampaignId(@Param("campaignId") @NotEmpty Long campaignId, @Param("regNumber") @NotEmpty String regNumber);

}
