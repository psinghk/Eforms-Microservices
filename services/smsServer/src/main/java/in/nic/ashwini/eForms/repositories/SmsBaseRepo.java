package in.nic.ashwini.eForms.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import in.nic.ashwini.eForms.entities.SmsBase;

@Repository
public interface SmsBaseRepo extends JpaRepository<SmsBase, Long>{
	
	SmsBase findByRegistrationNo(String registrationNo);
	List<SmsBase> findByDatetimeLike(String datetime);
	
	@Query(value = "select registration_no from sms_registration where date(datetime)=date(now()) group by registration_no desc limit 1", nativeQuery = true)
	String findLatestRegistrationNo();

}
