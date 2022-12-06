package in.nic.ashwini.eForms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
@EnableEncryptableProperties
public class ReportingServerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ReportingServerApplication.class, args);
	}
	
//	@Bean
//	@Primary
//	public ObjectMapper geObjMapper(){
//	    return new ObjectMapper()
//	            .registerModule(new ParameterNamesModule())
//	            .registerModule(new Jdk8Module())
//	            .registerModule(new JavaTimeModule());
//	}
	
}
