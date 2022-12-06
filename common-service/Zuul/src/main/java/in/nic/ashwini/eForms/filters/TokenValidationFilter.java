package in.nic.ashwini.eForms.filters;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import in.nic.ashwini.eForms.services.EncryptionService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenValidationFilter extends ZuulFilter {

	@Autowired
	public TokenStore tokenStore;

	private TokenExtractor tokenExtractor = new BearerTokenExtractor();

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	@Override
	public boolean shouldFilter() {
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
		if (request.getHeader("Authorization") != null) {
			return request.getHeader("Authorization").contains("Bearer ");
		}
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		log.info("setting email parameter in request from token");
		HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
//		String actualBearerToken = "";
//		if (request.getHeader("Authorization") != null) {
//			String bearerToken = request.getHeader("Authorization");
//			String[] bearerTokenInArray = bearerToken.split(" ");
//			actualBearerToken = bearerTokenInArray[1];
//		}
		Authentication authentication = tokenExtractor.extract(request);
		String accessToken = authentication.getPrincipal().toString();

		String[] parts = accessToken.split("\\.", 0);

		byte[] bytes = Base64.getUrlDecoder().decode(parts[1]);
		String decodedString = new String(bytes, StandardCharsets.UTF_8);

		System.out.println("Decoded: " + decodedString);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;

		try {
			// convert JSON string to Map
			map = mapper.readValue(decodedString, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		String decodedUsername = "";

		String ip = "";
		if (!decodedString.contains("ROLE_PASSAPP_USER") || decodedString.contains("ROLE_FULLY_AUTHENTICATED") || decodedString.contains("ROLE_PASSAPP_FULLY_AUTHENTICATED")) {
			decodedUsername = map.get("username").toString().trim();
			ip = map.get("ip").toString().trim();
		} else {
			decodedUsername = map.get("user_name").toString().trim();
		}

		OAuth2Authentication oauth2Authentication = loadAuthentication(accessToken);
		String username = oauth2Authentication.getName();
		if(decodedString.contains("ROLE_PASSAPP_FULLY_AUTHENTICATED")) {
			if (!(username.contains(decodedUsername) && username.contains(ip))) {
				throw new InvalidTokenException("username mismatches... Please check the token.");
			}
		}else {
			if (!username.equalsIgnoreCase(decodedUsername)) {
				throw new InvalidTokenException("username mismatches... Please check the token.");
			}
		}

		String clientIp = fetchClientIp(request);
//		if(!ip.equalsIgnoreCase(clientIp)) {
//			throw new InvalidTokenException("You are coming from a different IP, please generate a new token.");
//		}

		Map<String, List<String>> newParameterMap = new HashMap<>();
		Map<String, String[]> parameterMap = request.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String key = entry.getKey();
			String[] values = entry.getValue();
			newParameterMap.put(key, Arrays.asList(values));
		}
		
		if(decodedString.contains("ROLE_PASSAPP_FULLY_AUTHENTICATED")) {
			username = username.split(";")[0];
		}
		
		newParameterMap.put("email", Arrays.asList(username));
		newParameterMap.put("clientIp", Arrays.asList(fetchClientIp(request)));
		String allowedFormsInString = "";
		if (request.getHeader("AllowedForms") != null && !request.getHeader("AllowedForms").isEmpty()) {
			allowedFormsInString = EncryptionService.decrypt(request.getHeader("AllowedForms"));
			newParameterMap.put("allowedForms", Arrays.asList(allowedFormsInString.split(":")));
		} else {
			newParameterMap.put("allowedForms", Arrays.asList(""));
		}
		RequestContext.getCurrentContext().setRequestQueryParams(newParameterMap);
		return null;
	}

	private OAuth2Authentication loadAuthentication(String accessTokenValue) {
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);

		if (accessToken == null) {
			throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
		}
		if (accessToken.isExpired()) {
			this.tokenStore.removeAccessToken(accessToken);
			throw new InvalidTokenException("Access token expired: " + accessTokenValue);
		}
		OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
		if (result == null) {
			throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
		}
		return result;
	}

	private String fetchClientIp(HttpServletRequest request) {
		String remoteAddr = "";
		if (request != null) {
			remoteAddr = request.getHeader(FilterConstants.X_FORWARDED_FOR_HEADER);
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			} else {
				remoteAddr = new StringTokenizer(remoteAddr, ",").nextToken().trim();
			}
		}
		return remoteAddr;
	}
}
