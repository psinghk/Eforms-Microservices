package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DnsTeamData;

@Repository
public interface DnsTeamDataRepository extends JpaRepository<DnsTeamData, Long>{
	
	@Query("select distinct d.applicantEmail from DnsTeamData d where d.deleteStatus = 0 and d.domain = :domain")
	List<String> findOwnersByDomain(@Param(value = "domain") String domain);
	
	@Query("select d.ip from DnsTeamData d where d.deleteStatus = 0 and d.domain = :domain")
	List<String> findIpByDomain(@Param(value = "domain") String domain);
	
	@Query("select d.domain from DnsTeamData d where d.deleteStatus = 0 and d.ip = :ip")
	List<String> findDomainByIp(@Param(value = "ip") String ip);

}
