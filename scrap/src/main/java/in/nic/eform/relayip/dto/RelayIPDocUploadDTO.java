package in.nic.eform.relayip.dto;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Entity
@Table(name = ("doc_upload"))
@Access(value = AccessType.FIELD)
@Data
public class RelayIPDocUploadDTO {
	
	@Transient
	private List<MultipartFile> infile;
	@Id
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
	@Column(name = "extension")
	private String extension;
	@Column(name = "original_filename")
	private String original_filename;
	@Transient
	private String status;
	
}
