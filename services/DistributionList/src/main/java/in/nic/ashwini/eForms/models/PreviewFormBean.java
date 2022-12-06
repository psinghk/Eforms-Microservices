package in.nic.ashwini.eForms.models;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PreviewFormBean {
private String registrationNo;

@NotNull(message = "List Name should not be empty")
//@Pattern(regexp = "^[\\\\w\\\\-\\\\.\\\\+]+@[a-zA-Z0-9\\\\.\\\\-]+.[a-zA-z0-9]{2,4}$",message = "Please enter list name in correct format")
private String list_name;

@Pattern(regexp = "^[^<>&%]{2,300}+$",message = "Please enter list description")
@NotNull(message = "Description of list should not be empty")
private String description_list;

@Pattern(regexp = "^[0-9]{1,9}$",message = "Please enter memberCount")
@NotNull(message = "member count should not be empty")
private String memberCount;

@Pattern(regexp = "^(?:Yes|No|yes|no)$",message = "Please enter list Moderated")
@NotNull(message = "List moderated should not be empty")
private String list_mod;

@NotNull(message = "AllowedMemberMail should not be empty")
@Pattern(regexp = "^(?:Yes|No|yes|no)$",message = "Please enter list Moderated")
private String allowed_member;

@NotNull(message = "OtherMemberMail should not be empty")
@Pattern(regexp = "^(?:Yes|No|yes|no)$",message = "Please enter mail acceptance")
private String non_nicnet;

@NotNull(message = "ListTemp should not be empty")
@Pattern(regexp = "^(?:Yes|No|yes|no)$",message = "Please enter list temp")
private String list_temp;

    private String validDate;
    private String organization;
	private String employment;
	private String department;
	private String otherDept;
	private String state;
	private String ministry;
	private String remarks;
	
	@NotEmpty
	private Boolean tnc;
	private String omName;
	private String omMobile;
	private String omEmail;
    private String  formType;

    private String registration_no;
    private List<Moderators> moderators;
    private List<Owners> owners;
	
}
