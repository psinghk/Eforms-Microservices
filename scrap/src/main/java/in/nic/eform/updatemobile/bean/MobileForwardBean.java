package in.nic.eform.updatemobile.bean;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class MobileForwardBean {
	private String check;
	@Pattern(regexp = "[A-Z]+-[A-Z]+[0-9]+" ,message ="Invalid Format of Registration No")
	private String ref_num;
	@NotEmpty
	private String formtype;
	@NotEmpty
	private String role;
	@NotEmpty
	private String uemail;
	private String uname;
	private String umobile;
	private String hodEmails;
	private String hodMobile;
	private String hodName;
	private String file;
	private String rename_file;
	private String statRemarks;
	private String app_ca_type;
	private String app_ca_path;
	private String choose_da_type;
	private String toEmailFromSuppportConsole;
	private List<String> aliases;
	private String country_code;
	private String new_mobile;
	
    
	
	
}
