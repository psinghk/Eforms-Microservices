package in.nic.ashwini.ldap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@Profile("default")
@EnableLdapRepositories(basePackages = "in.nic.ashwini.ldap.**")
public class AppConfig {

    @Autowired
    private MyLdapProperties ldapProperties;
    
    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapProperties.getUrl());
        contextSource.setUserDn(ldapProperties.getAdminId());
        contextSource.setPassword(ldapProperties.getAdminPassword());
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
    	return new LdapTemplate(contextSource());
    }

}
