package in.nic.ashwini.eForms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication 
@EnableEncryptableProperties
public class EFormsResourceServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(EFormsResourceServerApplication.class, args);
	}
	

}
