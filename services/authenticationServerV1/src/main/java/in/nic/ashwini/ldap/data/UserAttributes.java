package in.nic.ashwini.ldap.data;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class UserAttributes {
	private String username;
	private String email;
	@Size(min = 1)
	private List<String> attributes;
	@NotNull
	private User user;
	private String remarks;
}
