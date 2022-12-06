package in.nic.ashwini.eForms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmtpIpDetails {
	
	public String[] appIp;
	
	public String[] oldAppIp;

}
