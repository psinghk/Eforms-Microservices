package in.nic.eform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
@EnableEncryptableProperties
public class UploadDownloadDocsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploadDownloadDocsApplication.class, args);
	}
}
