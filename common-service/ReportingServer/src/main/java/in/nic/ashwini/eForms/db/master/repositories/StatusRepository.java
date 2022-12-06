package in.nic.ashwini.eForms.db.master.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.db.master.entities.Status;
import in.nic.ashwini.eForms.db.master.entities.projections.TrackStatus;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long>{
	List<Status> findByRegistrationNo(String registrationNo);
	Optional<Status> findByRegistrationNoAndRecipientType(String registrationNo,String recipientType);
	Optional<Status> findFirstByRegistrationNoAndRecipientTypeOrderById(String registrationNo,String recipientType);
	Optional<Status> findFirstByRegistrationNoAndSenderTypeIsNullAndRecipientTypeIsNullOrderById(String registrationNo);
	Optional<Status> findFirstByRegistrationNoAndSenderTypeAndRecipientTypeIsNullOrderById(String registrationNo,String senderType);
	Optional<Status> findFirstByRegistrationNoAndSenderTypeAndRecipientType(String registrationNo,String senderType,String recipientType);
	List<Status> findByRegistrationNoAndSenderTypeOrRecipientType(String registrationNo,String senderType,String recipientType);
	Optional<Status> findFirstByRecipientTypeAndRecipient(String recipientType,String recipient);
	List<TrackStatus> findByRegistrationNoOrderById(String registrationNo);
	Optional<Status> findFirstByRegistrationNoOrderByIdDesc(String registrationNo);
	
	
	@Query("SELECT s FROM Status s WHERE  s.registrationNo = :regNumber  AND ( s.senderType = :sRole OR s.recipientType = :tRole )")
	Optional<Status> findByRegistrationNoForTrack(@Param("regNumber") String registrationNo, @Param("sRole") String senderType, @Param("tRole") String recipientType);
	List<Status> findByRegistrationNoOrderByIdDesc(String registrationNo);
	
	@Modifying
	@Query("UPDATE Status s SET s.onholdStatus = :onHold WHERE  s.registrationNo = :regNumber")
	int updateOnHoldStatus(@Param("regNumber") String registrationNo, @Param("onHold") String onHoldStatus);	
}
