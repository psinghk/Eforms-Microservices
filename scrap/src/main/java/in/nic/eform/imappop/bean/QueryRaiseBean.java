package in.nic.eform.imappop.bean;

import lombok.Data;

@Data
public class QueryRaiseBean {
	
	private String qr_form_type;
	private String qr_reg_no;
	private String qr_forwarded_by;
	private String qr_forwarded_by_user;
	private String qr_forwarded_to;
	private String qr_forwarded_to_user;
	private String 	qr_message;
	private String 	qr_createdon;

}
