package in.nic.ashwini.eForms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEurekaClient
@SpringBootApplication
@EnableEncryptableProperties
public class EFormsBootV2Application {

	public static void main(String[] args) {
		SpringApplication.run(EFormsBootV2Application.class, args);
	}

}
