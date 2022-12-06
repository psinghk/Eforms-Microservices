package in.nic.ashwini.eForms.db.slave.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.slave.entities.StatusFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.TrackStatusFromSlave;

@Repository
public interface StatusRepositoryToRead extends JpaRepository<StatusFromSlave, Long>{
	List<StatusFromSlave> findByRegistrationNo(String registrationNo);
	Optional<StatusFromSlave> findByRegistrationNoAndRecipientType(String registrationNo,String recipientType);
	Optional<StatusFromSlave> findFirstByRegistrationNoAndRecipientTypeOrderById(String registrationNo,String recipientType);
	Optional<StatusFromSlave> findFirstByRegistrationNoAndSenderTypeIsNullAndRecipientTypeIsNullOrderById(String registrationNo);
	Optional<StatusFromSlave> findFirstByRegistrationNoAndSenderTypeAndRecipientTypeIsNullOrderById(String registrationNo,String senderType);
	Optional<StatusFromSlave> findFirstByRegistrationNoAndSenderTypeAndRecipientType(String registrationNo,String senderType,String recipientType);
	List<StatusFromSlave> findByRegistrationNoAndSenderTypeOrRecipientType(String registrationNo,String senderType,String recipientType);
	Optional<StatusFromSlave> findFirstByRecipientTypeAndRecipient(String recipientType,String recipient);
	List<StatusFromSlave> findByRegistrationNoOrderById(String registrationNo);
	Optional<StatusFromSlave> findFirstByRegistrationNoOrderByIdDesc(String registrationNo);
	
	@Query("SELECT s FROM StatusFromSlave s WHERE  s.registrationNo = :registrationNo order by id")
	List<StatusFromSlave> findByRegistrationNoOrderByIdThroughQuery(String registrationNo);
	
	@Query("SELECT s FROM StatusFromSlave s WHERE  s.registrationNo = :regNumber  AND ( s.senderType = :sRole OR s.recipientType = :tRole )")
	Optional<StatusFromSlave> findByRegistrationNoForTrack(@Param("regNumber") String registrationNo, @Param("sRole") String senderType, @Param("tRole") String recipientType);
	List<StatusFromSlave> findByRegistrationNoOrderByIdDesc(String registrationNo);
	
	Optional<StatusFromSlave> findById(Long id);
	
	@Modifying
	@Query("UPDATE StatusFromSlave s SET s.onholdStatus = :onHold WHERE  s.registrationNo = :regNumber")
	int updateOnHoldStatus(@Param("regNumber") String registrationNo, @Param("onHold") String onHoldStatus);
	Optional<StatusFromSlave> findFirstByRegistrationNo(String registrationNo);
	Optional<StatusFromSlave> findFirstByRegistrationNoAndSenderTypeIsNullAndRecipientType(String registrationNo,
			String tRole);
	Optional<StatusFromSlave> findFirstByRegistrationNoAndSenderTypeAndRecipientTypeIsNull(String registrationNo,
			String sRole);
}
