/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.config;

import javax.sql.DataSource;

import org.sparta.springwebutils.jdbc.SpartaNamedParameterJdbcTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration.
 * 
 * @author danieldiehl
 */
@Configuration
public class AppConfig {

	/**
	 * Sparta Named Parameter JDBC Template bean.
	 * 
	 * @param ds data source
	 * @return new bean
	 */
	@Bean
	public SpartaNamedParameterJdbcTemplate spartaNamedJdbcTemplate(DataSource ds) {
		return new SpartaNamedParameterJdbcTemplate(ds);
	}
	
}
