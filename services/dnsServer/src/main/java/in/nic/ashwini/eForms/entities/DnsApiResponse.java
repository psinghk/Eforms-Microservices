package in.nic.ashwini.eForms.entities;

import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = "dns_api_response")
@Access(value = AccessType.FIELD)
@Data
public class DnsApiResponse {
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "api_reg_no")
	private String registrationNo;
	@Column(name = "api_response")
	private String apiResponse;
	@Column(name = "api_domain_response")
	private String apiDomainResponse;
	@CreationTimestamp
	@Column(name = "api_createdon")
	private LocalDateTime apiCreatedOn;
}
