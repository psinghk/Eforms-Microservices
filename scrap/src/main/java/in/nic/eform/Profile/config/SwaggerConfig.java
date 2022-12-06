package in.nic.eform.Profile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


// Do we really need Swagger service ....
// Add required configuration in  class  in.nic.eform.config.ApplicationConfig.java

//@Configuration
//@EnableSwagger2
public class SwaggerConfig {

//	@Bean
//	public Docket api() {
//		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
//				.paths(Predicates.not(PathSelectors.regex("/error.*"))).build();
//	}
}