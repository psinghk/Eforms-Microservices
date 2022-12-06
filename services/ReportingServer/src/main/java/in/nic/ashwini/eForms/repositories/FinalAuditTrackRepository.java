package in.nic.ashwini.eForms.repositories;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.nic.ashwini.eForms.entities.FinalAuditTrack;

@Repository
public interface FinalAuditTrackRepository extends JpaRepository<FinalAuditTrack, Long> {
	FinalAuditTrack findByRegistrationNo(String registrationNo);

	int countByRegistrationNoAndToEmailContaining(String registrationNo, String toEmail);
	
	int countByRegistrationNoAndStatus(String registrationNo, String status);
	int countByRegistrationNoAndStatusContaining(String regNumber, String status);

	@Query("select count(f.registrationNo) from FinalAuditTrack f where f.registrationNo = :regNumber "
			+ "and (f.applicantEmail in (:aliases) " + "or f.caEmail in (:aliases) "
			+ "or f.coordinatorEmail in (:aliases) " + "or f.supportEmail in (:aliases) "
			+ "or f.adminEmail in (:aliases) " + "or f.daEmail in (:aliases) " + "or f.usEmail in (:aliases) "
			+ "or f.toEmail in (:aliases))")
	int countRegistrationNumbersWhereUserIsStakeHolderButConsideringUniqueEmailInToEmail(
			@Param("regNumber") String registrationNo, @Param("aliases") List<String> aliases);
	// Optional<FinalAuditTrackBasic> findByRegistrationNo(String registrationNo);

	@Query("select f.toEmail from FinalAuditTrack f where f.registrationNo = :regNumber")
	String findToEmail(@Param("regNumber") String registrationNo);

	@Query("select distinct f.registrationNo from FinalAuditTrack f where f.status = 'completed' and f.registrationNo in (:regNumbers) order by f.registrationNo desc")
	List<String> findCompletedRegistrationNos(@Param(value = "regNumbers") List<String> reghNumbers);
	
	Page<FinalAuditTrack> findByApplicantEmail(String email, Pageable pageable);

	Page<FinalAuditTrack> findByStatusInAndApplicantEmailIn(List<String> status, List<String> email, Pageable pageable);

	Page<FinalAuditTrack> findByApplicantEmailIn(List<String> email, Pageable pageable);

	Page<FinalAuditTrack> findByFormNameInAndApplicantEmailIn(List<String> formName, List<String> email,
			Pageable pageable);

	Page<FinalAuditTrack> findByStatusInAndFormNameInAndApplicantEmailIn(List<String> status, List<String> formName,
			List<String> email, Pageable pageable);

	int countByStatusInAndApplicantEmailIn(List<String> status, List<String> email);

	int countByApplicantEmailIn(List<String> email);

	Page<FinalAuditTrack> findByStatusInAndToEmailIn(List<String> status, List<String> email, Pageable pageable);

	Page<FinalAuditTrack> findByStatusInAndCaEmailIn(List<String> status, List<String> email, Pageable pageable);

	Page<FinalAuditTrack> findByCaEmailIn(List<String> email, Pageable pageable);

	Page<FinalAuditTrack> findByFormNameInAndCaEmailInOrFormNameInAndToEmailIn(List<String> formName,
			List<String> email, List<String> formName1, List<String> email1, Pageable pageable);

	Page<FinalAuditTrack> findByStatusInAndCaEmailInOrStatusInAndToEmailIn(List<String> status, List<String> email,
			List<String> status1, List<String> email1, Pageable pageable);

	Page<FinalAuditTrack> findByStatusInAndFormNameInAndCaEmailInOrStatusInAndFormNameInAndToEmailIn(
			List<String> status, List<String> service, List<String> email, List<String> status2, List<String> service2,
			List<String> email2, Pageable sortedByIdDesc);

	int countByStatusInAndToEmailIn(List<String> status, List<String> email);

	int countByStatusInAndCaEmailIn(List<String> status, List<String> email);

	int countByCaEmailIn(List<String> email);
	
	Page<FinalAuditTrack> findByStatusInAndCoordinatorEmailIn(List<String> status, List<String> email, Pageable pageable);

	Page<FinalAuditTrack> findByCoordinatorEmailIn(List<String> email, Pageable pageable);

	Page<FinalAuditTrack> findByFormNameInAndCoordinatorEmailInOrFormNameInAndToEmailIn(List<String> formName,
			List<String> email, List<String> formName1, List<String> email1, Pageable pageable);

	Page<FinalAuditTrack> findByStatusInAndCoordinatorEmailInOrStatusInAndToEmailIn(List<String> status, List<String> email,
			List<String> status1, List<String> email1, Pageable pageable);

	Page<FinalAuditTrack> findByStatusInAndFormNameInAndCoordinatorEmailInOrStatusInAndFormNameInAndToEmailIn(
			List<String> status, List<String> service, List<String> email, List<String> status2, List<String> service2,
			List<String> email2, Pageable sortedByIdDesc);
	
	int countByStatusInAndCoordinatorEmailIn(List<String> status, List<String> email);

	int countByCoordinatorEmailIn(List<String> email);

	@Query("select distinct f.formName from FinalAuditTrack f where f.applicantEmail in (:email)")
	Set<String> findDistinctFormNamesForUser(@Param("email") @NotEmpty List<String> email);

	@Query("select distinct f.status from FinalAuditTrack f where f.applicantEmail in (:email)")
	Set<String> findDistinctStatusForUser(@Param("email") @NotEmpty List<String> email);

	@Query("select distinct f.formName from FinalAuditTrack f where f.caEmail in (:email) or (f.toEmail in (:email) and f.status='ca_pending')")
	Set<String> findDistinctFormNamesForRo(@Param("email") @NotEmpty List<String> email);

	@Query("select distinct f.status from FinalAuditTrack f where f.caEmail in (:email) or (f.toEmail in (:email) and f.status='ca_pending')")
	Set<String> findDistinctStatusForRo(@Param("email") @NotEmpty List<String> email);
	
	@Query("select distinct f.formName from FinalAuditTrack f where f.coordinatorEmail in (:email)  or (f.toEmail in (:email) and f.status='coordinator_pending')")
	Set<String> findDistinctFormNamesForCo(@Param("email") @NotEmpty List<String> email);

	@Query("select distinct f.status from FinalAuditTrack f where f.coordinatorEmail in (:email)  or (f.toEmail in (:email) and f.status='coordinator_pending')")
	Set<String> findDistinctStatusForCo(@Param("email") @NotEmpty List<String> email);

	Page<FinalAuditTrack> findByStatusInAndFormNameIn(List<String> status, List<String> allowedForms,
			Pageable sortedByIdDesc);

	Page<FinalAuditTrack> findByFormNameIn(List<String> allowedForms, Pageable sortedByIdDesc);

	int countByStatusInAndFormNameIn(List<String> status_list, List<String> allowedForms);

	int countByFormNameIn(List<String> emailList);
	
	@Query("select distinct f.status from FinalAuditTrack f")
	Set<String> findDistinctStatus();
	
	@Query(value = "select f from FinalAuditTrack f where f.registrationNo like %:keyword%  OR f.applicantEmail like %:keyword%  OR f.status like %:keyword%  OR f.appUserType like %:keyword% OR f.toDatetime like %:keyword% ")
	List<FinalAuditTrack> searchByKeyword(@Param("keyword") String keyword);
	
	@Query(value = "SELECT f from FinalAuditTrack f")
	List<FinalAuditTrack> finalAuditTrackExportData();
	
	@Modifying
	@Query("UPDATE FinalAuditTrack f SET f.onHold = :onHold, f.holdRemarks = :remarks WHERE  f.registrationNo = :regNumber")
	int updateOnHoldStatus(@Param("regNumber") String registrationNo, @Param("onHold") String onHoldStatus,  @Param("remarks") String remarks);

	Page<FinalAuditTrack> findByOnHold(String onHold, Pageable pageable);

	Page<FinalAuditTrack> findByCoordinatorEmailInOrStatusAndCoordinatorEmailInOrToEmailIn(List<String> emailList,
			String string, List<String> emailList2, List<String> emailList3, Pageable pageable);

	Page<FinalAuditTrack> findBySupportEmailInOrStatusAndSupportEmailInOrToEmailIn(List<String> emailList,
			String string, List<String> emailList2, List<String> emailList3, Pageable pageable);

	Page<FinalAuditTrack> findByAdminEmailInOrStatusAndAdminEmailInOrToEmailIn(List<String> emailList, String string,
			List<String> emailList2, List<String> emailList3, Pageable pageable);

	Page<FinalAuditTrack> findByApplicantName(String value, Pageable pageable);

	Page<FinalAuditTrack> findByCaEmailInOrStatusAndCaEmailInOrToEmailIn(List<String> emailList, String string,
			List<String> emailList2, List<String> emailList3, Pageable pageable);
	
	Page<FinalAuditTrack> findByCaName(String value, Pageable pageable);
	
	Page<FinalAuditTrack> findByCoordinatorName(String value, Pageable pageable);
	
	Page<FinalAuditTrack> findBySupportName(String value, Pageable pageable);
	
	Page<FinalAuditTrack> findByAdminName(String value, Pageable pageable);

	Page<FinalAuditTrack> findByApplicantMobile(String value, Pageable pageable);

	Page<FinalAuditTrack> findByCaMobile(String value, Pageable pageable);

	Page<FinalAuditTrack> findBySupportMobile(String value, Pageable pageable);

	Page<FinalAuditTrack> findByCoordinatorMobile(String value, Pageable pageable);

	Page<FinalAuditTrack> findByAdminMobile(String value, Pageable pageable);
	
	Page<FinalAuditTrack> findByStatusInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> status_list, List<String> emailList, String searchKey, String searchKey2, String searchKey3, 
			String searchKey5, String searchKey6, String searchKey7, String searchKey8,
			Pageable sortedByIdDesc);
	
	Page<FinalAuditTrack> findByApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> emailList, String searchKey, String searchKey2, String searchKey3, 
			String searchKey5, String searchKey6, String searchKey7, String searchKey8,
			Pageable sortedByIdDesc);

	Page<FinalAuditTrack> findByFormNameInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> service_list, List<String> emailList, String searchKey, String searchKey2, String searchKey3, 
			String searchKey5, String searchKey6, String searchKey7, String searchKey8,
			Pageable sortedByIdDesc);

	Page<FinalAuditTrack> findByStatusInAndFormNameInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> status_list, List<String> service_list, List<String> emailList, String searchKey,
			String searchKey2, String searchKey3, String searchKey5, String statusString,
			String searchKey6, String searchKey7, Pageable sortedByIdDesc);
	
	Page<FinalAuditTrack> findByFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> service_list, String searchKey, String searchKey2, String searchKey3, 
			String searchKey5, String searchKey6, String searchKey7, String searchKey8,
			Pageable sortedByIdDesc);
	
	Page<FinalAuditTrack> findByStatusInAndFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> status_list, List<String> service_list, String searchKey,
			String searchKey2, String searchKey3, String searchKey5, String statusString,
			String searchKey6, String searchKey7, Pageable sortedByIdDesc);
	
	Page<FinalAuditTrack> findByStatusInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> status_list, String searchKey,
			String searchKey2, String searchKey3, String searchKey5, String statusString,
			String searchKey6, String searchKey7, Pageable sortedByIdDesc);
}
