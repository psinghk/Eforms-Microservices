package in.nic.ashwini.eForms.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
//uidcheck
@Table(name="uidcheck")
public class UidCheck 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String uid;
	
	public UidCheck()
	{}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "UidCheck [uid=" + uid + "]";
	}
	
	
}
