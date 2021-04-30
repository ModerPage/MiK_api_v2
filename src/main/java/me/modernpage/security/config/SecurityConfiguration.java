package me.modernpage.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.modernpage.security.UserAuthenticationProvider;
import me.modernpage.security.filter.AuthFilter;
import me.modernpage.security.filter.AuthVerifierFilter;
import me.modernpage.security.handler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

	private final AccessDeniedHandler accessDeniedHandler;
	private final UserNotLoginHandler notLoginHandler;
	private final UserLoginSuccessHandler loginSuccessHandler;
	private final UserLoginFailureHandler userLoginFailureHandler;
	private final UserLogoutSuccessHandler logoutSuccessHandler;
	private final UserAuthenticationProvider userAuthenticationProvider;
	private final ObjectMapper objectMapper;

	public SecurityConfiguration(AccessDeniedHandler accessDeniedHandler,
								 UserNotLoginHandler notLoginHandler,
								 UserLoginSuccessHandler loginSuccessHandler,
								 UserLoginFailureHandler userLoginFailureHandler,
								 UserLogoutSuccessHandler logoutSuccessHandler,
								 UserAuthenticationProvider userAuthenticationProvider,
								 ObjectMapper objectMapper) {
		this.accessDeniedHandler = accessDeniedHandler;
		this.notLoginHandler = notLoginHandler;
		this.loginSuccessHandler = loginSuccessHandler;
		this.userLoginFailureHandler = userLoginFailureHandler;
		this.logoutSuccessHandler = logoutSuccessHandler;
		this.userAuthenticationProvider = userAuthenticationProvider;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider);
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers(JwtConfig.antMatchers.split(","))
					.permitAll()
				.anyRequest()
					.authenticated()
				.and()
				.httpBasic()
					.authenticationEntryPoint(notLoginHandler)
				.and()
				.logout()
					.logoutSuccessHandler(logoutSuccessHandler)
				.and()
				.exceptionHandling()
					.accessDeniedHandler(accessDeniedHandler)
				.and()
					.cors()
				.and()
					.csrf().disable();
		http
				.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.headers()
					.cacheControl();
		AuthFilter authFilter = new AuthFilter(authenticationManager());
		authFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
		authFilter.setAuthenticationFailureHandler(userLoginFailureHandler);
		http
				.addFilter(authFilter)
				.addFilterAfter(new AuthVerifierFilter(objectMapper), AuthFilter.class);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

}
