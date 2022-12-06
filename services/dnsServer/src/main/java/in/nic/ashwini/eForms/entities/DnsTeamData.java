package in.nic.ashwini.eForms.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "dns_team_data")
@AllArgsConstructor
@NoArgsConstructor
public class DnsTeamData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String domain;
	private String ip;
	@Column(name = "applicant_name")
	private String applicantName;
	@Column(name = "applicant_email")
	private String applicantEmail;
	@Column(name = "applicant_mobile")
	private String applicantMobile;
	@Column(name = "delete_status")
	private int deleteStatus;
}
