package in.nic.ashwini.eForms.entities;

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
@Table(name = "dnsreportdata")
@Access(value = AccessType.FIELD)
@Data
public class DnsReportData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "auth_off_name")
	private String authOffName;
	@Column(name = "auth_email")
	private String authEmail;
	@Column(name = "datetime")
	private String datetime;
	@Column(name = "record_mx")
	private String recordMx;
	@Column(name = "record_ptr")
	private String recordPtr;
	@Column(name = "record_srv")
	private String recordSrv;
	@Column(name = "record_spf")
	private String recordSpf;
	@Column(name = "record_txt")
	private String recordTxt;
	@Column(name = "record_dmarc")
	private String recordDmarc;
	@Column(name = "record_mx1")
	private String recordMx1;
	@Column(name = "record_ptr1")
	private String recordPtr1;
	@Column(name = "commaSeperatedURLvalues")
	private String commaSeperatedURLvalues;
	@Column(name = "commaSeperatedOldIPvalues")
	private String commaSeperatedOldIPvalues;
	@Column(name = "commaSeperatedNewIPvalues")
	private String commaSeperatedNewIPvalues;
	@Column(name = "commaSeperatedcnamevalues")
	private String commaSeperatedcnamevalues;
	@Column(name = "completed_by_user")
	private String completedByUser;
}
