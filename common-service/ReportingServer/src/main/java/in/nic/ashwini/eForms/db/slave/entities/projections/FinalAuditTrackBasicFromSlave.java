package in.nic.ashwini.eForms.db.slave.entities.projections;

import java.time.LocalDateTime;

public interface FinalAuditTrackBasicFromSlave {
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
