package in.nic.ashwini.eForms.db.slave.repositories;

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

import in.nic.ashwini.eForms.db.slave.entities.FinalAuditTrackFromSlave;

@Repository
public interface FinalAuditTrackRepositoryToRead extends JpaRepository<FinalAuditTrackFromSlave, Long> {
	FinalAuditTrackFromSlave findByRegistrationNo(String registrationNo);

	int countByRegistrationNoAndToEmailContaining(String registrationNo, String toEmail);

	int countByRegistrationNoAndStatus(String registrationNo, String status);

	int countByRegistrationNoAndStatusContaining(String regNumber, String status);

	@Query("select count(f.registrationNo) from FinalAuditTrackFromSlave f where f.registrationNo = :regNumber "
			+ "and (f.applicantEmail in (:aliases) " + "or f.caEmail in (:aliases) "
			+ "or f.coordinatorEmail in (:aliases) " + "or f.supportEmail in (:aliases) "
			+ "or f.adminEmail in (:aliases) " + "or f.daEmail in (:aliases) " + "or f.usEmail in (:aliases) "
			+ "or f.toEmail in (:aliases))")
	int countRegistrationNumbersWhereUserIsStakeHolderButConsideringUniqueEmailInToEmail(
			@Param("regNumber") String registrationNo, @Param("aliases") List<String> aliases);
	// Optional<FinalAuditTrackBasic> findByRegistrationNo(String registrationNo);

	@Query("select f.toEmail from FinalAuditTrackFromSlave f where f.registrationNo = :regNumber")
	String findToEmail(@Param("regNumber") String registrationNo);

	@Query("select distinct f.registrationNo from FinalAuditTrackFromSlave f where f.status = 'completed' and f.registrationNo in (:regNumbers) order by f.registrationNo desc")
	List<String> findCompletedRegistrationNos(@Param(value = "regNumbers") List<String> reghNumbers);
	
	@Query("select f.formName from FinalAuditTrackFromSlave f where f.registrationNo = :regNumber")
	String findFormNameForRegistrationNos(@Param(value = "regNumber") String regNumber);

	Page<FinalAuditTrackFromSlave> findByApplicantEmail(String email, Pageable pageable);
	
	
	Page<FinalAuditTrackFromSlave> findByStatusInAndApplicantEmailIn(List<String> status, List<String> email, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByApplicantEmailIn(List<String> email, Pageable pageable);

	@Query("select f from FinalAuditTrackFromSlave f where f.formName in (:formName) and f.applicantEmail in (:email)")
	Page<FinalAuditTrackFromSlave> findByFormNameInAndApplicantEmailIn(List<String> formName, List<String> email,
			Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByStatusInAndFormNameInAndApplicantEmailIn(List<String> status, List<String> formName,
			List<String> email, Pageable pageable);

	int countByStatusInAndApplicantEmailIn(List<String> status, List<String> email);
	
	@Query("select distinct f.formName from FinalAuditTrackFromSlave f where f.status in (:status) and f.applicantEmail in (:email)")
	List<String> findDistinctFormsByStatusInAndApplicantEmailIn(List<String> status, List<String> email);
	
	int countByApplicantEmailIn(List<String> email);
	// int countByApplicantEmail(String email);

	Page<FinalAuditTrackFromSlave> findByStatusInAndToEmailIn(List<String> status, List<String> email, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByStatusInAndCaEmailIn(List<String> status, List<String> email, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByCaEmailIn(List<String> email, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByFormNameInAndCaEmailInOrFormNameInAndToEmailIn(List<String> formName,
			List<String> email, List<String> formName1, List<String> email1, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByStatusInAndCaEmailInOrStatusInAndToEmailIn(List<String> status, List<String> email,
			List<String> status1, List<String> email1, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByStatusInAndFormNameInAndCaEmailInOrStatusInAndFormNameInAndToEmailIn(
			List<String> status, List<String> service, List<String> email, List<String> status2, List<String> service2,
			List<String> email2, Pageable sortedByIdDesc);

	int countByStatusInAndToEmailIn(List<String> status, List<String> email);

	int countByStatusInAndCaEmailIn(List<String> status, List<String> email);

	int countByCaEmailIn(List<String> email);

	Page<FinalAuditTrackFromSlave> findByStatusInAndCoordinatorEmailIn(List<String> status, List<String> email,
			Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByCoordinatorEmailIn(List<String> email, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByFormNameInAndCoordinatorEmailInOrFormNameInAndToEmailIn(List<String> formName,
			List<String> email, List<String> formName1, List<String> email1, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByStatusInAndCoordinatorEmailInOrStatusInAndToEmailIn(List<String> status,
			List<String> email, List<String> status1, List<String> email1, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByStatusInAndFormNameInAndCoordinatorEmailInOrStatusInAndFormNameInAndToEmailIn(
			List<String> status, List<String> service, List<String> email, List<String> status2, List<String> service2,
			List<String> email2, Pageable sortedByIdDesc);

	int countByStatusInAndCoordinatorEmailIn(List<String> status, List<String> email);

	int countByCoordinatorEmailIn(List<String> email);

	@Query("select distinct f.formName from FinalAuditTrackFromSlave f where f.applicantEmail in (:email)")
	Set<String> findDistinctFormNamesForUser(@Param("email") @NotEmpty List<String> email);

	@Query("select distinct f.status from FinalAuditTrackFromSlave f where f.applicantEmail in (:email)")
	Set<String> findDistinctStatusForUser(@Param("email") @NotEmpty List<String> email);

	@Query("select distinct f.formName from FinalAuditTrackFromSlave f where f.caEmail in (:email) or (f.toEmail in (:email) and f.status='ca_pending')")
	Set<String> findDistinctFormNamesForRo(@Param("email") @NotEmpty List<String> email);

	@Query("select distinct f.status from FinalAuditTrackFromSlave f where f.caEmail in (:email) or (f.toEmail in (:email) and f.status='ca_pending')")
	Set<String> findDistinctStatusForRo(@Param("email") @NotEmpty List<String> email);

	@Query("select distinct f.formName from FinalAuditTrackFromSlave f where f.coordinatorEmail in (:email)  or (f.toEmail in (:email) and f.status='coordinator_pending')")
	Set<String> findDistinctFormNamesForCo(@Param("email") @NotEmpty List<String> email);

	@Query("select distinct f.status from FinalAuditTrackFromSlave f where f.coordinatorEmail in (:email)  or (f.toEmail in (:email) and f.status='coordinator_pending')")
	Set<String> findDistinctStatusForCo(@Param("email") @NotEmpty List<String> email);

	Page<FinalAuditTrackFromSlave> findByStatusInAndFormNameIn(List<String> status, List<String> allowedForms,
			Pageable sortedByIdDesc);

	Page<FinalAuditTrackFromSlave> findByFormNameIn(List<String> allowedForms, Pageable sortedByIdDesc);

	int countByStatusInAndFormNameIn(List<String> status_list, List<String> allowedForms);

	int countByFormNameIn(List<String> emailList);

	@Query("select distinct f.status from FinalAuditTrackFromSlave f")
	Set<String> findDistinctStatus();

	@Query(value = "select f from FinalAuditTrackFromSlave f where f.registrationNo like %:keyword%  OR f.applicantEmail like %:keyword%  OR f.status like %:keyword%  OR f.appUserType like %:keyword% OR f.toDatetime like %:keyword% ")
	List<FinalAuditTrackFromSlave> searchByKeyword(@Param("keyword") String keyword);

	@Query(value = "SELECT f from FinalAuditTrackFromSlave f")
	List<FinalAuditTrackFromSlave> finalAuditTrackExportData();

	@Modifying
	@Query("UPDATE FinalAuditTrackFromSlave f SET f.onHold = :onHold, f.holdRemarks = :remarks WHERE  f.registrationNo = :regNumber")
	int updateOnHoldStatus(@Param("regNumber") String registrationNo, @Param("onHold") String onHoldStatus,
			@Param("remarks") String remarks);

	Page<FinalAuditTrackFromSlave> findByOnHold(String onHold, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByCoordinatorEmailInOrStatusAndCoordinatorEmailInOrToEmailIn(List<String> emailList,
			String string, List<String> emailList2, List<String> emailList3, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findBySupportEmailInOrStatusAndSupportEmailInOrToEmailIn(List<String> emailList,
			String string, List<String> emailList2, List<String> emailList3, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByAdminEmailInOrStatusAndAdminEmailInOrToEmailIn(List<String> emailList, String string,
			List<String> emailList2, List<String> emailList3, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByApplicantName(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByCaEmailInOrStatusAndCaEmailInOrToEmailIn(List<String> emailList, String string,
			List<String> emailList2, List<String> emailList3, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByCaName(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByCoordinatorName(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findBySupportName(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByAdminName(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByApplicantMobile(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByCaMobile(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findBySupportMobile(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByCoordinatorMobile(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByAdminMobile(String value, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByStatusInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> status_list, List<String> emailList, String searchKey, String searchKey2, String searchKey3,
			String searchKey5, String searchKey6, String searchKey7, String searchKey8, Pageable sortedByIdDesc);

	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> emailList, String searchKey, String searchKey2, String searchKey3, String searchKey5,
			String searchKey6, String searchKey7, String searchKey8, Pageable sortedByIdDesc);

	Page<FinalAuditTrackFromSlave> findByFormNameInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> service_list, List<String> emailList, String searchKey, String searchKey2, String searchKey3,
			String searchKey5, String searchKey6, String searchKey7, String searchKey8, Pageable sortedByIdDesc);

	Page<FinalAuditTrackFromSlave> findByStatusInAndFormNameInAndApplicantEmailInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> status_list, List<String> service_list, List<String> emailList, String searchKey,
			String searchKey2, String searchKey3, String searchKey5, String statusString, String searchKey6,
			String searchKey7, Pageable sortedByIdDesc);

	Page<FinalAuditTrackFromSlave> findByFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> service_list, String searchKey, String searchKey2, String searchKey3, String searchKey5,
			String searchKey6, String searchKey7, String searchKey8, Pageable sortedByIdDesc);

	Page<FinalAuditTrackFromSlave> findByStatusInAndFormNameInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> status_list, List<String> service_list, String searchKey, String searchKey2, String searchKey3,
			String searchKey5, String statusString, String searchKey6, String searchKey7, Pageable sortedByIdDesc);

	Page<FinalAuditTrackFromSlave> findByStatusInAndApplicantEmailContainingOrApplicantNameContainingOrApplicantMobileContainingOrRegistrationNoContainingOrStatusContainingOrAppUserTypeContainingOrAppCaTypeContaining(
			List<String> status_list, String searchKey, String searchKey2, String searchKey3, String searchKey5,
			String statusString, String searchKey6, String searchKey7, Pageable sortedByIdDesc);

	// new added

	List<FinalAuditTrackFromSlave> findByApplicantEmailAndStatus(String applicantemail, String status);

	List<FinalAuditTrackFromSlave> findByApplicantEmailAndToEmailContainingAndStatus(String applicantemail, String toemail,
			String status);

	List<FinalAuditTrackFromSlave> findByApplicantEmailAndCoordinatorEmailAndStatus(String applicantemail,
			String coordinatoremail, String status);

	List<FinalAuditTrackFromSlave> findByApplicantEmailAndCoordinatorEmailAndStatusNot(String applicantemail,
			String coordinatoremail, String status);

	List<FinalAuditTrackFromSlave> findByApplicantEmailInAndStatusIn(List<String> applicantemail, List<String> status);

	List<FinalAuditTrackFromSlave> findByApplicantEmailInAndToEmailContainingAndStatusIn(List<String> applicantemail,
			String toemail, List<String> status);
	
	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndToEmailContainingAndStatusIn(List<String> applicantemail,
			String toemail, List<String> status, Pageable pageable);

	List<FinalAuditTrackFromSlave> findByApplicantEmailInAndCoordinatorEmailAndStatusIn(List<String> applicantemail,
			String coordinatoremail, List<String> status);

	List<FinalAuditTrackFromSlave> findByApplicantEmailInAndCoordinatorEmailAndStatusNotIn(List<String> applicantemail,
			String coordinatoremail, List<String> status);

	List<FinalAuditTrackFromSlave> findByApplicantEmailInAndHogEmailAndStatusIn(List<String> applicantEmails, String hogEmail,
			List<String> status);
	List<FinalAuditTrackFromSlave> findByApplicantEmailInAndHogEmailInAndStatusIn(List<String> applicantEmails, List<String> hogEmail,
			List<String> status);
	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndHogEmailInAndStatusIn(List<String> applicantEmails, List<String> hogEmail,
			List<String> status,Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndStatusIn(List<String> applicantEmails, List<String> status,
			Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndToEmailInAndStatusIn(List<String> applicantEmails, List<String> coordinatorEmails, List<String> status,
			Pageable pageable);
	
	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndCoordinatorEmailInAndStatusIn(List<String> applicantEmails, List<String> coordinatorEmails, List<String> status,
			Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndToEmailInAndStatusInOrApplicantEmailInAndCoordinatorEmailInAndStatusIn(
			List<String> applicantEmails, List<String> coordinatorEmails, List<String> status_list_Pending,
			List<String> applicantEmails2, List<String> coordinatorEmails2, List<String> status_list_other,
			Pageable pageable);
	
	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndToEmailContainingAndStatusInOrApplicantEmailInAndCoordinatorEmailInAndStatusIn(
			List<String> applicantEmails, String coordEmail, List<String> status_list_Pending,
			List<String> applicantEmails2, List<String> coordinatorEmails2, List<String> status_list_other,
			Pageable pageable);
	
	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndToEmailContainingAndStatusInOrApplicantEmailInAndCoordinatorEmailAndStatusIn(
			List<String> applicantEmails, String coordEmail, List<String> status_list_Pending,
			List<String> applicantEmails2, String coordinatorEmails2, List<String> status_list_other,
			Pageable pageable);
	
	//all records
	int countByApplicantEmailInAndToEmailInAndStatus(List<String> applicantemail,
			List<String> toemail, String status);
	int countByApplicantEmailInAndToEmailContainingAndStatus(List<String> applicantemail,
			String toemail, String status);
	int countByApplicantEmailInAndCoordinatorEmailInAndStatus(List<String> applicantEmailBean, List<String> coordinatorEmail, String status);
	int countByApplicantEmailInAndCoordinatorEmailAndStatus(List<String> applicantEmailBean, String coordinatorEmail, String status);

	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndCoordinatorEmailAndStatusIn(List<String> applicantEmails,
			String coordEmail, List<String> status_list, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByFormNameInAndApplicantEmailInAndStatusIn(List<String> service_list,
			List<String> applicants, List<String> status_list, Pageable sortedByIdDesc);

	Integer countByApplicantEmailInAndCoordinatorEmailInAndStatusIn(List<String> applicantEmails, List<String> aliases,
			List<String> status);

	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndToEmailInOrApplicantEmailInAndStatusIn(
			List<String> applicantEmails, List<String> aliases, List<String> applicantEmails2, List<String> status,
			Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndStatusInAndHogEmailIn(List<String> applicantEmails,
			List<String> status, List<String> aliases, Pageable pageable);

	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndHogEmailInAndStatusInOrApplicantEmailInAndToEmailInAndStatusIn(
			List<String> applicantEmails, List<String> aliasesForHog, List<String> status,
			List<String> applicantEmails2, List<String> aliasesForHog2, List<String> asList, Pageable pageable);

	//@Query(value = "select f from final_audit_track f where (f.applicant_email in (?) and f.hog_email in (?) and f.status in (?)) or (f.applicant_email in (?)) and finalaudit0_.status in (?)))")
	Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndHogEmailInAndStatusInOrApplicantEmailInAndStatusIn(
			List<String> applicantEmails, List<String> aliasesForHog, List<String> status,
			List<String> applicantEmails2, List<String> statusList, Pageable pageable);
	  
}
