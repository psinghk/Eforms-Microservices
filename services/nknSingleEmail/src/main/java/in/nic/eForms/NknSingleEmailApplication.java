package in.nic.eForms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication 
@EnableEncryptableProperties
public class NknSingleEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(NknSingleEmailApplication.class, args);
	}

}
