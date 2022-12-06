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

import in.nic.ashwini.eForms.entities.DnsBulkCname;

@Repository
public interface DnsBulkCnameRepository extends JpaRepository<DnsBulkCname, Long>{
	
	List<DnsBulkCname> findByRegistrationNo(String registrationNo);
	List<DnsBulkCname> findByCampaignIdAndErrorStatusAndDeleteStatus(Long campaignId, String errorStatus, String deleteStatus);
	List<DnsBulkCname> findByCampaignIdAndDomainAndCnameAndErrorStatusAndDeleteStatus(Long campaignId, String domain, String cname, String errorStatus, String deleteStatus);
	Optional<DnsBulkCname> findById(String id);
	@Transactional
	@Modifying
	@Query("update DnsBulkCname d set  d.registrationNo = :regNumber where d.campaignId = :campaignId")
	int updateRegNumberByCampaignId(@Param("campaignId") @NotEmpty Long campaignId, @Param("regNumber") @NotEmpty String regNumber);
}
