package in.nic.ashwini.eForms.security.oauthclientdetailservice;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import in.nic.ashwini.eForms.db.slave.entities.OauthFromSlave;
import in.nic.ashwini.eForms.db.slave.repositories.OauthClientRepositoryToRead;

public class OauthClientDetailService implements ClientDetailsService{
	
	@Autowired
	private OauthClientRepositoryToRead oauthClientRepoToRead;

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		Optional<OauthFromSlave> oc = oauthClientRepoToRead.findFirstByClientId(clientId);
		OauthFromSlave o = oc.orElseThrow(()->new NoSuchClientException("Invalid Client ID"));
		return new OauthClient(o);
	}

}
