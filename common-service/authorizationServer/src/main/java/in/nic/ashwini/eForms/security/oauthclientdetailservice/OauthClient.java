package in.nic.ashwini.eForms.security.oauthclientdetailservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import in.nic.ashwini.eForms.db.slave.entities.OauthFromSlave;

public class OauthClient implements ClientDetails{
	
	private static final long serialVersionUID = 1L;
	private final OauthFromSlave oauthClientDetails;

	public OauthClient(OauthFromSlave o) {
		this.oauthClientDetails = o;
	}

	@Override
	public String getClientId() {
		return oauthClientDetails.getClientId();
	}

	@Override
	public boolean isSecretRequired() {
		return true;
	}

	@Override
	public String getClientSecret() {
		return oauthClientDetails.getClientSecret();
	}

	@Override
	public boolean isScoped() {
		return true;
	}

	@Override
	public Set<String> getScope() {
		Set<String> s = new HashSet<>();
		s.add(oauthClientDetails.getScope());
		return s;
	}
	
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		GrantedAuthority grantedAuthority = () -> oauthClientDetails.getScope();
		List<GrantedAuthority> list = new ArrayList<>();
		list.add(grantedAuthority);
		return list;
	}

	@Override
	public Set<String> getAuthorizedGrantTypes() {
		return new HashSet<String>(Arrays.asList(oauthClientDetails.getGrantType().split(",")));
	}

	@Override
	public Integer getAccessTokenValiditySeconds() {
		return oauthClientDetails.getAccessTokenExpiryTimeInSeconds();
	}

	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return oauthClientDetails.getRefreshTokenExpiryTimeInSeconds();
	}

	@Override
	public boolean isAutoApprove(String scope) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Set<String> getResourceIds() {
		return null;
	}
	
	@Override
	public Set<String> getRegisteredRedirectUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getAdditionalInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
