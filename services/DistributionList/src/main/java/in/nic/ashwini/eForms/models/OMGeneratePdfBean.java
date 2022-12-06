package in.nic.ashwini.eForms.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Data
public class OMGeneratePdfBean
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String registrationNo;
	private String omName;
	private String omMobile;
	private String omEmail;
	private String formType;
}
