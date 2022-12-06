package in.nic.eform.imappop.bean;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class SubmissionBean {
	private String check;
	@Pattern(regexp = "[A-Z]+-[A-Z]+[0-9]+" ,message ="Invalid Format of Registration No")
	private String ref_num;
	@NotEmpty
	private String formtype;
	@NotEmpty
	private String role;
	@NotEmpty
	private String uemail;
	//@NotEmpty
	private String uname;
	//@NotEmpty
	private String umobile;
	//@NotEmpty
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

}
