package in.nic.ashwini.eForms.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "slaveEntityManagerFactory", transactionManagerRef = "slaveTransactionManager", basePackages = {
		"in.nic.ashwini.eForms.db.slave.repositories" })
public class SlaveDbConfig {
	
	//@Primary
	@Bean(name = "slaveDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.slave")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	//@Primary
	@Bean(name = "slaveEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean slaveEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("slaveDataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("in.nic.ashwini.eForms.db.slave.entities")
				.persistenceUnit("slave").build();
	}

	//@Primary
	@Bean(name = "slaveTransactionManager")
	public PlatformTransactionManager slaveTransactionManager(
			@Qualifier("slaveEntityManagerFactory") EntityManagerFactory slaveEntityManagerFactory) {
		return new JpaTransactionManager(slaveEntityManagerFactory);
	}
}