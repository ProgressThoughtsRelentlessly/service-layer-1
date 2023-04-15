package com.pthore.service.serviceLayer1.configs;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity(debug=true)
@Configuration
public class ApplicationSecurityConfigs extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtVerificationFilter jwtVerificationFilter;
	
	private Logger logger = LoggerFactory.getLogger(ApplicationSecurityConfigs.class);
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .anonymous().and()
		.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )
        .cors()
        .configurationSource(new CorsConfigurationSource() {
        	
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration config = new CorsConfiguration();
				config.addAllowedOriginPattern("*");
				config.setAllowedMethods(Collections.singletonList("*"));
				config.setAllowCredentials(true);
				config.setAllowedHeaders(Collections.singletonList("*"));
				config.setExposedHeaders(Arrays.asList("*"));  
				config.setMaxAge(3600L); // 5mins
				return config;
			}}).and().csrf().disable()
		.addFilterBefore(jwtVerificationFilter, BasicAuthenticationFilter.class)
			.authorizeRequests()
			.mvcMatchers("api/**").permitAll()
			.and().httpBasic();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("user").password("root").authorities("ROLE_USER").and().passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	protected UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager();
	}
}
