package in.nic.ashwini.eForms.models;

import lombok.Data;

@Data
public class DnsBean {
	private int id = -1;
    private String domain = "";
    private String cname = "";
    private String newIp = "";
    private String oldIp = "";
    private String errorMessage = "";
    private String req = "";
    private String reqOtherAdd = "";
    private String cnameTxt = "";
    private String mxTxt = "";
    private String ptrTxt = "";
    private String txtTxt = "";
    private String srvTxt = "";
    private String spfTxt = "";
    private String dmarcTxt = "";
    private String oldCnameTxt = "";
    private String oldMxTxt = "";
    private String oldPtrTxt = "";
    private String oldTxtTxt = "";
    private String oldSrvTxt = "";
    private String oldSpfTxt = "";
    private String oldDmarcTxt = "";
    private String serverLocation = "";
    private String migrationDate = "";
    private int campaignId = -1;
    private String registrationNumber = "";
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        DnsBean dnsData = (DnsBean) obj;
        if (dnsData.id == this.id) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.id;
    }

}
