package com.esign.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;



@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication 
@EnableEncryptableProperties
@ComponentScan(basePackages = { "com.esign.demo" })
public class EsignApplication extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		
		SpringApplication.run(EsignApplication.class, args);
	}
}
