package in.nic.ashwini.eForms.db.master.entities.projections;

import java.time.LocalDateTime;

public interface FinalAuditTrackBasic {
	String getStatus();
	String getSenderType();
	String getRecipientType();
	String getApplicantEmail();
	String getApplicantMobile();
	String getApplicantName();
	String getApplicantIp();
	String getApplicantDatetime();
	String getApplicantRemarks();
	String getToEmail();
	LocalDateTime getToDatetime();
	String getFormName();
}
