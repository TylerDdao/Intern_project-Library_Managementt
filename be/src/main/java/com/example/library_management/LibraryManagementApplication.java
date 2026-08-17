package com.example.library_management;

import com.example.library_management.util.HashGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication ()
@EnableScheduling
public class LibraryManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementApplication.class, args);
		System.out.println("Hello, World");
	}


}
