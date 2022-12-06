package in.nic.eform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class eFormServerApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(eFormServerApplication.class, args);
        System.out.println( "eForm Server Started ...:)" );
    }
    
    @Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
