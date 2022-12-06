package in.nic.ashwini.ldap.entities;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Scope("singleton")
@Data
public class AccessTokenEntity{
	private String accessToken;
}
