package in.nic.ashwini.eForms.db.slave.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "mailadmin_forms")
@Access(value = AccessType.FIELD)
@Data
public class MailAdminFormsFromSlave {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "m_id")
	private int mId;
	@Column(name = "m_sup_id")
	private int mSupId; 
	
	@Column(name = "m_email")
	private String mEmail;
	
	@Column(name = "m_single")
	private String mSingle;
	
	@Column(name = "m_bulk")
	private String mBulk;
	@Column(name = "m_nkn")
	private String mNkn;
	@Column(name = "m_relay")
	private String mRelay;
	@Column(name = "m_ldap")
	private String mLdap;
	@Column(name = "m_dlist")
	private String mDlist;
	@Column(name = "m_sms")
	private String mSms;
	@Column(name = "m_ip")
	private String mIp;
	
	@Column(name = "m_imappop")
	private String mImappop;
	@Column(name = "m_gem")
	private String mGem;
	@Column(name = "m_mobile")
	private String mMobile;
	@Column(name = "m_dns")
	private String mDns;
	@Column(name = "m_wifi")
	private String mWifi;
	@Column(name = "m_vpn")
	private String mVpn;
	
	
	@Column(name = "m_centralutm")
	private String mCentralutm;
	
	@Column(name = "m_webcast")
	private String mWebcast;
	
	@Column(name = "m_email_act")
	private String mEmailAct;
	
	@Column(name = "m_email_deact")
	private String mEmaildeact;
	
	@Column(name = "m_profile")
	private String mProfile;
	
}
