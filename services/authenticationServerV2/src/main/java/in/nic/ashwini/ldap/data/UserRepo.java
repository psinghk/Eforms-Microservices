package in.nic.ashwini.ldap.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.ldap.repository.LdapRepository;

public interface UserRepo extends LdapRepository<UserForSearch>{
	@Query("(uid={0})")
	UserForSearch findByUsername(String username);
}
