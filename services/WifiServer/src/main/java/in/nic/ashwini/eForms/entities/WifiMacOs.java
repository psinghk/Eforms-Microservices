package in.nic.ashwini.eForms.entities;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "wifi_mac_os")
@Access(value = AccessType.FIELD)
@Data
public class WifiMacOs {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@JsonIgnore
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "operating_system")
	private String operatingSystem;
	@Column(name = "machine_address")
	private String machineAddress;
	@Column(name = "device_type")
	private String deviceType;
	@Column(name = "status")
	private String status;
	

}
