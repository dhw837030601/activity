package com.aiwue;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@Configuration
@ComponentScan
@SpringBootApplication
@MapperScan("com.aiwue.activiti.mapper")
@EnableEurekaClient
@EnableFeignClients
@EnableHystrix
@EnableHystrixDashboard
@EnableAutoConfiguration(exclude = {org.activiti.spring.boot.SecurityAutoConfiguration.class})
public class AiwueActivitiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiwueActivitiServerApplication.class, args);
	}

}


