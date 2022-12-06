package in.nic.ashwini.eForms.entities;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dlist_bulk")
@Data
public class GenerateBulkPdf {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "registration_no")
	private String registrationNo;
	@Column(name = "list_name")
	private String list_name;
	@Column(name = "description_list")
	private String description_list;
	@Column(name = "list_mod")
	private String list_mod;
	@Column(name = "allowed_member_mail")
	private String allowed_member;
	@Column(name = "mail_Acceptance")
	private String non_nicnet;
	@Column(name = "list_temp")
	private String list_temp;
	@Column(name = "owner_Name")
	private String owner_name;
	@Column(name = "Owner_Email")
    private String owner_email;
	@Column(name = "owner_Mobile")
	private String owner_mobile;
	@Column(name = "moderator_Name")
    private String t_off_name;
	@Column(name = "moderator_Email")
    private String tauth_email;
	@Column(name = "moderator_Mobile")
	private String tmobile;
	@Column(name = "owner_Admin")
    private String owner_Admin;
	@Column(name = "moderator_Admin")   
    private String moderator_Admin;
	
}
