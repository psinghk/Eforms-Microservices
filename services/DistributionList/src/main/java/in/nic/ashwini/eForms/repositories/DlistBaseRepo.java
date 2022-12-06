package in.nic.ashwini.eForms.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import in.nic.ashwini.eForms.entities.DlistBase;

@Repository
public interface DlistBaseRepo extends JpaRepository<DlistBase, Long>{
	
	DlistBase findByRegistrationNo(String registrationNo);
	List<DlistBase> findByDatetimeLike(String datetime);
	List<DlistBase>  findByid(Long id);
	
	@Query(value = "select registration_no from distribution_registration where date(datetime)=date(now()) group by registration_no desc limit 1", nativeQuery = true)
	String findLatestRegistrationNo();
	
}
