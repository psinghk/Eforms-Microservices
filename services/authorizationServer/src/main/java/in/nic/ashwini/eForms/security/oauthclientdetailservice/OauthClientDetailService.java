package in.nic.ashwini.eForms.security.oauthclientdetailservice;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import in.nic.ashwini.eForms.entities.Oauth;
import in.nic.ashwini.eForms.repositories.OauthClientRepository;

public class OauthClientDetailService implements ClientDetailsService{
	
	@Autowired
	private OauthClientRepository oauthClientRepo;

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		Optional<Oauth> oc = oauthClientRepo.findFirstByClientId(clientId);
		Oauth o = oc.orElseThrow(()->new NoSuchClientException("Invalid Client ID"));
		return new OauthClient(o);
	}

}
