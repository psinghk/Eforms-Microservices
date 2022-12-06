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

import lombok.Data;


@Entity
@Table(name = "vpn_entries")
@Access(value = AccessType.FIELD)
@Data
public class VpnEntryBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="registration_no")
	private String registrationNo;
	
	@Column(name="ip_type")
	private String iptype;
	//private String ipType;
	
	@Column(name="ip1")
	private String serverIp;
	//private String ip1;
	
	@Column(name="ip2")
	private String ipRangeFrom;
	
	@Column(name="server_location")
	private String serverLocation;
	
	@Column(name="server_loc_other")
	private String serverLocOther;
	
	@Column(name="app_url")
	private String applicationUrl;
	//private String appUrl;
	
	@Column(name="dest_port")
	private String destinationPort;
	//private String destPort;
	
	@Column(name="deleted_flag")
	private String deletedFlag;
	
	@Column(name="deleted_by")
	private String deletedBy;
	
	@Column(name="datetime")
	private LocalDateTime datetime;
	
	@Column(name="action_type")
	private String actionType;


}
