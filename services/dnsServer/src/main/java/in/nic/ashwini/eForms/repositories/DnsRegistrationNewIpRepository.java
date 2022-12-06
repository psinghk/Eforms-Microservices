package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DnsRegistrationNewip;

@Repository
public interface DnsRegistrationNewIpRepository extends JpaRepository<DnsRegistrationNewip, Long>{
	
	List<DnsRegistrationNewip> findByRegistrationNo(String registrationNo);
	
	@Query(value = "select distinct dns.registrationNo from DnsRegistrationNewip dns where dns.newIp = :newIp")
	List<String> findByNewIp(@Param(value = "newIp") String newIp);
	
	DnsRegistrationNewip findByDnsId(long dnsId);
	
	@Query(value = "select dns.newIp from DnsRegistrationNewip dns where dns.registrationNo = :regNumber")
	String findNewIpByRegNo(@Param(value = "regNumber") String regNumber);

}
