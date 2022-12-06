package in.nic.eform.Profile.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "district")
public class State {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long statecode;
	private String stateName;
	private String districtName;

}
