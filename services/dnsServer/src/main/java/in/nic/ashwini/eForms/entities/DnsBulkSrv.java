package in.nic.ashwini.eForms.entities;

import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = "dns_bulk_srv")
@Access(value = AccessType.FIELD)
@Data
public class DnsBulkSrv {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "campaign_id")
	private Long campaignId;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "domain")
	private String domain;
	@Column(name = "srv")
	private String srv;
	@Column(name = "old_srv")
	private String oldSrv;
	@Column(name = "location")
	private String location;
	@Column(name = "migration_date")
	private String migrationDate;
	@Column(name = "dns_error")
	private String dnsError;
	@Column(name = "error_status")
	private String errorStatus;
	@Column(name = "delete_status")
	private String deleteStatus;
	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
