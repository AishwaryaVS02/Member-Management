package com.surest.member_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MemberManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemberManagementApplication.class, args);
	}

}
