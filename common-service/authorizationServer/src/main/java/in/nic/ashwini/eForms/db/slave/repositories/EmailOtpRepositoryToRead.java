package in.nic.ashwini.eForms.db.slave.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.EmailOtpFromSlave;

@Repository
public interface EmailOtpRepositoryToRead extends JpaRepository<EmailOtpFromSlave, Integer>{
	Optional<EmailOtpFromSlave> findTopByEmailAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(String mobile, LocalDateTime time, LocalDateTime time1);
	
	@Transactional
	@Modifying
	@Query("UPDATE EmailOtpFromSlave e SET e.resendAttempt = e.resendAttempt + 1 WHERE e.email = :email AND e.generationTimeStamp <= :currentTime AND e.expiryTimeStamp >= :currentTime ")
	int updateResendAttempt(@Param("email") String email, @Param("currentTime") LocalDateTime currentTime);
}
