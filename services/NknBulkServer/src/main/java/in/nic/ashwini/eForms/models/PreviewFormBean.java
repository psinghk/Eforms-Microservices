package in.nic.ashwini.eForms.models;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PreviewFormBean {
	
	
	//@NotEmpty
	private String instName;//com

	//@NotEmpty
	private String instId;//com
	//@NotEmpty
	private String nknProject; //com
	private List<MultipartFile> infile;   //csv file
	
	
	public List<MultipartFile> getInfile() {
		return infile;
	}
	
	
	

	
	private String organization;
//	@NotEmpty
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	@NotNull
	private Boolean tnc;

	public Object getInfile;
}
