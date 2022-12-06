package in.nic.eform.imappop.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Table(name = ("doc_upload"))
@Data
public class ImappopDocUploadDTO {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(name = "registration_no")
	private String registrationno;
	@Column(name = "role")
	private String role;
	@Column(name = "doc")
	private String doc;
	@Column(name = "doc_path")
	private String docpath;
//	@Column(name = "extension")
//	private String extension;
	@Transient
	private String status;
	@Column(name = "original_filename")
	private String original_filename;
	@Column(name = "extension")
	private String extension;

}
