package in.ashwini.nic.notificationServer.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
            http
            .authorizeRequests()
    		.mvcMatchers("/**")
    		//.access("hasIpAddress('::1') or hasIpAddress('127.0.0.1')")
    		//.access("hasIpAddress('*')")
    		//.anyRequest()
    		.permitAll();
    		
    		http.csrf().disable();

    		http
    		.sessionManagement()
    		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
