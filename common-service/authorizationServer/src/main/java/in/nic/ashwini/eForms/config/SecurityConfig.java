package in.nic.ashwini.eForms.config;

import java.util.HashMap;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;

import in.nic.ashwini.eForms.security.manager.CustomAuthenticationManager;
import in.nic.ashwini.eForms.security.providers.LdapAuthenticationProvider;
import in.nic.ashwini.eForms.security.providers.RefreshTokenProvider;
import in.nic.ashwini.eForms.security.providers.TokenAuthenticationProvider;
import in.nic.ashwini.eForms.service.ValidationService;

@Configuration
//@EnableEncryptableProperties
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	public AuthenticationProvider ldapAuthenticationProvider() {
		return new LdapAuthenticationProvider();
	}

	@Bean
	public AuthenticationProvider tokenAuthenticationProvider() {
		return new TokenAuthenticationProvider();
	}

	@Bean
	public AuthenticationProvider refreshTokenProvider() {
		return new RefreshTokenProvider();
	}
	
//	@Bean
//	public UserDetailsService UserDetailsService() {
//		return new JpaUserDetailsService();
//	}

	@Bean
	public ValidationService validationService() {
		return new ValidationService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(ldapAuthenticationProvider()).authenticationProvider(tokenAuthenticationProvider());
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return new CustomAuthenticationManager();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().mvcMatchers("/**")
		//.access("hasIpAddress('::1') or hasIpAddress('127.0.0.1')")
		//.access("hasIpAddress('*')")
				//.anyRequest()
				.authenticated().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
	   return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
	}
}
