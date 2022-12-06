package in.nic.ashwini.eForms.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.nic.ashwini.eForms.entities.FinalAuditTrack;
import in.nic.ashwini.eForms.entities.Status;
import in.nic.ashwini.eForms.entities.projections.TrackStatus;
import in.nic.ashwini.eForms.repositories.FinalAuditTrackRepository;
import in.nic.ashwini.eForms.repositories.StatusRepository;

@Service
public class TrackService {

	private final StatusRepository statusRepository;
	private final FinalAuditTrackRepository finalAuditTrackRepository;
	private final UtilityService utilityService;

	@Autowired
	public TrackService(StatusRepository statusRepository, FinalAuditTrackRepository finalAuditTrackRepository,
			UtilityService utilityService) {
		super();
		this.statusRepository = statusRepository;
		this.finalAuditTrackRepository = finalAuditTrackRepository;
		this.utilityService = utilityService;
	}

	public List<String> fetchRoles(String registrationNo) {
		List<String> roles = new ArrayList<>();
		List<String> finalRoles = new ArrayList<>();
		int i = 0;
		List<TrackStatus> trackStatus = statusRepository.findByRegistrationNoOrderById(registrationNo);
		for (TrackStatus status : trackStatus) {
			roles.add(status.getSenderType() + "=>" + status.getRecipientType());
			if ((trackStatus.size() == i) && i > 0) {
				roles.add(status.getRecipientType());
			}
			++i;
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

	public FinalAuditTrack fetchCompleteDetails(String registrationNo) {
		return finalAuditTrackRepository.findByRegistrationNo(registrationNo);
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

		Optional<Status> statusTable = statusRepository.findFirstByRegistrationNoOrderByIdDesc(registrationNo);
		Status status = null;
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
			String loggedInEmail) {
		String status = "", currentStatus = "", recv_email = "", app_date = "", remarks = "", sender_details = "",
				msg = "", role_app = "", reqProcessedAs = "", app_date_text = "", email = "";
		String current_user = "", forwarder = "", stat_process = "", forwardedBy = "";
		LocalDateTime recv_date = null;
		String recvDateInString = "";

		if (sRole.isEmpty()) {
			role_app = "yourself";
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
		}

		Optional<Status> statusTable = null;

		if (tRole.equalsIgnoreCase("undefined")) {
			statusTable = statusRepository.findFirstByRegistrationNoAndRecipientTypeOrderById(registrationNo, sRole);
		} else if (sRole.equalsIgnoreCase("null") && tRole.equalsIgnoreCase("null")) {
			statusTable = statusRepository
					.findFirstByRegistrationNoAndSenderTypeIsNullAndRecipientTypeIsNullOrderById(registrationNo);
		} else if (tRole.equalsIgnoreCase("null")) {
			statusTable = statusRepository
					.findFirstByRegistrationNoAndSenderTypeAndRecipientTypeIsNullOrderById(registrationNo, sRole);
		} else {
			statusTable = statusRepository.findFirstByRegistrationNoAndSenderTypeAndRecipientType(registrationNo, sRole,
					tRole);
		}
		Status status1 = null;
		if (statusTable.isPresent()) {
			status1 = statusTable.orElse(null);
		}

		if (status1 != null) {
			remarks = status1.getRemarks();
			if (remarks == null) {
				remarks = "";
			}
			String toEmail = finalAuditTrackRepository.findToEmail(registrationNo);

			recv_date = status1.getCreatedon();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			recvDateInString = recv_date.format(formatter);

			current_user = status1.getSenderEmail();
			forwardedBy = status1.getSenderType();
			recv_email = status1.getRecipient();
			stat_process = status1.getSubmissionType();
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
							status = "Approved by " + role_app + "(" + current_user + ") and forwarded to ("
									+ findRole(tRole) + ")";
						}
					}
					app_date_text = "Approving Date->" + recv_date;
				}
			}

			if (!forward.isEmpty()) {
				statusTable = statusRepository.findByRegistrationNoForTrack(registrationNo, forward, sRole);
				if (statusTable.isPresent()) {
					status1 = statusTable.orElse(null);
				} else {
					status1 = null;
				}
				if (status1 != null) {
					recv_date = status1.getCreatedon();
					recvDateInString = recv_date.format(formatter);
					current_user = status1.getSenderEmail();
					forwardedBy = status1.getSenderType();

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
			FinalAuditTrack finalAuditTrack = finalAuditTrackRepository.findByRegistrationNo(registrationNo);
			if (finalAuditTrack.getStatus().contains("cancel")) {
				status = "Application cancelled by User.";
				msg = "Status->" + status + "=>Cancellation Date->" + finalAuditTrack.getToDatetime()
						+ "=>Cancelled by->Yourself(" + loggedInEmail + ")";
			}else {
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

	public List<FinalAuditTrack> searchByKeyword(String registrationNo) {
		return finalAuditTrackRepository.searchByKeyword(registrationNo);
	}

	public List<FinalAuditTrack> finalAuditTrackExportData() {
		return finalAuditTrackRepository.finalAuditTrackExportData();
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean putOnHold(@NotEmpty String regNumber, @NotEmpty String remarks, @NotEmpty String onHoldStatus) {
		if (finalAuditTrackRepository.updateOnHoldStatus(regNumber, onHoldStatus, remarks) > 0)
			return true;
		return false;
	}

	public Page<FinalAuditTrack> fetchForms(List<String> allowedForms, String by, String value, String forRole,
			Pageable pageable) {
		switch (by.toLowerCase()) {
		case "email":
			List<String> emailList = utilityService.aliases(value);
			switch (forRole) {
			case "user":
				return finalAuditTrackRepository.findByApplicantEmailIn(emailList, pageable);
			case "ro":
				return finalAuditTrackRepository.findByCaEmailInOrStatusAndCaEmailInOrToEmailIn(emailList, "ca_pending",
						emailList, emailList, pageable);
			case "coord":
				return finalAuditTrackRepository.findByCoordinatorEmailInOrStatusAndCoordinatorEmailInOrToEmailIn(
						emailList, "coordinator_pending", emailList, emailList, pageable);
			case "support":
				return finalAuditTrackRepository.findBySupportEmailInOrStatusAndSupportEmailInOrToEmailIn(emailList,
						"support_pending", emailList, emailList, pageable);
			case "admin":
				return finalAuditTrackRepository.findByAdminEmailInOrStatusAndAdminEmailInOrToEmailIn(emailList,
						"mail-admin_pending", emailList, emailList, pageable);
			default:
				return null;
			}
		case "name":
			switch (forRole) {
			case "user":
				return finalAuditTrackRepository.findByApplicantName(value, pageable);
			case "ro":
				return finalAuditTrackRepository.findByCaName(value, pageable);
			case "coord":
				return finalAuditTrackRepository.findByCoordinatorName(value, pageable);
			case "support":
				return finalAuditTrackRepository.findBySupportName(value, pageable);
			case "admin":
				return finalAuditTrackRepository.findByAdminName(value, pageable);
			default:
				break;
			}
			return null;
		case "mobile":
			switch (forRole) {
			case "user":
				return finalAuditTrackRepository.findByApplicantMobile(value, pageable);
			case "ro":
				return finalAuditTrackRepository.findByCaMobile(value, pageable);
			case "coord":
				return finalAuditTrackRepository.findByCoordinatorMobile(value, pageable);
			case "support":
				return finalAuditTrackRepository.findBySupportMobile(value, pageable);
			case "admin":
				return finalAuditTrackRepository.findByAdminMobile(value, pageable);
			default:
				break;
			}
			return null;
		default:
			break;
		}
		return null;
	}
}
