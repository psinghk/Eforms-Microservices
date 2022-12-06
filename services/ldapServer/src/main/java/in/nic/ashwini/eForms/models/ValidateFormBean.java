package in.nic.ashwini.eForms.models;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class ValidateFormBean {
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
	
	private String renamedFilepath;
	
	private String https;
	
	private String audit;
}
