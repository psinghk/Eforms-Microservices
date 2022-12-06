package in.nic.eform.ldap.bean;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
@Data
@JsonInclude(Include.NON_EMPTY)
public class LdapFormBean {

	@NotNull
	@NotEmpty
	private String formtype;
	private String consent;
	private String check;
	//@NotEmpty
	//@Pattern(regexp = "[A-Z]+-[A-Z]+[0-9]+" ,message ="Invalid Format of Registration No")
	private String registrationno;
	@NotEmpty
	private String app_name;
	
	@NotEmpty
	@Pattern(regexp = "^(?:(?:(?:https?|ftp):)?\\/\\/)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})).?)(?::\\d{2,5})?(?:[/?#]\\S*)?$" ,message ="Url is not valid!")
	private String app_url;
	
	@NotEmpty
	@Pattern(regexp = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$" ,message ="Base ip is not valid!")
	private String base_ip;
	
	@Pattern(regexp = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$" ,message ="Service ip not valid!")
	private String service_ip;
	
	@NotEmpty
	@Pattern(regexp = "^[a-zA-Z#0-9\\s,.\\-\\/\\(\\)]{2,100}$" ,message ="Domain not valid!")
	private String domain;
	
	@NotEmpty
	private String server_loc;
	@NotEmpty
	private String https;
	@NotEmpty
	private String audit;
	
	private String server_loc_txt;
	
	private String ldap_id1;
	
	private String ldap_id2;
	@NotEmpty
	private String uploaded_filename;
	@NotEmpty
	private String renamed_filepath;
	
	private String imgtxt;
	@NotEmpty
	private String cert;
	
	private String tnc;
	
	private String applicant_name;
	private String applicant_email;
	private String applicant_mobile;
	private String applicant_ophone;
	private String applicant_rphone;
	private String applicant_designation;
	private String applicant_ministry;
	private String applicant_employment;
	private String applicant_department;
	private String applicant_stateCode;
	private String applicant_state_dept;
	private String applicant_other_department;
	private String applicant_organization;
	private String applicant_code;
	private String applicant_ofcAddess;
	private String applicant_posting_city;
	private String applicant_posting_state;
	private String applicant_pincode;
	
	private String hod_email;
	private String hod_name;
	private String hod_mobile;
	private String hod_telephone;
	private String hod_designation;
	
	private String under_sec_name;
	private String under_sec_email;
	private String under_sec_mobile;
	private String under_sec_tel;
	private String under_sec_designation;
//	
	
	
}
