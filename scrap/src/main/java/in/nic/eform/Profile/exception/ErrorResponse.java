package in.nic.eform.Profile.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
	List<Error> erros = new ArrayList<>();
}
