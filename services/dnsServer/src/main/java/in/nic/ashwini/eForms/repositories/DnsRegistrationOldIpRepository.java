package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DnsRegistrationOldip;

@Repository
public interface DnsRegistrationOldIpRepository extends JpaRepository<DnsRegistrationOldip, Long>{
	List<DnsRegistrationOldip> findByRegistrationNo(String registrationNo);
	DnsRegistrationOldip findByDnsId(long dnsId);
}
