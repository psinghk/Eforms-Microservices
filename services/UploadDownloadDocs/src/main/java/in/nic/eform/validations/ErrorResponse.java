package in.nic.eform.validations;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {

	private List<Error> errors = new ArrayList<>();



}
