package in.nic.ashwini.eForms.dto;

import java.util.List;

import lombok.Data;

@Data
public class AvalableSpace {

	private String dn;
	private String sunAvailableServices;
	private String preferredMailHost;
	private String preferredMailMessageStore;
	private List<String> allowedDomains;
	
	
}
