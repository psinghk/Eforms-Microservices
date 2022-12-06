package in.nic.eForms.models;

import java.util.List;

import javax.naming.Name;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class Quota {
	
	private String  dn;
	
	
	private String sunAvailableServices;
	private String preferredMailHost;
	private String preferredMailMessageStore;
	
	private List<String> allowedDomains;
	
	
}
