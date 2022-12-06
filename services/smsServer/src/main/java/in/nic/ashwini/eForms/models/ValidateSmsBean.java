package in.nic.ashwini.eForms.models;
import in.nic.ashwini.eForms.custumvalidation.NameValid;
import in.nic.ashwini.eForms.custumvalidation.AppUrlValid;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import in.nic.ashwini.eForms.custumvalidation.AddressValid;
import in.nic.ashwini.eForms.custumvalidation.CityValid;
import in.nic.ashwini.eForms.custumvalidation.DesignationValid;
import in.nic.ashwini.eForms.custumvalidation.DomesticTraficValid;
import in.nic.ashwini.eForms.custumvalidation.PinValid;
import in.nic.ashwini.eForms.custumvalidation.ServerLocValid;
import in.nic.ashwini.eForms.custumvalidation.StateValid;
import in.nic.ashwini.eForms.custumvalidation.EmailValid;
import in.nic.ashwini.eForms.custumvalidation.Ip;
import in.nic.ashwini.eForms.custumvalidation.MobileValid;
import lombok.Data;

@Data
public class ValidateSmsBean 
{
	@NameValid
	private String app_name;
	
	@AppUrlValid
	private String app_url;

	@ServerLocValid
	private String server_loc;
	
	@Ip
	private String base_ip;
	
	@NameValid
	private String t_off_name;
	
	@DesignationValid
	private String tdesignation;
	
	@AddressValid
	private String taddrs;
	
	@StateValid
	private String tstate;
	
	@CityValid
	private String tcity;
	
	@PinValid
	private String tpin;
	
	@MobileValid
	private String tmobile;
	
	@EmailValid
	private String tauth_email;
    
	@NameValid
	private String bauth_off_name;
	
	@DesignationValid
	private String bdesignation;	
	
	@AddressValid
	private String baddrs;
	
	@CityValid
	private String bcity;
	
	@StateValid
	private String bsatate;
	
	@PinValid
	private String bpin;
	
	@MobileValid
	private String bmobile;
	
	@EmailValid
	private String bauth_email;

	@Pattern(regexp = "^(?:Yes|No|yes|no)$",message = "Please enter list Moderated")
	@NotNull(message = "security audit should not be empty")
	private String audit;
	
//	@NotNull
//	private String sender_id;
	
	@NotNull(message = "domestic trafic should not be empty")
	private String domestic_traf;

}
