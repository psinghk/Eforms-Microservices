package in.nic.eform.updatemobile.dto;

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
public class MobileMailadminFormsDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String m_id;
	
	@Column(name = "m_sup_id")
	private String m_sup_id;
	
	@Column(name = "m_email	")
	private String 	memail	;
	
	@Column(name = "m_single")
	private String 	m_single;
	
	@Column(name = "m_bulk")
	private String 	m_bulk;
	
	@Column(name = "m_nkn")
	private String 	m_nkn;
	
	@Column(name = "m_relay")
	private String  m_relay;
	
	@Column(name = "m_ldap")
	private String 	mldap;
	
	@Column(name = "m_dlist")
	private String 	m_dlist;
	
	@Column(name = "m_sms")
	private String 	m_sms;
	
	@Column(name = "m_ip")
	private String  m_ip;
	
	@Column(name = "m_imappop")
	private String  mimappop;
	
	@Column(name = "m_gem")
	private String  m_gem;
	
	@Column(name = "m_mobile")
	private String  mmobile;
	
	@Column(name = "m_dns")
	private String  m_dns;
	
	@Column(name = "m_wifi")
	private String 	m_wifi;
	
	@Column(name = "m_vpn")
	private String 	m_vpn;
	
	@Column(name = "m_cloud")
	private String  m_cloud;
	
	@Column(name = "m_centralutm")
	private String  m_centralutm;
	
	@Column(name = "m_webcast")
	private String  m_webcast;
}
