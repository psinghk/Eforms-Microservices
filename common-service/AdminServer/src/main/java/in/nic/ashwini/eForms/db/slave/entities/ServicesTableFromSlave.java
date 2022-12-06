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
@Table(name = "service_tbl")
@Access(value = AccessType.FIELD)
@Data
public class ServicesTableFromSlave {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "service_name")
	private String serviceName;
	
	private String 	keyword	;
	
	@Column(name = "service_type")
	private String 	serviceType;
	
	private Integer status;
	
	@Column(name="service_order")
	private Integer serviceOrder;
	
	private String url;
	
	@Column(name = "logo_class")
	private String logo;
	
	@Column(name = "newly_added")
	private String newlyAdded;
}
