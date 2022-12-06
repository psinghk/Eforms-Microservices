package in.nic.ashwini.eForms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.UpdateMobileOtp;

@Repository
public interface UpdateMobileOtpRepo extends JpaRepository<UpdateMobileOtp, Long>{
	
	@Query(value = "select otp from update_mobile_otp where mobile = ?1", nativeQuery = true)
	public String findByMobile(String mobile);

	
	@Query(value = "select otp from update_mobile_otp where mobile = ?1 and exptime >= NOW() order by id desc limit 1", nativeQuery = true)
	public String fetchOtp(String mobile);
}
