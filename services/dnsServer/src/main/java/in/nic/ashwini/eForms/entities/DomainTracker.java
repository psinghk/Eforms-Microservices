package in.nic.ashwini.eForms.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "dnsreportdata")
@Access(value = AccessType.FIELD)
@Data
public class DomainTracker {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "authorize_email")
	private String authorizeEmail;
	@Column(name = "allow_domain_tracker")
	private String allowDomainTracker;
}
