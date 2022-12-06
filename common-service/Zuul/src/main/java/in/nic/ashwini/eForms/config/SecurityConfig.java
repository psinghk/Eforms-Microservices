package in.nic.ashwini.eForms.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

import in.nic.ashwini.eForms.controller.ResponseBean;
import in.nic.ashwini.eForms.filters.CaptchaFilter;
import in.nic.ashwini.eForms.filters.ErrorFilter;
import in.nic.ashwini.eForms.filters.LogFilter;
import in.nic.ashwini.eForms.filters.LogPostFilter;
import in.nic.ashwini.eForms.filters.RegNumberValidationFilter;
import in.nic.ashwini.eForms.filters.TokenValidationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		//web.ignoring().antMatchers("/verifyUser","/oauth-server/**","/oauth/**","/captcha","/test");
		web.ignoring().antMatchers("/verifyUser","/oauth-server/**","/oauth/**","/captcha","/test","/refreshToken", "/config-server/**","**/genpdf/**");
	}

	@Bean
	public ResponseBean responseBean() {
		return new ResponseBean();
	}
	
	@Bean
	public CustomFallbackProvider fallBackProvider() {
		return new CustomFallbackProvider();
	}
	
	@Bean
	public LogFilter logFilter() {
		return new LogFilter();
	}
	
	@Bean
	public CaptchaFilter captchaFilter() {
		return new CaptchaFilter();
	}
	
	@Bean
	public LogPostFilter logPostFilter() {
		return new LogPostFilter();
	}
	
	@Bean
	public ErrorFilter errorFilter() {
		return new ErrorFilter();
	}
	
	@Bean
	public RegNumberValidationFilter regNumberValidation() {
		return new RegNumberValidationFilter();
	}
	
	@Bean
	public TokenValidationFilter tokenValidation() {
		return new TokenValidationFilter();
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
