package se.plushogskolan.casemanagement.config;


import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import se.plushogskolan.casemanagement.auditing.CustomAuditorAware;

@Configuration
@EnableJpaRepositories("se.plushogskolan.casemanagement.repository")
@EnableTransactionManagement
public abstract class InfrastructureConfig {
	
	@Bean
	public abstract DataSource dataSource();
	
	@Bean
	public abstract JpaVendorAdapter jpaVendorAdapter();

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setJpaVendorAdapter(jpaVendorAdapter());
		factory.setPackagesToScan("se.plushogskolan.casemanagement.model");

		return factory;
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new CustomAuditorAware();
	}

}
