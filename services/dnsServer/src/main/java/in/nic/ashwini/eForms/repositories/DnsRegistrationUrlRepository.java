package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DnsRegistrationUrl;

@Repository
public interface DnsRegistrationUrlRepository extends JpaRepository<DnsRegistrationUrl, Long>{
	
	List<DnsRegistrationUrl> findByRegistrationNo(String registrationNo);
	@Query(value = "select distinct dns.registrationNo from DnsRegistrationUrl dns where dns.dnsUrl = :dnsUrl")
	List<String> findByDnsUrl(@Param(value = "dnsUrl") String dnsUrl);
	
	DnsRegistrationUrl findByDnsId(long dnsId);
	
	@Query(value = "select dns.dnsUrl from DnsRegistrationUrl dns where dns.registrationNo = :regNumber")
	String findDomainByRegNo(@Param(value = "regNumber") String regNumber);
	
}
