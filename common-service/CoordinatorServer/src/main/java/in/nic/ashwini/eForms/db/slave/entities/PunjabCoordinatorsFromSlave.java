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
@Table(name = "punjab_district_nodal_officers")
public class PunjabCoordinatorsFromSlave {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;
	
	public String district;
	public String name;
	public String mobile;
	
	@Column(name = "desig")
	public String designation;
	
	public String email;
}
