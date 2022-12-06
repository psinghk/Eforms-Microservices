package in.nic.ashwini.eForms.services;

public class Constants {
	public static final String BULK_REGISTRATION_NO_FORMAT = "bulkuser-form";
	public static final String SINGLE_REGISTRATION_NO_FORMAT = "singleuser-form";
	public static final String NKN_BULK_REGISTRATION_NO_FORMAT = "nkn-bulk-form";
	public static final String NKN_SINGLE_REGISTRATION_NO_FORMAT = "nkn-form";
	public static final String GEM_REGISTRATION_NO_FORMAT = "gem-form";
	public static final String IMAPPOP_REGISTRATION_NO_FORMAT = "imappop-form";
	public static final String MOBILE_REGISTRATION_NO_FORMAT = "mobile-form";
	public static final String LDAP_REGISTRATION_NO_FORMAT = "ldap-form";
	public static final String RELAY_REGISTRATION_NO_FORMAT = "relay-form";
	public static final String DLIST_REGISTRATION_NO_FORMAT = "dlist-form";
	public static final String SMS_REGISTRATION_NO_FORMAT = "sms-form";
	public static final String DNS_REGISTRATION_NO_FORMAT = "dns-form";
	public static final String VPN_REGISTRATION_NO_FORMAT = "vpn-form";
	public static final String VPN_ADD_REGISTRATION_NO_FORMAT = "vpnadd-form";
	public static final String VPN_DELETE_REGISTRATION_NO_FORMAT = "vpndelete-form";
	public static final String VPN_RENEW_REGISTRATION_NO_FORMAT = "vpnrenew-form";
	public static final String WIFI_REGISTRATION_NO_FORMAT = "wifi-form";
	
	public static final String PROFILE_TABLE = "user_profile";
    public static final String STATUS_TABLE = "status";
    public static final String ADMIN_TABLE = "support_individual_login";
    public static final String SUPPORT_TABLE = "support_ip";
    public static final String COORDINATOR_TABLE = "employment_coordinator";
    public static final String VPN_COORDINATOR_TABLE = "vpn_coordinator";
    public static final String STATE_TABLE = "district";
    public static final String RO_TABLE = "nicemployee_reporting";
    public static final String OTP_TABLE = "otp_save";
    public static final String CA_TABLE = "comp_auth";
    public static final String DASHBOARD_TABLE = "dashboard_admin";
    public static final String RELAY_TABLE_NAME = "relay_registration";
    public static final String BULK_TABLE_NAME = "bulk_registration";
    public static final String DIST_TABLE_NAME = "distribution_registration";
    public static final String MOB_TABLE_NAME = "mobile_registration";
    public static final String NKN_TABLE_NAME = "nkn_registration";
    public static final String IP_TABLE_NAME = "ip_registration";
    public static final String SMS_TABLE_NAME = "sms_registration";
    public static final String SINGLE_TABLE_NAME = "single_registration";
    public static final String GEM_TABLE_NAME = "gem_registration";
    public static final String VPN_TABLE_NAME = "vpn_registration";
    public static final String DNS_TABLE_NAME = "dns_registration";
    public static final String WEBCAST_TABLE_NAME = "webcast_registration";
    public static final String FIREWALL_TABLE_NAME = "centralutm_registration";
    public static final String EMAILACTIVATE_TABLE_NAME = "email_act_registration";
    public static final String EMAILDEACTIVATE_TABLE_NAME = "email_deact_registration";
    public static final String LDAP_TABLE_NAME = "ldap_registration";
    public static final String IMAP_TABLE_NAME = "imappop_registration";
    public static final String WIFI_TABLE_NAME = "wifi_registration";
    
    public static final String DA_PENDING = "Pending with DA-Admin";
    public static final String MAIL_ADMIN_PENDING = "Pending with Admin";
    public static final String COORDINATOR_PENDING = "Pending with Coordinator";
    public static final String HOG_PENDING = "Pending with HOG";
    public static final String HOG_REJECTED = "Rejected by HOG";
    public static final String CA_PENDING = "Pending with RO/Nodal/FO";
    public static final String US_PENDING = "Pending with Under Secretary and above";
    public static final String SUPPORT_PENDING = "Pending with Support";
    public static final String manual_upload = "Pending with User";
    public static final String CANCEL = "Cancelled";
    public static final String US_REJECTED = "Rejected by Under Secretary and above";
    public static final String PENDING_API = "Pending with API";
    public static final String DA_REJECTED = "Rejected by DA-Admin";
    public static final String MAIL_ADMIN_REJECTED = "Rejected by Admin";
    public static final String CA_REJECTED = "Rejected by RO/Nodal/FO";
    public static final String COMPLETED = "Completed";
    public static final String SUPPORT_REJECTED = "Rejected by Support";
    public static final String COORDINATOR_REJECTED = "Rejected by Coordinator";
    public static final String US_EXPIRED = "Under Secretary and above Link Timeout";
    
    public static final String ROLE_USER = "user";
    public static final String ROLE_DA = "da";
    public static final String ROLE_CA = "ca";
    public static final String ROLE_CO = "co";
    public static final String ROLE_MAILADMIN = "admin";
    public static final String ROLE_SUP = "sup";
    
    public static final String STATUS_MAILADMIN_PENDING = "mail-admin_pending";
    public static final String STATUS_DA_PENDING = "da_pending";
    public static final String STATUS_SUPPORT_PENDING = "support_pending";
    public static final String STATUS_CA_PENDING = "ca_pending";
    public static final String STATUS_COORDINATOR_PENDING = "coordinator_pending";
    public static final String STATUS_MANUAL_UPLOAD = "manual_upload";
    public static final String STATUS_US_PENDING = "us_pending";
    
    public static final String STATUS_MAILADMIN_REJECTED = "mail-admin_rejected";
    public static final String STATUS_DA_REJECTED = "da_rejected";
    public static final String STATUS_SUPPORT_REJECTED = "support_rejected";
    public static final String STATUS_CA_REJECTED = "ca_rejected";
    public static final String STATUS_COORDINATOR_REJECTED = "coordinator_rejected";
    public static final String STATUS_USER_REJECTED = "cancel";
    public static final String STATUS_US_REJECTED = "us_rejected";
    
    public static final String STATUS_USER_TYPE = "a";
    public static final String STATUS_CA_TYPE = "ca";
    public static final String STATUS_COORDINATOR_TYPE = "c";
    public static final String STATUS_SUPPORT_TYPE = "s";
    public static final String STATUS_ADMIN_TYPE = "m";
    public static final String STATUS_US_TYPE = "us";
    public static final String STATUS_DA_TYPE = "d";
    
    public static final String RELAY_FORM_KEYWORD = "relay";
    public static final String SMS_FORM_KEYWORD = "sms";
    public static final String DNS_FORM_KEYWORD = "dns";
    public static final String MOB_FORM_KEYWORD = "mobile";
    public static final String NKN_SINGLE_FORM_KEYWORD = "nkn_single";
    public static final String IMAP_FORM_KEYWORD = "imappop";
    public static final String VPN_SURRENDER_FORM_KEYWORD = "vpn_surrender";
    public static final String VPN_DELETE_FORM_KEYWORD = "vpn_delete";
    public static final String DNS_SECOND_COORD = "preeti.nhq@nic.in,rahejapreeti@hotmail.com,rahejapreeti@gmail.com";
    public static final String VPN_SINGLE_FORM_KEYWORD = "vpn_single";
    public static final String CENTRAL_UTM_FORM_KEYWORD = "centralutm";
    public static final String BULK_FORM_KEYWORD = "bulk";
    public static final String LDAP_FORM_KEYWORD = "ldap";
    public static final String WIFI_FORM_KEYWORD = "wifi";
    public static final String VPN_ADD_FORM_KEYWORD = "change_add";
    public static final String VPN_RENEW_FORM_KEYWORD = "vpn_renew";
    public static final String WEBCAST_FORM_KEYWORD = "webcast";
    public static final String FIREWALL_FORM_KEYWORD = "centralutm";
    public static final String EMAILACTIVATE_FORM_KEYWORD = "email_act";
    public static final String EMAILDEACTIVATE_FORM_KEYWORD = "email_deact";
    public static final String VPN_BULK_FORM_KEYWORD = "vpn_bulk";
    public static final String DIST_FORM_KEYWORD = "dlist";
    public static final String NKN_BULK_FORM_KEYWORD = "nkn_bulk";
    public static final String SINGLE_FORM_KEYWORD = "single";
    public static final String IP_FORM_KEYWORD = "ip";
    public static final String GEM_FORM_KEYWORD = "gem";
    
    
    public static final String DNS_BULK_CNAME = "dns_bulk_cname";
    public static final String DNS_BULK_MX = "dns_bulk_mx";
    public static final String DNS_BULK_SPF = "dns_bulk_spf";
    public static final String DNS_BULK_SRV = "dns_bulk_srv";
    public static final String DNS_BULK_TXT = "dns_bulk_txt";
    public static final String DNS_BULK_PTR = "dns_bulk_ptr";
    public static final String DNS_BULK_UPLOAD = "dns_bulk_upload";
    public static final String DNS_BULK_DMARC = "dns_bulk_dmarc";
	
    public static final String EMAIL_COLUMN_FOR_SUPPORT_ADMIN = "user_name";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String COORDINATOR_EMAIL = "emp_coord_email";
    public static final String MANUAL = "Manual";
    public static final String STAT_REMARKS_TEXT = "AlphaNumeric Characters allowed with special characters like ( # , . - : / ' @ & \\ _ ?) with maximum length 500 characters ";
    
    public static final String DNS_MAILADMIN = "preeti.nhq@nic.in";
  //public static final String SECOND_COORD = "seema@gov.in,rajesh.singh@gov.in,rajp@nic.in";// to be uncommented in production
    public static final String VPN_MAILADMIN = "preeti.nhq@nic.in,rahejapreeti@hotmail.com,rahejapreeti@gmail.com"; // line added by pr on 29thdec17
  //public static final String GOV_FIRST_COORD = "amishra@nic.in";// to be uncommented in production
    public static final String DNS_GOV_FIRST_COORD = "preeti.nhq@nic.in,raheja_preeti@yahoo.co.in";
  //public static final String NIC_FIRST_COORD = "rsm@nic.in,prakash@nic.in,bkd@nic.in"; // to be uncommented in production
    public static final String DNS_NIC_FIRST_COORD = "preeti.nhq@nic.in,rahejapreeti@gmail.com";
    public static final String GEM_DA_ADMIN = "lily.prasad@gem.gov.in";
    public static final String HIMACHAL_DA_ADMIN = "kaushal.shailender@nic.in";
}