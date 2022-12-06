package in.nic.ashwini.eForms.db.slave.entities;

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
@Table(name = "service_admin")
@Access(value = AccessType.FIELD)
@Data
public class ServiceAdminFromSlave {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "service_id")
	private Integer serviceId;

	@Column(name = "admin_email	")
	private String adminEmail;

	private String status;

}
