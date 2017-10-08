package com.github.jamorin.nzbgateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.ForwardedHeaderFilter;

@EnableZuulProxy
@SpringBootApplication
@RestController
public class NzbGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(NzbGatewayApplication.class, args);
	}

    @GetMapping("/ping")
    public String basic() {
        return "pong";
    }

    @Bean
    public FilterRegistrationBean forwardedHeaderFilter(@Value("${gateway.use-forwarded-filter}") boolean useFilter) {
        FilterRegistrationBean bean = new FilterRegistrationBean(new ForwardedHeaderFilter());
        bean.setEnabled(useFilter);
        bean.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 20);
        return bean;
    }

}
