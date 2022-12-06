package in.nic.ashwini.eForms.db.master.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Oauth {
	
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
