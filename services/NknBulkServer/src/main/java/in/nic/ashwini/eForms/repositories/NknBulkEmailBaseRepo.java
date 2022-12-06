package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.NknBulkEmailBase;

@Repository
public interface NknBulkEmailBaseRepo extends JpaRepository<NknBulkEmailBase, Long>{
	

	NknBulkEmailBase findByRegistrationNo(String registrationNo);
	List<NknBulkEmailBase> findByDatetimeLike(String datetime);
	
//	@Lock(LockModeType.PESSIMISTIC_WRITE)
//	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
	@Query(value = "select registration_no from nkn_registration where date(datetime)=date(now()) group by registration_no desc limit 1", nativeQuery = true)
	String findLatestRegistrationNo();
	
	//@Query(value="SELECT email FROM punjab_district_nodal_officers WHERE district = ?1", nativeQuery = true)
	//public String fetchPunjabNodalOfficers(String district);
	
}