package in.nic.ashwini.eForms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import in.nic.ashwini.eForms.config.RibbonConfiguration;

@EnableEurekaClient
@SpringBootApplication
@RibbonClient(name = "custom", configuration = RibbonConfiguration.class)
@EnableEncryptableProperties
public class AuthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationApplication.class, args);
	}

}
