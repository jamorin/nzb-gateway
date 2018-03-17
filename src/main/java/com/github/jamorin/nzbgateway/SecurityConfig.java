package com.github.jamorin.nzbgateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
            .addFilterBefore(new EmptyBasicAuthenticationSuppressingFilter(), BasicAuthenticationFilter.class)
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/actuator/health").permitAll()
            .requestMatchers(new AndRequestMatcher(
                new RequestHeaderRequestMatcher("X-Api-Key"),
                new OrRequestMatcher(
                    new AntPathRequestMatcher("/sonarr/api/**"),
                    new AntPathRequestMatcher("/radarr/api/**")
                )
            )).permitAll()
            .antMatchers("/actuator/hystrix.stream").hasIpAddress("127.0.0.1")
            .antMatchers("/actuator/**").hasRole("ACTUATOR")
            .anyRequest().hasRole("USER")
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
