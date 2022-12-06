package in.nic.ashwini.eForms.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "dns_registration_url")
@Access(value = AccessType.FIELD)
@Data
public class DnsRegistrationUrl {
	@Id
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "dns_url")
	private String dnsUrl;
	@Column(name = "dns_id")
	private long dnsId;
}
