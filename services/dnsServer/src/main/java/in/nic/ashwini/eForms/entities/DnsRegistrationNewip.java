package in.nic.ashwini.eForms.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "dns_registration_newip")
@Access(value = AccessType.FIELD)
@Data
public class DnsRegistrationNewip {
	@Id
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "newip")
	private String newIp;
	@Column(name = "dns_id")
	private long dnsId;
}
