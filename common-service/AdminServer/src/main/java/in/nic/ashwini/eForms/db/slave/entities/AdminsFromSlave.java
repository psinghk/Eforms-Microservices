package in.nic.ashwini.eForms.db.slave.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "support_individual_login")
public class AdminsFromSlave {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;
	
	@Column(name = "email_address")
	public String email;
	
	public String name;
	public String designation;
	public String mobile;
	
	@Column(name = "individual_ip")
	public String vpnIp;
	
	@Column(name = "ip")
	public String desktopIp;
}
