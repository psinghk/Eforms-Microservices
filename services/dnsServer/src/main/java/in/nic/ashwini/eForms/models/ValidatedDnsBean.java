package in.nic.ashwini.eForms.models;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import in.nic.ashwini.eForms.exceptions.Ip;
import in.nic.ashwini.eForms.exceptions.IsWithinAMonth;
import in.nic.ashwini.eForms.utils.LocalDateDeserializer;
import in.nic.ashwini.eForms.utils.LocalDateSerializer;
import lombok.Data;

@Data
public class ValidatedDnsBean {
	private long id=-1;
	@NotBlank(message = "Domain can not be empty")
	@Size(min=2,max=64, message="Length of the Domain must be between 2 and 64")
	@Pattern(regexp = "^(?!:\\/\\/)([a-zA-Z0-9-\\_]+\\.){0,5}[a-zA-Z0-9-\\_][a-zA-Z0-9-\\_]+\\.[a-zA-Z]{2,64}?$", message = "Enter valid DNS URL [e.g.: demo.nic.in or demo.gov.in]")
    private String domain = "";
	@Size(min=2,max=64, message="Length of the cname must be between 2 and 64")
	@Pattern(regexp = "^(?!:\\/\\/)([a-zA-Z0-9-\\_]+\\.){0,5}[a-zA-Z0-9-\\_][a-zA-Z0-9-\\_]+\\.[a-zA-Z]{2,64}?$", message = "Enter valid cname [e.g.: demo.nic.in or demo.gov.in]")
    private String cname;
	@Ip
    private String newIp;
	@Ip
    private String oldIp;
	@NotBlank(message = "Request type must be either new, modify or delete")
    private String req;
    @NotNull(message="Please select either default(aaaa) record or choose any")
    private String reqOtherAdd;
    @Size(min=2,max=64, message="Length of the cname must be between 2 and 64")
	@Pattern(regexp = "^(?!:\\/\\/)([a-zA-Z0-9-\\_]+\\.){0,5}[a-zA-Z0-9-\\_][a-zA-Z0-9-\\_]+\\.[a-zA-Z]{2,64}?$", message = "Enter valid cname [e.g.: demo.nic.in or demo.gov.in]")
    private String cnameTxt;
    @Size(min=2,max=100, message="Length of the MX record must be between 2 and 100")
	@Pattern(regexp = "^[a-zA-Z#0-9\\s,.\\_\\-\\/\\(\\)]{2,100}$", message = "Enter MX value, [Alphanumeric,dot(.),comma(,),hyphen(-),underscore(_),slash(/) and whitespaces] allowed.")
    private String mxTxt;
    @Ip
    private String ptrTxt;
    @Size(min=2,max=300, message="Length of the TXT record must be between 2 and 300")
	@Pattern(regexp = "^[^<>&%]{2,300}+$", message = "Enter valid TXT value")
    private String txtTxt;
    @Size(min=2,max=300, message="Length of the SRV record must be between 2 and 300")
	@Pattern(regexp = "^[^<>&%]{2,300}+$", message = "Enter valid SRV value")
    private String srvTxt;
    @Size(min=2,max=300, message="Length of the SPF record must be between 2 and 300")
	@Pattern(regexp = "^[^<>&%]{2,300}+$", message = "Enter valid SPF value")
    private String spfTxt;
    @Size(min=2,max=300, message="Length of the DMARC record must be between 2 and 300")
	@Pattern(regexp = "^[^<>&%]{2,300}+$", message = "Enter valid DMARC value")
    private String dmarcTxt;
    @Size(min=2,max=64, message="Length of the cname must be between 2 and 64")
	@Pattern(regexp = "^(?!:\\/\\/)([a-zA-Z0-9-\\_]+\\.){0,5}[a-zA-Z0-9-\\_][a-zA-Z0-9-\\_]+\\.[a-zA-Z]{2,64}?$", message = "Enter valid cname [e.g.: demo.nic.in or demo.gov.in]")
    private String oldCnameTxt;
    @Size(min=2,max=100, message="Length of the MX record must be between 2 and 100")
	@Pattern(regexp = "^[a-zA-Z#0-9\\s,.\\_\\-\\/\\(\\)]{2,100}$", message = "Enter MX value, [Alphanumeric,dot(.),comma(,),hyphen(-),underscore(_),slash(/) and whitespaces] allowed.")
    private String oldMxTxt;
    @Ip
    private String oldPtrTxt;
    @Size(min=2,max=300, message="Length of the TXT record must be between 2 and 300")
	@Pattern(regexp = "^[^<>&%]{2,300}+$", message = "Enter valid TXT value")
    private String oldTxtTxt;
    @Size(min=2,max=300, message="Length of the SRV record must be between 2 and 300")
	@Pattern(regexp = "^[^<>&%]{2,300}+$", message = "Enter valid SRV value")
    private String oldSrvTxt;
    @Size(min=2,max=300, message="Length of the SPF record must be between 2 and 300")
	@Pattern(regexp = "^[^<>&%]{2,300}+$", message = "Enter valid SPF value")
    private String oldSpfTxt;
    @Size(min=2,max=300, message="Length of the DMARC record must be between 2 and 300")
	@Pattern(regexp = "^[^<>&%]{2,300}+$", message = "Enter valid DMARC value")
    private String oldDmarcTxt;
    @NotBlank(message = "Server location can not be empty")
    @Size(min=2,max=100, message="Length of the location string must be between 2 and 100")
	@Pattern(regexp = "^[a-zA-Z0-9 .,-_&]{2,100}$", message = "Please enter server location in correct format , Alphanumeric(.,-_&) allowed [limit 2-100]")
    private String serverLocation = "";
    @JsonDeserialize(using=LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @FutureOrPresent(message="Migration date can not be a past date")
    @DateTimeFormat( pattern="dd/MM/yyyy")
    @IsWithinAMonth
    private LocalDate migrationDate;
    private long campaignId=-1;
    private String registrationNumber;
    private String error;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        ValidatedDnsBean dnsData = (ValidatedDnsBean) obj;
        if (dnsData.id == this.id) {
            return true;
        }

        //return (dnsData.id == this.id);
        boolean result = false;
        switch (reqOtherAdd) {
            case "":
                result = dnsData.domain.equalsIgnoreCase(this.domain) && dnsData.newIp.equalsIgnoreCase(this.newIp);
                break;
            case "cname":
                result = dnsData.domain.equalsIgnoreCase(this.domain) && dnsData.cnameTxt.equalsIgnoreCase(this.cnameTxt);
                break;
            case "mx":
                result = dnsData.domain.equalsIgnoreCase(this.domain) && dnsData.mxTxt.equalsIgnoreCase(this.mxTxt);
                break;
            case "spf":
                result = dnsData.domain.equalsIgnoreCase(this.domain) && dnsData.spfTxt.equalsIgnoreCase(this.spfTxt);
                break;
            case "srv":
                result = dnsData.domain.equalsIgnoreCase(this.domain) && dnsData.srvTxt.equalsIgnoreCase(this.srvTxt);
                break;
            case "txt":
                result = dnsData.domain.equalsIgnoreCase(this.domain) && dnsData.txtTxt.equalsIgnoreCase(this.txtTxt);
                break;
            case "ptr":
                result = dnsData.domain.equalsIgnoreCase(this.domain) && dnsData.ptrTxt.equalsIgnoreCase(this.ptrTxt);
                break;
            case "dmarc":
                result = dnsData.domain.equalsIgnoreCase(this.domain) && dnsData.dmarcTxt.equalsIgnoreCase(this.dmarcTxt);
                break;
        }

        return result;
    }

    @Override
    public int hashCode() {
        return 1;
    }
        
}
