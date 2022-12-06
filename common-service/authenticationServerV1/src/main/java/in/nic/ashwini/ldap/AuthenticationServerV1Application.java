package in.nic.ashwini.ldap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import brave.sampler.Sampler;
import in.nic.ashwini.ldap.config.RibbonConfiguration;

//@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
@RibbonClient(name = "custom", configuration = RibbonConfiguration.class)
@EnableEncryptableProperties
public class AuthenticationServerV1Application {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServerV1Application.class, args);
	}
}
