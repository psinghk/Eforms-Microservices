package in.nic.eform.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = ("doc_upload"))
@Data
public class DocUpload {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(name = "registration_no")
	private String regno;
	@Column(name = "role")
	private String role;
	@Column(name = "doc")
	private String doc;
	@Column(name = "doc_path")
	private String docpath;
	@Column(name = "extension")
	private String extension;
	@Column(name = "original_filename")
	private String filename;
}
