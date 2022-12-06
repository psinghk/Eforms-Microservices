package in.nic.eform.updatemobile.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import in.nic.eform.updatemobile.dto.MobileFinalAuditTrackDTO;
@Repository
public interface MobileFinalAuditTrackRepository extends CrudRepository<MobileFinalAuditTrackDTO, Integer>{
	
	MobileFinalAuditTrackDTO findByRegistrationno(String regNo);
	
	int countByregistrationnoAndToemailLikeOrApplicantemailOrCaemailLikeOrCoordinatoremailLike(String registration_no,String emailAddress1,String emailAddress2,String emailAddress3,String emailAddress4);

	int countByregistrationnoAndApplicantemailAndStatusOrStatusOrStatusOrStatus(String registration_no,String emailAddress1,String status1,String status2,String status3,String status4);

	int countByregistrationnoAndCaemailAndStatusOrStatusAndToemail(String registration_no,String emailAddress1,String status1,String status2,String emailAddress2);

	int countByregistrationnoAndCoordinatoremailAndStatusOrStatusAndToemail(String registration_no,String emailAddress1,String status1,String status2,String emailAddress2);

	int countByregistrationnoAndSupportemailAndStatusOrStatus(String registration_no,String emailAddress1,String status1,String status2);

	int countByregistrationnoAndToemailAndStatus(String registration_no,String emailAddress1,String status);

	
}
