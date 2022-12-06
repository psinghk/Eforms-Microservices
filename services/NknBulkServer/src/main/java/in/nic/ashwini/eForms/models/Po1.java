package in.nic.ashwini.eForms.models;

import java.util.List;



import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public final class Po1  {
	
    private String dn;
	
	public String getDn() {
		return String.valueOf(this.dn);
	}
    
	private List<String> po;
}