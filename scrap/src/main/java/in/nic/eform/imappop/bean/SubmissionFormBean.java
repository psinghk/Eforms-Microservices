package in.nic.eform.imappop.bean;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
@Data
@JsonInclude(Include.NON_EMPTY)
public class SubmissionFormBean {

	 @NotEmpty
	    private String protocol;
	  //  @NotEmpty
	    private String imgtxt;
	    private String captcha;
	    private String CSRFRandom;
	    private String action_type;
	    private String form_type;
	    private String consent;
	    private String userip;
	    
	    private String under_sec_email;
	    private String under_sec_name;
	    private String under_sec_mobile;
	    private String under_sec_tel;
	    private String under_sec_desig;
	    //@NotEmpty
	    private String applicant_email;
	   // @NotEmpty
	    private String applicant_name;
	    private String applicant_mobile;
	    private String applicant_ophone;
		private String applicant_rphone;
		private String applicant_designation;
	    private String min;
	    private String user_employment;
	    private String dept;
	    private String stateCode;
	    private String state_dept;
	    private String other_dept;
	    private String org;
	    private String state;
	    private String applicant_code;
		private String applicant_ofcAddess;
		private String applicant_posting_city;
		private String applicant_posting_state;
		private String applicant_pincode;
		private String add_state;
	    
	    
		// @NotEmpty
	    private String hod_email;
	    private String hod_name;
	    private String hod_mobile;
	    private String hod_tel;
		private String hod_designation;
		private String ca_name;
		private String ca_email;
		private String ca_mobile;
		private String ca_telephone;
	    private String ca_design;
	    private String tnc;
	    
//	
	
	
}
