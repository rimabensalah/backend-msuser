package com.firstservice.userservice;



import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.firstservice.userservice.domain.Role;
import com.firstservice.userservice.domain.RoleName;
import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.service.UserServiceImpl;

@SpringBootApplication
@EnableEurekaClient
public class UserserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}
	/*@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder() ;
		
	}*/
	//@Bean
	/*CommandLineRunner run(UserServiceImpl userService) {
		return args ->{
			userService.saveRole(new Role(1L,RoleName.ROLE_USER));
			userService.saveRole(new Role(2L,RoleName.ROLE_ADMIN));
			
			
			userService.saveUser(new Utilisateur(1L,"Ryma","ryma@gmail.com","12345", new ArrayList<>()));
			userService.addRoleToUser("ryma", RoleName.ROLE_USER);
			
		};*/
		
	//}
}
