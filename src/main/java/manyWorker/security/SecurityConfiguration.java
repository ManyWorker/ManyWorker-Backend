package manyWorker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

		@Autowired
		private JWTAuthenticationFilter JWTAuthenticationFilter;

		@Bean
		AuthenticationManager authenticationManager(AuthenticationConfiguration authConf) throws Exception {
			return authConf.getAuthenticationManager();
		}

		@Bean
		PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
					// Rutas públicas
					.requestMatchers(HttpMethod.POST, "/login").permitAll()
					.requestMatchers(HttpMethod.POST, "/trabajador").permitAll()
					.requestMatchers(HttpMethod.POST, "/cliente").permitAll()				
					.requestMatchers(HttpMethod.GET, "/trabajador/*").permitAll()

					// Rutas ADMINISTRADOR
					.requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
		            .requestMatchers(HttpMethod.GET, "/trabajador").hasRole("ADMINISTRADOR")
		            .requestMatchers(HttpMethod.GET, "/cliente", "/cliente/*").hasRole("ADMINISTRADOR")
					.requestMatchers(HttpMethod.PUT, "/banear/**").hasRole("ADMINISTRADOR")
					.requestMatchers(HttpMethod.PUT, "/desbanear/**").hasRole("ADMINISTRADOR")

					// Rutas TRABAJADOR
					.requestMatchers(HttpMethod.PUT, "/trabajador").hasRole("TRABAJADOR")
					.requestMatchers(HttpMethod.DELETE, "/trabajador").hasRole("TRABAJADOR")

					// Rutas CLIENTE
					.requestMatchers(HttpMethod.PUT, "/cliente").hasRole("CLIENTE")
					.requestMatchers(HttpMethod.DELETE, "/cliente").hasRole("CLIENTE")
					.requestMatchers("/cliente/miPerfil").hasRole("CLIENTE")

					// Poned los endpoints de la tarea 8 aqui ordenados (cada uno los endpoint de las entity q hicisteis en la primera parte)
					
					
					
					// Rutas SWAGGER
		            .requestMatchers("/v3/api-docs/**").permitAll()
		            .requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
		            
					// Resto de rutas requieren autenticación
					.anyRequest().authenticated())
			
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.addFilterBefore(JWTAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

			return http.build();
	}
}
