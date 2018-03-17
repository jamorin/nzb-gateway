package com.github.jamorin.nzbgateway;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableZuulProxy
@SpringBootApplication
@RestController
@EnableHystrix
@EnableHystrixDashboard
public class NzbGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(NzbGatewayApplication.class, args);
	}

	@HystrixCommand(groupKey = "ping")
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
