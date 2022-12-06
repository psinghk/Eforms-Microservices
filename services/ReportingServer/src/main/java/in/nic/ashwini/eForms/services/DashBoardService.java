package in.nic.ashwini.eForms.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.utils.Constants;

@Service
public class DashBoardService {

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


	public Set<String> convertFormNamesForFilters(Set<String> formNames) {
		Set<String> set = new HashSet<>();
		for (String formName : formNames) {
			if(formName.equalsIgnoreCase("bulk") || formName.equalsIgnoreCase("gem") || formName.equalsIgnoreCase("single") || formName.equalsIgnoreCase("nkn_single") || formName.equalsIgnoreCase("nkn_bulk") || formName.equalsIgnoreCase("email_act") || formName.equalsIgnoreCase("email_deact")) {
				set.add(Constants.EMAIL_REGISTRATION);
			} else if(formName.equalsIgnoreCase("vpn_single") || formName.equalsIgnoreCase("vpn_delete") || formName.equalsIgnoreCase("vpn_renew") || formName.equalsIgnoreCase("vpn_surrender") || formName.equalsIgnoreCase("change_add")) {
				set.add(Constants.VPN_REGISTRATION);
			} else if(formName.equalsIgnoreCase("sms")) {
				set.add(Constants.SMS_REGISTRATION);
			} else if(formName.equalsIgnoreCase("wifi")) {
				set.add(Constants.WIFI_REGISTRATION);
			} else if(formName.equalsIgnoreCase("dns")) {
				set.add(Constants.DNS_REGISTRATION);
			} else if(formName.equalsIgnoreCase("dlist")) {
				set.add(Constants.DISTRIBUTIONLIST_REGISTRATION);
			} else if(formName.equalsIgnoreCase("imappop")) {
				set.add(Constants.IMAPPOP_REGISTRATION);
			} else if(formName.equalsIgnoreCase("ldap")) {
				set.add(Constants.LDAP_REGISTRATION);
			} else if(formName.equalsIgnoreCase("mobile")) {
				set.add(Constants.UPDATEMOBILE_REGISTRATION);
			} else if(formName.equalsIgnoreCase("relay")) {
				set.add(Constants.SMTP_REGISTRATION);
			} else if(formName.equalsIgnoreCase("webcast")) {
				set.add(Constants.WEBCAST_REGISTRATION);
			} else if(formName.equalsIgnoreCase("centralutm")) {
				set.add(Constants.CENTRALUTM_REGISTRATION);
			}
		}
		return set;
	}
	
	public Set<String> setRequestTypes(Set<String> status) {
		Set<String> set = new HashSet<>();
		for (String formName : status) {
			if(formName.contains("_pending") || formName.contains("manual") || formName.contains("api")) {
				set.add(Constants.PENDING_REQUESTS);
			} else if(formName.equalsIgnoreCase("completed")) {
				set.add(Constants.COMPLETED_REQUESTS);
			} else if(formName.equalsIgnoreCase("cancel") || formName.contains("reject")) {
				set.add(Constants.REJECTED_REQUESTS);
			} else if(formName.equalsIgnoreCase("cancel")) {
				set.add(Constants.CANCELLED_REQUESTS);
			} else if(formName.contains("_pending") || formName.contains("manual") || formName.contains("api") || formName.equalsIgnoreCase("completed")) {
				set.add(Constants.FORWARDED_REQUESTS);
			}
		}
		return set;
	}
	
	
}
