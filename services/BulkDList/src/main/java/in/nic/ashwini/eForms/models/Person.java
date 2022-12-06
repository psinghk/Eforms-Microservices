package in.nic.ashwini.eForms.models;



import java.util.List;

public class Person {
	private String registration_no;
	private List<Moderators> moderators;
	private List<Owners> owners;
	public List<Moderators> getModerators() {
		return moderators;
	}
	public void setModerators(List<Moderators> moderators) {
		this.moderators = moderators;
	}
	public List<Owners> getOwners() {
		return owners;
	}
	public void setOwners(List<Owners> owners) {
		this.owners = owners;
	}
	public String getRegistration_no() {
		return registration_no;
	}
	public void setRegistration_no(String registration_no) {
		this.registration_no = registration_no;
	}
	
}
