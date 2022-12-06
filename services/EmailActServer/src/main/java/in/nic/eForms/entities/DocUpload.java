package in.nic.eForms.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = ("doc_upload"))
@Data
public class DocUpload {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "role")
	private String role;
	@Column(name = "doc")
	private String doc;
	@Column(name = "doc_path")
	private String docPath;
	private String status;
	@Column(name = "original_filename")
	private String originalFilename;
	@Column(name = "extension")
	private String extension;
	@CreationTimestamp
	@Column(name = "upload_time")
	private LocalDateTime uploadedOn;
	

}
