package in.nic.ashwini.eForms.config;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
//IMPORTANT: it has to be a normal configuration class, 
//not extending WebMvcConfigurerAdapter or other Spring Security class
    @Bean
    public FilterRegistrationBean<CorsFilter> customCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        //config.addAllowedOrigin("https://localhost:4200");
        //config.addAllowedOrigin("https://10.122.34.101");
        //config.addAllowedOrigin("https://164.100.2.97");
        config.addAllowedOrigin("*");
        //config.setAllowedHeaders(Arrays.asList("Set-Cookie","Access-Control-Allow-Origin","Access-Control-Allow-Credentials","Vary","Content-Type","Content-Length","Connection","Date"));
        config.addAllowedHeader("*");
        //config.addAllowedMethod("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.setMaxAge(3600l);
        //config.addAllowedMethod("DELETE");
        //config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}