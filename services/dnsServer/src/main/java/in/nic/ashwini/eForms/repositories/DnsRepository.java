package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.DnsBase;

@Repository
public interface DnsRepository extends JpaRepository<DnsBase, Long>{
	
	DnsBase findByRegistrationNo(String registrationNo);
	List<DnsBase> findByDatetimeLike(String datetime);
	List<DnsBase> findByRegistrationNoIn(List<String> registrationNo);
	
	@Query("select distinct d.email from DnsBase d where d.registrationNo in (:regNumbers)")
	List<String> findApplicants(@Param(value = "regNumbers") List<String> regNumbers);
	
//	@Lock(LockModeType.PESSIMISTIC_WRITE)
//	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
	@Query(value = "select registration_no from dns_registration where date(datetime)=date(now()) group by registration_no desc limit 1", nativeQuery = true)
	String findLatestRegistrationNo();
	
	//@Query(value="SELECT email FROM punjab_district_nodal_officers WHERE district = ?1", nativeQuery = true)
	//public String fetchPunjabNodalOfficers(String district);
}
