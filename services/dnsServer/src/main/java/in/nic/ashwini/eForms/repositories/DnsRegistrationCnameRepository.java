package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DnsRegistrationCname;
import in.nic.ashwini.eForms.entities.DnsRegistrationUrl;

@Repository
public interface DnsRegistrationCnameRepository extends JpaRepository<DnsRegistrationCname, Long>{
	List<DnsRegistrationCname> findByRegistrationNo(String registrationNo);
	DnsRegistrationCname findByDnsId(long dnsId);
}
