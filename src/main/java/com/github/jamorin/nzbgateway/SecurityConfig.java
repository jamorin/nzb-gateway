package com.github.jamorin.nzbgateway;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableOAuth2Sso
@RequiredArgsConstructor
@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        SecurityProperties.User user = securityProperties.getUser();
        auth.inMemoryAuthentication()
            .withUser(user.getName())
            .password(user.getPassword())
            .roles(user.getRole().toArray(new String[0]));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/application/health", "/sonarr/api/**", "/radarr/api/**").permitAll()
            .antMatchers("/ping", "/nzbget/**", "/xmlrpc", "/api/**").hasRole("USER")
            .anyRequest().hasRole("ACTUATOR")
            .and()
            .httpBasic().realmName("NzbGateway")
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
    }

    @Bean
    public AuthoritiesExtractor authoritiesExtractor(@Value("${gateway.auth0-roles-namespace}") String namespace) {
        return profile -> {
            Object authorities = profile.get(namespace);
            if (authorities != null && authorities instanceof Collection) {
                Set<String> roles = new HashSet<>();
                for (Object role : (List) authorities) {
                    if (role instanceof String) {
                        roles.add((String) role);
                    }
                }
                return AuthorityUtils.createAuthorityList(roles.toArray(new String[0]));
            }
            return AuthorityUtils.NO_AUTHORITIES;
        };
    }

    @Bean
    public PrincipalExtractor principalExtractor() {
        return profile -> profile.get("email");
    }

}
