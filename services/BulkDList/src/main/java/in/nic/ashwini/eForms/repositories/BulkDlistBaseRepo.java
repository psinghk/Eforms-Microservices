package in.nic.ashwini.eForms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.BulkDlistBase;

@Repository
public interface BulkDlistBaseRepo extends JpaRepository<BulkDlistBase, Long>{
	
	BulkDlistBase findByRegistrationNo(String registrationNo);
	List<BulkDlistBase> findByDatetimeLike(String datetime);
	
//	@Lock(LockModeType.PESSIMISTIC_WRITE)
//	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="5000")})
	@Query(value = "select registration_no from bulk_distribution_registration where date(datetime)=date(now()) group by registration_no desc limit 1", nativeQuery = true)
	String findLatestRegistrationNo();
	
}
