package in.nic.ashwini.eForms.models;

import lombok.Data;

@Data
public class SsoResponseBean {
	private String localTokenId;
    private String email;
    private String browserId;
    private String userName;
    private String sessionId; 
    private String status;
    private String tokenValid;
    private String url;
    private String reason;
    private String mobileNo;
    private String ip;
    private String ua;
}
