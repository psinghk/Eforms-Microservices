package in.nic.ashwini.eForms.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.nic.ashwini.eForms.db.master.entities.FinalAuditTrack;
import in.nic.ashwini.eForms.db.master.entities.Status;
import in.nic.ashwini.eForms.db.master.repositories.FinalAuditTrackRepository;
import in.nic.ashwini.eForms.db.master.repositories.StatusRepository;
import in.nic.ashwini.eForms.db.slave.entities.FinalAuditTrackFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.StatusFromSlave;
import in.nic.ashwini.eForms.db.slave.entities.TrackStatusFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.FinalAuditTrackRepositoryToRead;
import in.nic.ashwini.eForms.db.slave.repositories.StatusRepositoryToRead;

@Service
public class TrackService {

	private final StatusRepository statusRepository;
	private final FinalAuditTrackRepository finalAuditTrackRepository;
	private final StatusRepositoryToRead statusRepositoryToRead;
	private final FinalAuditTrackRepositoryToRead finalAuditTrackRepositoryToRead;
	private final UtilityService utilityService;

	@Autowired
	public TrackService(StatusRepository statusRepository, FinalAuditTrackRepository finalAuditTrackRepository,
			UtilityService utilityService, StatusRepositoryToRead statusRepositoryToRead,
			FinalAuditTrackRepositoryToRead finalAuditTrackRepositoryToRead) {
		super();
		this.statusRepository = statusRepository;
		this.finalAuditTrackRepository = finalAuditTrackRepository;
		this.statusRepositoryToRead = statusRepositoryToRead;
		this.finalAuditTrackRepositoryToRead = finalAuditTrackRepositoryToRead;
		this.utilityService = utilityService;
	}

	public List<String> fetchRoles(String registrationNo) {
		List<String> roles = new ArrayList<>();
		List<String> finalRoles = new ArrayList<>();
		int i = 0;
		String statusType = "";
		List<StatusFromSlave> trackStatus = statusRepositoryToRead.findByRegistrationNoOrderById(registrationNo);
		for (StatusFromSlave status : trackStatus) {
			if (status.getStatus().contains("api") || status.getStatus().contains("manual")) {
				roles.add("a=>a");
			} else {
				if ((statusType.contains("api") || status.getStatus().contains("manual"))
						&& status.getSenderType() == null) {
					roles.add("a=>" + status.getRecipientType());
				} else {
					roles.add(status.getSenderType() + "=>" + status.getRecipientType());
				}
			}
			if ((trackStatus.size() == i) && i > 0) {
				roles.add(status.getRecipientType());
			}
			++i;
			statusType = status.getStatus();
		}
		Collections.reverse(roles);
		for (String arrRole : roles) {
			if (arrRole != null) {
				finalRoles.add(arrRole);
			}
		}
		Collections.reverse(finalRoles);
		return finalRoles;
	}

	public FinalAuditTrackFromSlave fetchCompleteDetails(String registrationNo) {
		return finalAuditTrackRepositoryToRead.findByRegistrationNo(registrationNo);
	}

	public String generateMessageForTrack(LocalDateTime approvedDateTime, String recipientEmail, String currentStatus,
			String registrationNo, String loggedInEmail) {
		String statusMsg = "";
		if (currentStatus.contains("cancel") && recipientEmail == null) {
			statusMsg = getStatTypeString(currentStatus) + "(" + loggedInEmail + ")";
		} else {
			statusMsg = getStatTypeString(currentStatus) + "(" + recipientEmail + ")";
		}
		String appDateText = "";
		if (currentStatus.contains("pending")) {
			appDateText = "Pending Since ";
		} else if (currentStatus.contains("rejected")) {
			appDateText = "Rejection Date";
		} else if (currentStatus.contains("cancel")) {
			appDateText = "Cancellation Date";
		} else if (currentStatus.contains("completed")) {
			appDateText = "Completion Date";
		} else {
			appDateText = "Submission Date";
		}

		Optional<StatusFromSlave> statusTable = statusRepositoryToRead
				.findFirstByRegistrationNoOrderByIdDesc(registrationNo);
		StatusFromSlave status = null;
		if (statusTable.isPresent()) {
			status = statusTable.orElse(null);
		}
		String senderEmail = "";
		String senderMobile = "";
		String senderName = "";
		String forwardedBy = "";
		String msg = "";
		LocalDateTime submitTime = null;
		if (status != null) {
			senderEmail = status.getSenderEmail();
			senderMobile = status.getSenderMobile();
			senderName = status.getSenderName();
			submitTime = status.getSenderDatetime();
			forwardedBy = status.getSenderType();

			if ((senderEmail == null) || (senderEmail != null && senderEmail.isEmpty())) {
				senderEmail = status.getSender();
			}

			if ((senderEmail == null) || (senderEmail != null && senderEmail.isEmpty())) {
				if (forwardedBy != null && forwardedBy.equalsIgnoreCase("a")) {
					senderEmail = loggedInEmail;
					senderName = "Yourself ";
				}
			}
		}

		if (senderEmail != null && senderName != null) {
			if (senderEmail.isEmpty() && senderName.isEmpty()) {
				msg = "Status->" + statusMsg + "=>" + appDateText + "->" + approvedDateTime + "=>Sender Details->";
			} else if (senderEmail.isEmpty() && !senderName.isEmpty()) {
				msg = "Status->" + statusMsg + "=>" + appDateText + "->" + approvedDateTime + "=>Sender Details->"
						+ senderName;
			} else if (senderName.isEmpty() && !senderEmail.isEmpty()) {
				msg = "Status->" + statusMsg + "=>" + appDateText + "->" + approvedDateTime + "=>Sender Details->"
						+ senderEmail;
			} else {
				msg = "Status->" + statusMsg + "=>" + appDateText + "->" + approvedDateTime + "=>Sender Details->"
						+ senderName + "(" + senderEmail + ")";
			}
		} else if (senderEmail == null && senderName != null) {
			if (senderName.isEmpty()) {
				msg = "Status->" + statusMsg + "=>" + appDateText + "->" + approvedDateTime + "=>Sender Details-> ";
			} else {
				msg = "Status->" + statusMsg + "=>" + appDateText + "->" + approvedDateTime + "=>Sender Details->"
						+ senderName;
			}
		} else if (senderName == null && senderEmail != null) {
			if (senderEmail.isEmpty()) {
				msg = "Status->" + statusMsg + "=>" + appDateText + "->" + approvedDateTime + "=>Sender Details-> ";
			} else {
				msg = "Status->" + statusMsg + "=>" + appDateText + "->" + approvedDateTime + "=>Sender Details->"
						+ senderEmail;
			}
		} else if (senderEmail == null && senderName == null) {
			msg = "Status->" + statusMsg + "=>" + appDateText + "->" + approvedDateTime + "=>Sender Details-> ";
		}

		return msg;
	}

	public String getStatTypeString(String stat_type) {
		switch (stat_type) {
		case "ca_pending":
			return Constants.CA_PENDING;
		case "ca_rejected":
			return Constants.CA_REJECTED;
		case "support_pending":
			return Constants.SUPPORT_PENDING;
		case "support_rejected":
			return Constants.SUPPORT_REJECTED;
		case "coordinator_pending":
			return Constants.COORDINATOR_PENDING;
		case "hog_pending":
			return Constants.HOG_PENDING;
		case "hog_rejected":
			return Constants.HOG_REJECTED;
		case "coordinator_rejected":
			return Constants.COORDINATOR_REJECTED;
		case "completed":
			return Constants.COMPLETED;
		case "cancel":
			return Constants.CANCEL;
		case "mail-admin_pending":
			return Constants.MAIL_ADMIN_PENDING;
		case "mail-admin_rejected":
			return Constants.MAIL_ADMIN_REJECTED;
		case "da_pending":
			return Constants.DA_PENDING;
		case "da_rejected":
			return Constants.DA_REJECTED;
		case "api":
			return Constants.PENDING_API;
		case "domainapi":
			return Constants.PENDING_API;
		case "manual_upload":
			return Constants.manual_upload;
		case "us_pending":
			return Constants.US_PENDING;
		case "us_rejected":
			return Constants.US_REJECTED;
		case "us_expired":
			return Constants.US_EXPIRED;
		default:
			return "";
		}
	}

	public String fetchCurrentStatusByRole(String sRole, String tRole, String registrationNo, String forward,
			String loggedInEmail, Integer position) {
		Optional<StatusFromSlave> statusTable = null;
		List<StatusFromSlave> completeStatusTable = statusRepositoryToRead.findByRegistrationNoOrderById(registrationNo);
		StatusFromSlave liveTrack = completeStatusTable.get(position);
		sRole = liveTrack.getSenderType() != null ? liveTrack.getSenderType() : "";
		String status = "", currentStatus = "", remarks = "", sender_details = "", msg = "", role_app = "", reqProcessedAs = "", app_date_text = "", email = "";
		String current_user = "", forwarder = "", stat_process = "", forwardedBy = "", recv_email= "";
		LocalDateTime recv_date = null;
		String recvDateInString = "";
		Long statId = -1l;
		tRole = liveTrack.getRecipientType() != null ? liveTrack.getRecipientType() : "";
		
		if (sRole.isEmpty()) {
			role_app = "yourself";
		} else if (sRole.equals("h")) {
			role_app = "HOG";
		} else if (sRole.equals("a")) {
			role_app = "Applicant";
		} else if (sRole.matches("c")) {
			role_app = "Coordinator";
		} else if (sRole.matches("ca")) {
			role_app = "Reporting/Forwarding/Nodal Officer";
		} else if (sRole.equals("d")) {
			role_app = "DA-Admin";
		} else if (sRole.equals("m")) {
			role_app = "Admin";
		} else if (sRole.equals("s")) {
			role_app = "Support";
		} else if (sRole.equals("us")) {
			role_app = "Under Secretary";
		}

		if (forward.isEmpty()) {
			forwarder = "yourself";
		} else if (forward.equals("a")) {
			forwarder = "Applicant";
		} else if (forward.matches("c")) {
			forwarder = "Coordinator";
		} else if (forward.matches("ca")) {
			forwarder = "Reporting/Forwarding/Nodal Officer";
		} else if (forward.equals("d")) {
			forwarder = "DA-Admin";
		} else if (forward.equals("m")) {
			forwarder = "Admin";
		} else if (forward.equals("s")) {
			forwarder = "Support";
		} else if (forward.equals("us")) {
			forwarder = "Under Secretary";
		} else if (forward.equals("h")) {
			forwarder = "HOG";
		}

		
		statusTable = Optional.ofNullable(liveTrack);
		
		StatusFromSlave status1 = null;
		if (statusTable.isPresent()) {
			status1 = statusTable.orElse(null);
		}

		if (status1 != null) {
			statId = status1.getId();
			remarks = status1.getRemarks();
			if (remarks == null) {
				remarks = "";
			}
			String toEmail = finalAuditTrackRepositoryToRead.findToEmail(registrationNo);

			recv_date = status1.getCreatedon();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			recvDateInString = recv_date.format(formatter);

			current_user = status1.getSenderEmail() != null ? status1.getSenderEmail() : "";
			forwardedBy = status1.getSenderType() != null ? status1.getSenderType() : "";
			recv_email = status1.getRecipient() != null ? status1.getRecipient() : "";
			stat_process = status1.getSubmissionType() != null ? status1.getSubmissionType() : "";
			currentStatus = status1.getStatus().toLowerCase();

			if ((current_user == null) || (current_user != null && current_user.isEmpty())) {
				if (forwardedBy.equalsIgnoreCase("a")) {
					current_user = loggedInEmail;
				}
			}

			if ((current_user == null) || (current_user != null && current_user.isEmpty())) {
				sender_details = forwarder + "&ensp;";
			} else {
				sender_details = forwarder + "&ensp;(" + current_user + ")";
			}

			if (forward.isEmpty()) {
				if (currentStatus.contains("pending") && sRole.equalsIgnoreCase("a")) {
					reqProcessedAs = "Approved";
					app_date_text = "Submission Date->" + recv_date;
					if ((current_user == null) || (current_user != null && current_user.isEmpty())) {
						status = "Form has been successfully submitted by you and forwarded to (" + findRole(tRole)
								+ ")";
					} else {
						status = "Form has been successfully submitted by you (" + current_user + ") and forwarded to ("
								+ findRole(tRole) + ")";
					}
				} else if (currentStatus.contains("api") && sRole.equalsIgnoreCase("a")) {
					reqProcessedAs = "Pending";
					app_date_text = "Submission Date->" + recv_date;
					if ((current_user == null) || (current_user != null && current_user.isEmpty())) {
						status = "You have got a request from Domain registrar. Request is pending with you only.";
					} else {
						status = "You have got a request from Domain registrar. Request is pending with you only. ("
								+ current_user + ")";
					}
				} else if (currentStatus.contains("manual") && sRole.equalsIgnoreCase("a")) {
					reqProcessedAs = "Pending";
					app_date_text = "Submission Date->" + recv_date;
					if ((current_user == null) || (current_user != null && current_user.isEmpty())) {
						status = "You have chosen manual option to submit. Hence, request is pending with you only.";
					} else {
						status = "You have chosen manual option to submit. Hence, request is pending with you only. ("
								+ current_user + ")";
					}
				} else if (currentStatus.contains("cancel") && sRole.equalsIgnoreCase("a")) {
					reqProcessedAs = "Cancelled";
					app_date_text = "Cancellation Date->" + recv_date;
					if ((current_user == null) || (current_user != null && current_user.isEmpty())) {
						status = "Request has been cancelled by you.";
					} else {
						status = "Request has been cancelled by you (" + current_user + ").";
					}
				}
				recvDateInString = "";
			} else if (tRole.equalsIgnoreCase("undefined") || tRole.isEmpty()) {
				if (currentStatus.contains("pending")) {
					if (status1.getOnholdStatus().toLowerCase().equalsIgnoreCase("y")) {
						reqProcessedAs = "On Hold";
						app_date_text = "On Hold Since->" + recv_date;
					} else {
						if (toEmail != null && !toEmail.isEmpty()) {
							status = "Pending with " + role_app + "(" + toEmail + ")";
						} else {
							status = "Pending with " + role_app;
						}
						app_date_text = "Pending Since->" + recv_date;
					}
				} else if (currentStatus.contains("rejected")) {
					if (toEmail != null && !toEmail.isEmpty()) {
						status = "Rejected by " + role_app + "(" + toEmail + ")";
					} else {
						status = "Rejected by " + role_app;
					}
					app_date_text = "Rejection Date->" + recv_date;
				} else if (currentStatus.equalsIgnoreCase("completed")) {
					if (toEmail != null && !toEmail.isEmpty()) {
						status = "Completed by " + role_app + "(" + toEmail + ")";
					} else {
						status = "Completed by " + role_app;
					}
					app_date_text = "Completion Date->" + recv_date;
				}
			} else if (currentStatus.contains("pending")) {
				if (!(stat_process == null || stat_process.isEmpty())) {
					String[] aa = stat_process.split("_");
					String process = aa[0];
					String actionBy = aa[1];
					String actionFor = aa[2];

					if (process.equalsIgnoreCase("pulled")) {
						status = "Pulled by " + findRole(actionBy) + " from " + findRole(actionFor);
					} else if (process.equalsIgnoreCase("reverted")) {
						status = "Reverted by " + findRole(actionBy) + " to " + findRole(actionFor);
					} else if (process.equalsIgnoreCase("forwarded")) {
						status = "Forwarded by " + findRole(actionBy) + " to " + findRole(actionFor);
					}
				} else if (forward.equalsIgnoreCase("a") && sRole.equalsIgnoreCase("a")) {
					if (current_user == null) {
						status = "Submitted by " + role_app + " and forwarded to (" + findRole(tRole) + ")";
					} else {
						if (current_user.isEmpty()) {
							status = "Submitted by " + role_app + " and forwarded to (" + findRole(tRole) + ")";
						} else {
							status = "Submitted by " + role_app + "(" + current_user + ") and forwarded to ("
									+ findRole(tRole) + ")";
						}
					}
					app_date_text = "Submission Date->" + recv_date;
				} else {
					if (current_user == null) {
						status = "Approved by " + role_app + " and forwarded to (" + findRole(tRole) + ")";
					} else {
						if (current_user.isEmpty()) {
							status = "Approved by " + role_app + " and forwarded to (" + findRole(tRole) + ")";
						} else {
							if (sRole.equals("c") && tRole.equals("h")) {
								status = "Pull from Coordinator and forwarded to HOG (" + current_user + ")";
							} else {
								status = "Approved by " + role_app + "(" + current_user + ") and forwarded to ("
										+ findRole(tRole) + ")";
							}

						}
					}
					app_date_text = "Approving Date->" + recv_date;
				}
			}

			if (!forward.isEmpty()) {
				if (statId > 0) {
					statusTable = statusRepositoryToRead.findById(statId);
				}

				// statusTable =
				// statusRepositoryToRead.findByRegistrationNoForTrack(registrationNo, forward,
				// sRole);
				if (statusTable.isPresent()) {
					status1 = statusTable.orElse(null);
				} else {
					status1 = null;
				}
				if (status1 != null) {
					recv_date = status1.getCreatedon();
					recvDateInString = recv_date.format(formatter);
					current_user = status1.getSenderEmail() != null ? status1.getSenderEmail() : "";
					forwardedBy = status1.getSenderType() != null ? status1.getSenderType() : "";

					if ((current_user == null) || (current_user != null && current_user.isEmpty())) {
						current_user = status1.getSenderEmail();
					}

					if ((current_user == null) || (current_user != null && current_user.isEmpty())) {
						if (forwardedBy.equalsIgnoreCase("a")) {
							current_user = loggedInEmail;
						}
					}

					if ((current_user == null) || (current_user != null && current_user.isEmpty())) {
						sender_details = forwarder + "&ensp;";
					} else {
						sender_details = forwarder + "&ensp;(" + current_user + ")";
					}
				}
			}
		}
		if (status.isEmpty()) {
			FinalAuditTrackFromSlave finalAuditTrack = finalAuditTrackRepositoryToRead
					.findByRegistrationNo(registrationNo);
			if (finalAuditTrack.getStatus().contains("cancel")) {
				status = "Application cancelled by User.";
				msg = "Status->" + status + "=>Cancellation Date->" + finalAuditTrack.getToDatetime()
						+ "=>Cancelled by->Yourself(" + loggedInEmail + ")";
			} else {
				msg = "Status->Please check the inputs provided by you.";
			}
		} else {
			msg = "Status->" + status + "=>Receiving Date->" + recv_date + "=>" + app_date_text + "=>Remarks->"
					+ remarks + "=>Sender Details->" + sender_details;
		}
		return msg;
	}

	public String findRole(String forward) {
		if (forward.equals("")) {
			return "yourself";
		} else if (forward.equals("a")) {
			return "Applicant";
		} else if (forward.matches("c")) {
			return "Coordinator";
		} else if (forward.matches("ca")) {
			return "Reporting/Forwarding/Nodal Officer";
		} else if (forward.equals("d")) {
			return "DA-Admin";
		} else if (forward.equals("m")) {
			return "Admin";
		} else if (forward.equals("s")) {
			return "Support";
		} else if (forward.equals("us")) {
			return "Under Secretary";
		} else {
			return "";
		}
	}

	public List<FinalAuditTrackFromSlave> searchByKeyword(String registrationNo) {
		return finalAuditTrackRepositoryToRead.searchByKeyword(registrationNo);
	}

	public List<FinalAuditTrackFromSlave> finalAuditTrackExportData() {
		return finalAuditTrackRepositoryToRead.finalAuditTrackExportData();
	}

	// @Transactional(rollbackFor = Exception.class)
	public boolean putOnHold(@NotEmpty String regNumber, @NotEmpty String remarks, @NotEmpty String onHoldStatus) {
		if (finalAuditTrackRepository.updateOnHoldStatus(regNumber, onHoldStatus, remarks) > 0)
			return true;
		return false;
	}

	public Page<FinalAuditTrackFromSlave> fetchForms(List<String> allowedForms, String by, String value, String forRole,
			Pageable pageable) {
		switch (by.toLowerCase()) {
		case "email":
			List<String> emailList = utilityService.aliases(value);
			switch (forRole) {
			case "user":
				return finalAuditTrackRepositoryToRead.findByApplicantEmailIn(emailList, pageable);
			case "ro":
				return finalAuditTrackRepositoryToRead.findByCaEmailInOrStatusAndCaEmailInOrToEmailIn(emailList,
						"ca_pending", emailList, emailList, pageable);
			case "coord":
				return finalAuditTrackRepositoryToRead.findByCoordinatorEmailInOrStatusAndCoordinatorEmailInOrToEmailIn(
						emailList, "coordinator_pending", emailList, emailList, pageable);
			case "support":
				return finalAuditTrackRepositoryToRead.findBySupportEmailInOrStatusAndSupportEmailInOrToEmailIn(
						emailList, "support_pending", emailList, emailList, pageable);
			case "admin":
				return finalAuditTrackRepositoryToRead.findByAdminEmailInOrStatusAndAdminEmailInOrToEmailIn(emailList,
						"mail-admin_pending", emailList, emailList, pageable);
			default:
				return null;
			}
		case "name":
			switch (forRole) {
			case "user":
				return finalAuditTrackRepositoryToRead.findByApplicantName(value, pageable);
			case "ro":
				return finalAuditTrackRepositoryToRead.findByCaName(value, pageable);
			case "coord":
				return finalAuditTrackRepositoryToRead.findByCoordinatorName(value, pageable);
			case "support":
				return finalAuditTrackRepositoryToRead.findBySupportName(value, pageable);
			case "admin":
				return finalAuditTrackRepositoryToRead.findByAdminName(value, pageable);
			default:
				break;
			}
			return null;
		case "mobile":
			switch (forRole) {
			case "user":
				return finalAuditTrackRepositoryToRead.findByApplicantMobile(value, pageable);
			case "ro":
				return finalAuditTrackRepositoryToRead.findByCaMobile(value, pageable);
			case "coord":
				return finalAuditTrackRepositoryToRead.findByCoordinatorMobile(value, pageable);
			case "support":
				return finalAuditTrackRepositoryToRead.findBySupportMobile(value, pageable);
			case "admin":
				return finalAuditTrackRepositoryToRead.findByAdminMobile(value, pageable);
			default:
				break;
			}
			return null;
		default:
			break;
		}
		return null;
	}

	// newly added

	public List<FinalAuditTrackFromSlave> findByApplicantEmailAndStatus(String email, String status) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailAndStatus(email, status);
	}

	public List<FinalAuditTrackFromSlave> findByApplicantEmailAndToEmailAndStatus(String applicantemail, String toemail,
			String status) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailAndToEmailContainingAndStatus(applicantemail,
				toemail, status);
	}

	public List<FinalAuditTrackFromSlave> findByApplicantEmailAndCoordinatorEmailAndStatus(String applicantemail,
			String coordemail, String status) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailAndCoordinatorEmailAndStatus(applicantemail,
				coordemail, status);
	}

	public List<FinalAuditTrackFromSlave> findByApplicantEmailAndCoordinatorEmailAndStatusNot(String applicantemail,
			String coordemail, String status) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailAndCoordinatorEmailAndStatusNot(applicantemail,
				coordemail, status);
	}

	public FinalAuditTrack savedata(FinalAuditTrack finalAuditTrack) {
		return finalAuditTrackRepository.save(finalAuditTrack);
	}

	public Status saveDataStatus(Status status) {
		return statusRepository.save(status);
	}

	public FinalAuditTrackFromSlave findByRegistrationNo(String regno) {

		FinalAuditTrackFromSlave retval = finalAuditTrackRepositoryToRead.findByRegistrationNo(regno);
		return retval;
	}

	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndStatusIn(List<String> email, List<String> status) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndStatusIn(email, status);
	}

	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndToEmailAndStatusIn(List<String> email,
			String toEmail, List<String> status) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndToEmailContainingAndStatusIn(email, toEmail,
				status);
	}

	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndCoordinatorEmailAndStatusIn(List<String> email,
			String toEmail, List<String> status) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndCoordinatorEmailAndStatusIn(email, toEmail,
				status);
	}

	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndCoordinatorEmailAndStatusNotIn(List<String> email,
			String toEmail, List<String> status) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndCoordinatorEmailAndStatusNotIn(email, toEmail,
				status);
	}

	public List<FinalAuditTrackFromSlave> findByApplicantEmailInAndHogEmailAndStatusIn(List<String> applicantEmails,
			String hogEmail, List<String> status) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndHogEmailAndStatusIn(applicantEmails, hogEmail,
				status);
	}

	public Integer countByApplicantEmailInAndHogEmailAndStatusIn(List<String> applicantEmails, String hogEmail,
			List<String> status) {
		List<String> aliasesForHog = utilityService.aliases(hogEmail);
		return finalAuditTrackRepositoryToRead
				.findByApplicantEmailInAndHogEmailInAndStatusIn(applicantEmails, aliasesForHog, status).size();
	}

	public Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndStatusIn(List<String> applicantEmails,
			List<String> status, Pageable pageable) {
		return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndStatusIn(applicantEmails, status, pageable);
	}

	public Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndHogEmailAndStatusIn(List<String> applicantEmails,
			String hogEmail, List<String> status, Pageable pageable) {
		List<String> aliasesForHog = utilityService.aliases(hogEmail);
		if (status.containsAll(Arrays.asList("completed", "hog_pending", "hog_rejected"))) {
			return finalAuditTrackRepositoryToRead
					.findByApplicantEmailInAndHogEmailInAndStatusInOrApplicantEmailInAndToEmailInAndStatusIn(
							applicantEmails, aliasesForHog, status, applicantEmails, aliasesForHog,
							Arrays.asList("hog_pending"), pageable);
		}
		return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndHogEmailInAndStatusIn(applicantEmails,
				aliasesForHog, status, pageable);
	}

	public Integer countByApplicantEmailInAndHogEmailInAndStatusIn(List<String> applicantEmails, List<String> aliases,
			List<String> status) {
		return finalAuditTrackRepositoryToRead.countByApplicantEmailInAndCoordinatorEmailInAndStatusIn(applicantEmails,
				aliases, status);
	}

	public Page<FinalAuditTrackFromSlave> findHogRequests(List<String> applicantEmails, List<String> status,
			List<String> aliases, Pageable pageable) {
		if (status.contains("any")) {
			return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndToEmailInOrApplicantEmailInAndStatusIn(
					applicantEmails, aliases, applicantEmails, status, pageable);
		} else {
			return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndStatusInAndHogEmailIn(applicantEmails,
					status, aliases, pageable);
		}

	}

	public Page<FinalAuditTrackFromSlave> findByApplicantEmailInAndStatusInOrApplicantEmailInAndHogEmailAndStatusIn(
			List<String> applicantEmails, List<String> statusList, List<String> applicantEmails2, String hogEmail,
			List<String> status, Pageable pageable) {
		List<String> aliasesForHog = utilityService.aliases(hogEmail);
			return finalAuditTrackRepositoryToRead.findByApplicantEmailInAndHogEmailInAndStatusInOrApplicantEmailInAndStatusIn(applicantEmails, aliasesForHog, status, applicantEmails, statusList,pageable);
		
	}
}
