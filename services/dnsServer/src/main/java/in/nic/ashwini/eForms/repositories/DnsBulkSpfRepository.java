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

import in.nic.ashwini.eForms.entities.DnsBulkSpf;

@Repository
public interface DnsBulkSpfRepository extends JpaRepository<DnsBulkSpf, Long>{
	
	List<DnsBulkSpf> findByRegistrationNo(String registrationNo);
	List<DnsBulkSpf> findByCampaignIdAndErrorStatusAndDeleteStatus(Long campaignId, String errorStatus, String deleteStatus);
	List<DnsBulkSpf> findByCampaignIdAndDomainAndSpfAndErrorStatusAndDeleteStatus(Long campaignId, String domain, String spf, String errorStatus, String deleteStatus);
	Optional<DnsBulkSpf> findById(String id);
	@Transactional
	@Modifying
	@Query("update DnsBulkSpf d set  d.registrationNo = :regNumber where d.campaignId = :campaignId")
	int updateRegNumberByCampaignId(@Param("campaignId") @NotEmpty Long campaignId, @Param("regNumber") @NotEmpty String regNumber);

}
