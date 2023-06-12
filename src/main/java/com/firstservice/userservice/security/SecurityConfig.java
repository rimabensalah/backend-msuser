package com.firstservice.userservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.firstservice.userservice.security.jwt.AuthEntryPointJwt;
import com.firstservice.userservice.security.jwt.AuthTokenFilter;
import com.firstservice.userservice.service.UserDetailsServiceImpl;
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(
		//securedEnabled = false,
		//jsr250Enabled = true,
		prePostEnabled = true
		)
public class SecurityConfig {
	//private  UserDetailsService userDetailsService;
	 @Autowired
	  UserDetailsServiceImpl userDetailsService;
	  @Autowired
	  private AuthEntryPointJwt unauthorizedHandler;

	  @Bean
	  public AuthTokenFilter authenticationJwtTokenFilter() {
	    return new AuthTokenFilter();
	  }
	  @Bean
	  public DaoAuthenticationProvider authenticationProvider() {
	      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	       
	      authProvider.setUserDetailsService(userDetailsService);
	      authProvider.setPasswordEncoder(passwordEncoder());
	   
	      return authProvider;
	  }
	  
	  @Bean
	  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
	    return authConfig.getAuthenticationManager();
	  }
	  @Bean
	  public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	  }



	  /*@Bean
	  public DaoAuthenticationProvider authenticationProvider() {
	      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	       
	      authProvider.setUserDetailsService(userDetailsService);
	      authProvider.setPasswordEncoder(passwordEncoder());
	   
	      return authProvider;
	      }*/
	  
	/*protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		//super.configure(auth);
	}*/
	 /* @Bean
	    public InMemoryUserDetailsManager userDetailsService() {
	        UserDetails user = User.withDefaultPasswordEncoder()
	                .username("user")
	                .password("password")
	                .roles("USER")
	                .build();
	        UserDetails admin = User.withDefaultPasswordEncoder()
	                .username("admin")
	                .password("password")
	                .roles("ADMIN")
	                .build();
	        return new InMemoryUserDetailsManager(user, admin);
	    }*/

	@Bean
	public  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		//http.csrf().disable();
		//http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		//http.authorizeHttpRequests().anyRequest().permitAll();
		//http.addFilter(null);
		//super.configure(http);
		//http.authorizeRequests() .antMatchers("/api/user/save").permitAll()
		//.antMatchers("/api/users").access("hasRole('USER')")
		//.antMatchers("/get**}").access("hasRole('ADMIN')")
		//.antMatchers("/retrieve-all-clients").access("hasRole('ADMIN')")
		 //.anyRequest()
		// .authenticated()
		// .and()
		// .httpBasic().and().csrf().disable(); 
		//return http.build();
		/*return http
		.csrf(csrf -> csrf.ignoringAntMatchers("/api/roles"))
		.authorizeHttpRequests(auth ->{
			
			//auth.antMatchers("/api/users").hasRole("USER");
			auth.antMatchers("/api/user/save").hasRole("ADMIN");
			auth.antMatchers("/api/role/**").hasRole("ADMIN");
			
		})
		.httpBasic(Customizer.withDefaults())
		.build();*/
		   http.cors().and().csrf().disable()
	        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
	        .authorizeRequests().antMatchers("/api/auth/**").permitAll()
	        .antMatchers("/api/**").permitAll()
	        .anyRequest().authenticated();
	    
	    http.authenticationProvider(authenticationProvider());

	    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	    
	    return http.build();
	}

}
