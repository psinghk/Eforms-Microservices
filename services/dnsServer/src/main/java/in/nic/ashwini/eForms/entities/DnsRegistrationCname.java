package in.nic.ashwini.eForms.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "dns_registration_cname")
@Access(value = AccessType.FIELD)
@Data
public class DnsRegistrationCname {
	@Id
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "cname")
	private String cname;
	@Column(name = "dns_id")
	private long dnsId;
}
