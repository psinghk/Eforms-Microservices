package in.nic.ashwini.eForms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.mvcMatchers("/verifyUser", "/verifyUserForPassApp","/authenticate", "/captcha", "/genrateCaptcha", "/actuator/**",
						"/callbackUrlForParichay", "/refreshToken","**/genpdf/**")
				.permitAll()
				// .mvcMatchers("/callbackUrlForParichay","/refreshToken").access("hasIpAddress('::1')
				// or hasIpAddress('127.0.0.1') or hasIpAddress('10.122.34.101')")
				.mvcMatchers("/validateOtp","**/validateOtp/**").hasAnyRole("PRE_AUTH")
				.mvcMatchers("/hogpannel/**","/**/hogpannel/**").hasAnyRole("COORDINATOR_PORTAL", "SUPERADMIN")
				.mvcMatchers("/admin/**", "/**/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
				.mvcMatchers("/ro/**", "/**/ro/**").hasAnyRole("RO", "SUPERADMIN")
				.mvcMatchers("/support/**", "/**/support/**").hasAnyRole("SUPPORT", "SUPERADMIN")
				.mvcMatchers("/coordinator/**", "/**/coordinator/**").hasAnyRole("CO", "SUPERADMIN")
				.mvcMatchers("/user/**", "/**/user/**").hasAnyRole("USER", "SUPERADMIN")
				.mvcMatchers("/validateOtp/**", "/**/validateOtp/**").hasAnyRole("PRE_AUTH_USER", "SUPERADMIN")
				.mvcMatchers("/support-app-service/**", "/**/support-app-service/**").hasAnyRole("SUPPORT_APP", "SUPERADMIN")
				.mvcMatchers("/changePassword/**", "/**/changePassword/**").hasAnyRole("PASSAPP_FULLY_AUTHENTICATED", "SUPERADMIN")
				.mvcMatchers("/passapp/**", "/**/passapp/**").hasAnyRole("PASSAPP_USER", "SUPERADMIN")
				.mvcMatchers("/**").hasAnyRole("FULLY_AUTHENTICATED", "SUPERADMIN");

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.addFilterAfter(new SameSiteFilter(), BasicAuthenticationFilter.class)
				.csrf()
				.ignoringAntMatchers("/authenticate", "/validateOtp")
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
//		
		//http.csrf().disable();

		http.headers().referrerPolicy(ReferrerPolicy.SAME_ORIGIN);

//        http
//		.headers()
//		.httpStrictTransportSecurity()
//		.includeSubDomains(true)
//		.maxAgeInSeconds(31536000);
//
		http.headers().contentSecurityPolicy("script-src 'self'");

		http.headers().frameOptions().deny();
//
//		http
//        .requiresChannel()
//        .anyRequest()
//        .requiresSecure();

	}
}
