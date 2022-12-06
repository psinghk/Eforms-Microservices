package in.nic.ashwini.eForms.models;

import in.nic.ashwini.eForms.custumvalidation.NameValid;
import in.nic.ashwini.eForms.custumvalidation.AppUrlValid;

import java.util.List;

import in.nic.ashwini.eForms.custumvalidation.AddressValid;
import in.nic.ashwini.eForms.custumvalidation.CityValid;
import in.nic.ashwini.eForms.custumvalidation.DesignationValid;
import in.nic.ashwini.eForms.custumvalidation.PinValid;
import in.nic.ashwini.eForms.custumvalidation.ServerLocValid;
import in.nic.ashwini.eForms.custumvalidation.StateValid;
import in.nic.ashwini.eForms.custumvalidation.EmailValid;
import in.nic.ashwini.eForms.custumvalidation.Ip;
import in.nic.ashwini.eForms.custumvalidation.MobileValid;
import lombok.Data;

@Data
public class PreviewFormBean 
{

	private List<String> sms_service;
	
	private String pull_url;
	private String pull_keyword;
	private String s_code;
	private String short_code;
	
	@NameValid
	private String app_name;
	
	@AppUrlValid
	private String app_url;
	
	private String sms_usage;
	@ServerLocValid
	private String server_loc;
	private String server_loc_txt;
	@Ip
	private String base_ip;
	private String service_ip;
	
	@NameValid
	private String t_off_name;
	private String tdesignation;
	private String temp_code;
	
	@AddressValid
	private String taddrs;
	
	@CityValid
	private String tcity;
	
	@StateValid
	private String tstate;
	
	@PinValid
	private String tpin;
	private String ttel_ofc;	
	private String ttel_res;
	
	@MobileValid
	private String tmobile;
	
	@EmailValid
	private String tauth_email;
    
	@NameValid
	private String bauth_off_name;
	
	@DesignationValid
	private String bdesignation;	
	private String bemp_code;
	
	@AddressValid
	private String baddrs;
	
	@CityValid
	private String bcity;
	
	@StateValid
	private String bstate;
	
	@PinValid
	private String bpin;
	private String btel_ofc;	
	private String btel_res;
	
	@MobileValid
	private String bmobile;
	
	@EmailValid
	private String bauth_email;

	private String audit;
	private String datepicker1;
	private String staging_ip;
	private String sender;
	private String sender_id;
	private String domestic_traf;
	private String inter_traf;
	
	private String organization;
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	private Boolean tnc;

}
