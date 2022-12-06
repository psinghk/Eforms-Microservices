package in.nic.ashwini.ldap.scheduler;

import java.util.Collections;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import in.nic.ashwini.ldap.entities.AccessTokenEntity;
import in.nic.ashwini.ldap.utility.AESUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AccessToken {

	private final RestTemplate restTemplate;
	
	private AccessTokenEntity accessTokenEntity;
	
	@Value("${zimbra.ldap.url}")
	private String ZIMBRA_LDAP_URL;
	
	@Value("${zimbra.ldap-host.handshake}")
	private String HANDSHAKE_KEY;
	
	@Value("${zimbra.ldap.api.secretkey}")
	private String SECRET_KEY;

	@Autowired
	public AccessToken(RestTemplate restTemplate, AccessTokenEntity accessTokenEntity) {
		super();
		this.restTemplate = restTemplate;
		this.accessTokenEntity=accessTokenEntity;
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				return false;
			}
		});
	}

	// Time is in millisecond ... for 10 mins = 10*60*1000
	//@Scheduled(fixedDelay = 36000000)
	@Scheduled(fixedDelay = 420000)
	//@Scheduled(fixedDelay = 1000)
	public void fetchAndUpdateAccessToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String handShakeUrl = ZIMBRA_LDAP_URL + "handshake";
		log.info(handShakeUrl);
		HttpEntity<?> entity = new HttpEntity<>(HANDSHAKE_KEY, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(handShakeUrl, entity, String.class);
		String handshakeRespStr = response.getBody();
		if (handshakeRespStr != null && !handshakeRespStr.isEmpty()) {
			handshakeRespStr = handshakeRespStr.substring(1, handshakeRespStr.length() - 1);
			String handshakeDrcStr = AESUtil.decrypt(handshakeRespStr, SECRET_KEY);
			JSONParser parser = new JSONParser();
			try {
				JSONObject handshakeRespJson = (JSONObject) parser.parse(handshakeDrcStr);
				String accessToken = handshakeRespJson.get("result").toString();
				boolean status = (boolean) handshakeRespJson.get("status");
				if (status && !accessToken.isEmpty()) {
					log.info("ACCESS TOKEN ::: {}",accessToken);
					accessTokenEntity.setAccessToken(accessToken);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}
