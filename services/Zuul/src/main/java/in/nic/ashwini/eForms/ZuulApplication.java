package in.nic.ashwini.eForms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import in.nic.ashwini.eForms.config.RibbonConfiguration;

@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
@RibbonClient(name = "custom", configuration = RibbonConfiguration.class)
@EnableEncryptableProperties
public class ZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}
	
//	/**
//     * Solve upload file reset problem
//     * @return
//     */
//    @Bean
//    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory(){
//        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
//
//        factory.addConnectorCustomizers((TomcatConnectorCustomizer)  connector -> {
//            if(connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>){
//                ( (AbstractHttp11Protocol<?>)connector.getProtocolHandler()).setMaxSwallowSize(-1);
//            }
//        });
//
//        return factory;
//    }
}
