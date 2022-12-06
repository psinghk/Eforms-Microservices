package in.nic.ashwini.eForms.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.nic.ashwini.eForms.db.master.entities.LoginTrail;
import in.nic.ashwini.eForms.db.master.repositories.LoginTrailRepository;
import in.nic.ashwini.eForms.service.UtilityService;
import lombok.experimental.UtilityClass;

public class LogoutController {
	
	@Autowired
	LoginTrailRepository loginTrailRepository;
	@Autowired
	UtilityService utilityService;
	
	
	@PostMapping("/logout")
	public Boolean logout(@RequestParam("email") String email,@RequestParam("clientIp") String clientIp,@RequestParam("sessionId") String sessionId,@RequestParam("remarks") String remarks) {
		
		Set<String> aliases = utilityService.fetchAliasesFromLdap(email);
		boolean flag=false;
		List<LoginTrail> loginTrail = loginTrailRepository.findByEmailInAndTokenAndStatus(aliases, sessionId, 0);
		
		for(LoginTrail loginUser:loginTrail) {
			LocalDateTime currentTime = LocalDateTime.now();
			loginUser.setLogoutTime(currentTime);
			loginUser.setLogoutRemarks(remarks);
		    LoginTrail login = loginTrailRepository.save(loginUser);
			if(login.getId()>0) {
				flag=true;
			}
		}
		return flag;
	}

}
