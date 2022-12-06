package in.ashwini.nic.configurationServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

import in.ashwini.nic.configurationServer.config.RibbonConfiguration;

@EnableConfigServer
@SpringBootApplication
@EnableDiscoveryClient
@RibbonClient(name = "custom", configuration = RibbonConfiguration.class)
public class ConfigurationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigurationServerApplication.class, args);
	}

}
