package in.nic.ashwini.eForms.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import in.nic.ashwini.eForms.repositories.EmailOtpRepository;
import in.nic.ashwini.eForms.repositories.ErrorLoginDetailsRepository;
import in.nic.ashwini.eForms.repositories.ErrorOtpDetailsRepository;
import in.nic.ashwini.eForms.repositories.LoginDetailsRepository;
import in.nic.ashwini.eForms.repositories.MobileOtpRepository;
import in.nic.ashwini.eForms.security.granters.CustomRefreshTokenGranter;
import in.nic.ashwini.eForms.security.granters.DaOtpTokenGranter;
import in.nic.ashwini.eForms.security.granters.DaTokenGranter;
import in.nic.ashwini.eForms.security.granters.MfaTokenGranter;
import in.nic.ashwini.eForms.security.granters.NonGovMfaTokenGranter;
import in.nic.ashwini.eForms.security.granters.NonGovTokenGranter;
import in.nic.ashwini.eForms.security.granters.PasswordTokenGranter;
import in.nic.ashwini.eForms.security.granters.ResendOtpTokenGranter;
import in.nic.ashwini.eForms.security.granters.SupportMfaTokenGranter;
import in.nic.ashwini.eForms.security.granters.UpdateMobileMfaTokenGranter;
import in.nic.ashwini.eForms.security.granters.VerifyUpdateMobileMfaTokenGranter;
import in.nic.ashwini.eForms.security.oauthclientdetailservice.OauthClientDetailService;
import in.nic.ashwini.eForms.security.tokenenhancer.CustomTokenConverter;
import in.nic.ashwini.eForms.service.MfaService;
import in.nic.ashwini.eForms.service.UtilityService;
import in.nic.ashwini.eForms.service.ValidationService;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter implements ResourceLoaderAware {

	private final AuthenticationManager authenticationManager;
	private final ResourceLoader resourceLoader;
	private final MfaService mfaService;
	private final UtilityService utilityService;
	private final ValidationService validationService;
	private final EmailOtpRepository emailOtpRepository;
	private final MobileOtpRepository mobileOtpRepository;
	private final LoginDetailsRepository loginDetailsRepository;
	private final ErrorLoginDetailsRepository errorloginDetailsRepository;
	private final ErrorOtpDetailsRepository errorOtpDetailsRepository;

	@Value("${ssl.key}")
	private String sslKey;

	@Autowired
	public AuthorizationServer(AuthenticationManager authenticationManager, MfaService mfaService,
			ResourceLoader resourceLoader, UtilityService utilityService, ValidationService validationService,
			EmailOtpRepository emailOtpRepository, MobileOtpRepository mobileOtpRepository,
			LoginDetailsRepository loginDetailsRepository, ErrorLoginDetailsRepository errorloginDetailsRepository,
			ErrorOtpDetailsRepository errorOtpDetailsRepository) {
		this.authenticationManager = authenticationManager;
		this.mfaService = mfaService;
		this.resourceLoader = resourceLoader;
		this.utilityService = utilityService;
		this.validationService = validationService;
		this.emailOtpRepository = emailOtpRepository;
		this.mobileOtpRepository = mobileOtpRepository;
		this.loginDetailsRepository = loginDetailsRepository;
		this.errorloginDetailsRepository = errorloginDetailsRepository;
		this.errorOtpDetailsRepository = errorOtpDetailsRepository;
	}

	@Autowired
	public PasswordEncoder PasswordEncoder;

	@Bean
	public OauthClientDetailService clientDetailService() {
		return new OauthClientDetailService();
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailService());
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				// .reuseRefreshTokens(false)
				.tokenStore(tokenStore()).accessTokenConverter(jwtAccessTokenConverter())
				.tokenGranter(tokenGranter(endpoints));
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter jwt = new CustomTokenConverter();
		/*
		 * For production we can keep the file in FileSystem, so that certificate could
		 * be updated after a regular interval of time To load the file from a FileSyetm
		 * use following :-
		 * resourceLoader.getResource("file:/eforms/config/keystore/eforms.jks")
		 */
		KeyStoreKeyFactory keyPair = new KeyStoreKeyFactory(resourceLoader.getResource("classpath:eforms.jks"),
				sslKey.toCharArray());
		jwt.setKeyPair(keyPair.getKeyPair("eforms"));
		return jwt;
	}

	private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
		List<TokenGranter> granters = new ArrayList<TokenGranter>(Arrays.asList(endpoints.getTokenGranter()));
		granters.add(new PasswordTokenGranter(endpoints, authenticationManager, mfaService, utilityService,
				validationService, loginDetailsRepository, errorloginDetailsRepository));
		granters.add(new MfaTokenGranter(endpoints, authenticationManager, mfaService, utilityService,
				validationService, loginDetailsRepository, errorOtpDetailsRepository));
		granters.add(new SupportMfaTokenGranter(endpoints, authenticationManager, mfaService, utilityService,
				validationService));
		granters.add(new NonGovTokenGranter(endpoints, utilityService, validationService, mfaService));
		granters.add(new NonGovMfaTokenGranter(endpoints, mfaService, utilityService));
		granters.add(new ResendOtpTokenGranter(endpoints, authenticationManager, mfaService, utilityService,
				validationService, emailOtpRepository, mobileOtpRepository));
		granters.add(new UpdateMobileMfaTokenGranter(endpoints, authenticationManager, mfaService, utilityService,
				validationService));
		granters.add(new VerifyUpdateMobileMfaTokenGranter(endpoints, authenticationManager, mfaService, utilityService,
				validationService));
		granters.add(new CustomRefreshTokenGranter(endpoints, authenticationManager, mfaService, utilityService,
				validationService));
		granters.add(
				new DaTokenGranter(endpoints, authenticationManager, mfaService, utilityService, validationService));
		granters.add(
				new DaOtpTokenGranter(endpoints, authenticationManager, mfaService, utilityService, validationService));
		return new CompositeTokenGranter(granters);
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {

	}

	@Bean
	public CustomTokenConverter customTokenEnhancer() {
		return new CustomTokenConverter();
	}
}
