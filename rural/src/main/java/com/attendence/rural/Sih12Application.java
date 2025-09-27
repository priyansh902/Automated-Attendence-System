package com.attendence.rural;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.attendence.rural.Model.Role;
import com.attendence.rural.Repositor.Role_Repo;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing

public class Sih12Application {

	public static void main(String[] args) {
		SpringApplication.run(Sih12Application.class, args);
	}

	@Bean
		CommandLineRunner initRoles(Role_Repo roleRepository) {
   			 return args -> {
       			 if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
           			 roleRepository.save(new Role(null, "ROLE_ADMIN"));
           				 roleRepository.save(new Role(null, "ROLE_TEACHER"));
            				roleRepository.save(new Role(null, "ROLE_STUDENT"));
       				 }
   			 };
	}


}



