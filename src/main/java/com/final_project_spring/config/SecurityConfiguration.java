package com.final_project_spring.config;

import static com.final_project_spring.entity.Permission.ADMIN_CREATE;
import static com.final_project_spring.entity.Permission.ADMIN_DELETE;
import static com.final_project_spring.entity.Permission.ADMIN_READ;
import static com.final_project_spring.entity.Permission.ADMIN_UPDATE;
import static com.final_project_spring.entity.Permission.USER_READ;
import static com.final_project_spring.entity.Role.ADMIN;
import static com.final_project_spring.entity.Role.USER;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @author Rajesh_Kawali
 * 
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;

	@Autowired
	private AuthenticationProvider authenticationProvider;

	@Autowired
	private LogoutHandler logoutHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf((csrf) -> csrf.ignoringRequestMatchers("/h2-console/**").disable())
				.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()))
				.authorizeHttpRequests((authorize) -> authorize
						.requestMatchers(toH2Console()).permitAll()
						.requestMatchers(
								"/api/v1/auth/**",
								"/api-documentation",
								"/swagger-api-docs",
								"/swagger-api-docs/**",
								"/swagger-ui/index.html",
								"/swagger-resources",
								"/swagger-resources/**",
								"/configuration/ui",
								"/configuration/security",
								"/swagger-ui/**",
								"/webjars/**",
								"/swagger-ui.html",
								"/api/activate",
								"/api/v1/auth/register",
								"/api/v1/auth/authenticate",

								"/login") // Добавьте этот путь
						.permitAll()
						.requestMatchers("/api/v1/customer/getAll").permitAll()
						.requestMatchers("/api/v1/customer/**").hasAnyRole(ADMIN.name(), USER.name())
						.requestMatchers(GET, "/api/v1/customer/**").hasAnyAuthority(ADMIN_READ.name(), USER_READ.name())
						.requestMatchers(POST, "/api/v1/customer/**").hasAuthority(ADMIN_CREATE.name())
						.requestMatchers(PUT, "/api/v1/customer/**").hasAuthority(ADMIN_UPDATE.name())
						.requestMatchers(DELETE, "/api/v1/customer/**").hasAuthority(ADMIN_DELETE.name())
						.anyRequest().authenticated())
				.sessionManagement((sessionManagement) -> sessionManagement
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.logout((logout) -> logout
						.logoutUrl("/api/v1/auth/logout").addLogoutHandler(logoutHandler)
						.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));

		return http.build();
	}
}
