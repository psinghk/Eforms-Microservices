package in.nic.ashwini.ldap.validation;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
	List<Error> erros = new ArrayList<>();
}
