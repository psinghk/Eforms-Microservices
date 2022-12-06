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
@Table(name = "dns_bulk_campaigns")
@Access(value = AccessType.FIELD)
@Data
public class DnsBulkCampaigns {
	@Id
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "owner_name")
	private String ownerName;
	@Column(name = "owner_email")
	private String ownerEmail;
	@Column(name = "file_path")
	private String filePath;
	@Column(name = "uploaded_file")
	private String uploadedFile;
	@Column(name = "status")
	private String status;
	@Column(name = "discard_status")
	private String discardStatus;
	@Column(name = "request_type")
	private String requestType;
	@Column(name = "req_other_add")
	private String reqOtherAdd;
	@CreationTimestamp
	@Column(name = "submitted_at")
	private LocalDateTime submissionTimeStamp;
}
