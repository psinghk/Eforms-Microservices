package in.nic.ashwini.ldap.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {
	private String field;
	private String message;
}
