package in.nic.ashwini.eForms.db.slave.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "oauth")
@Data
public class OauthFromSlave {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;
	
	@Column(name = "client_id")
	public String clientId;
	
	@Column(name = "client_secret")
	public String clientSecret;
	
	@Column(name = "grant_type")
	public String grantType;
	
	@Column(name = "scope")
	public String scope;
	
	@Column(name = "access_token_exp_time")
	public Integer accessTokenExpiryTimeInSeconds;
	
	@Column(name = "refresh_token_exp_time")
	public Integer refreshTokenExpiryTimeInSeconds;

}
