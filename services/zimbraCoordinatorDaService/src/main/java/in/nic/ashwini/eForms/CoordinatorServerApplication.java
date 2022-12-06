package in.nic.ashwini.eForms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
public class CoordinatorServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoordinatorServerApplication.class, args);
	}

}
