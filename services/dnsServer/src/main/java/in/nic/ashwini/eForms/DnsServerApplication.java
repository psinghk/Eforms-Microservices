package in.nic.ashwini.eForms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication 
@EnableEncryptableProperties
public class DnsServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(DnsServerApplication.class, args);
		log.info("DNS Micro Service started ...:)");
	}
}
