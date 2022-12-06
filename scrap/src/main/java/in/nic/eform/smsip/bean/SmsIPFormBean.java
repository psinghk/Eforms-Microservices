package in.nic.eform.smsip.bean;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
@Data
@JsonInclude(Include.NON_EMPTY)
public class SmsIPFormBean {
	private String registrationno;
	private String formtype;
	private String req_for;
	@NotEmpty
	private String account_name;
	private String old_ip1;
	private String new_ip1;
	private String server_loc;
	@NotEmpty
	private String ip1;
	private String ip2;
	private String ip3;
	private String ip4;
	
	private String ip_change_request;
	private String ip_action_request;
	private String support_action_taken;
	
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
	
	
}
