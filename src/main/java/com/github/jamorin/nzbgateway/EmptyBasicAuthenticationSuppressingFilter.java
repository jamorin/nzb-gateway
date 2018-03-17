package com.github.jamorin.nzbgateway;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * NZB360 will always send an empty Basic Authentication header.
 * Spring Security tries to process this header so let's ignore it for requests with the X-Api-Key header
 */
public class EmptyBasicAuthenticationSuppressingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("X-Api-Key");

        if (!StringUtils.hasText(header)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest wrappedRequest = new BasicAuthenticationHeaderRemovingRequest(request);

        chain.doFilter(wrappedRequest, response);
    }

    private static class BasicAuthenticationHeaderRemovingRequest extends HttpServletRequestWrapper {
        public BasicAuthenticationHeaderRemovingRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            if (HttpHeaders.AUTHORIZATION.equals(name)) {
                return null;
            }
            return name;
        }
    }
}
