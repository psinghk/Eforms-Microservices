package in.nic.ashwini.eForms.db.master.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.MobileOtp;

@Repository
public interface MobileOtpRepository extends JpaRepository<MobileOtp, Integer> {
	Optional<MobileOtp> findTopByMobileAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(String mobile,
			LocalDateTime time, LocalDateTime time1);

	@Transactional
	@Modifying
	@Query("UPDATE MobileOtp m SET m.resendAttempt = m.resendAttempt + 1 WHERE m.mobile = :mobile AND m.generationTimeStamp <= :currentTime AND m.expiryTimeStamp >= :currentTime ")
	int updateResendAttempt(@Param("mobile") String mobile, @Param("currentTime") LocalDateTime currentTime);
}
