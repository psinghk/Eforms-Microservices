package in.nic.ashwini.eForms.models;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class PreviewFormBean {
	private String appName;
	
	private String appUrl;
	
	private String domain;
	
	private String baseIp;
	
	private String serviceIp;
	
	private String serverLoc;
	
	private String server_location_txt;
	
	private String ldap_id1;
	
	private String ldap_id2;
	
	private MultipartFile uploadedFilename;
//	private String renamedFilepath;
	private String organization;
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	private Boolean tnc;
	private String https;
	private String audit;
}
