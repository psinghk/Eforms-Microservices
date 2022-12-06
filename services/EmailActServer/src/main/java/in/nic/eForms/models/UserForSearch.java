package in.nic.eForms.models;

import java.util.List;

import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@Data
public final class UserForSearch  {
	
	
	private String mobile = "";
    private String email;
    private List<String> aliases;
	private List<String> mailalternateaddress;
	private String userInetStatus;
	private String userMailStatus;
}
