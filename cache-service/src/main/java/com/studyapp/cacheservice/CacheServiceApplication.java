package com.studyapp.cacheservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CacheServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheServiceApplication.class, args);
	}

}
