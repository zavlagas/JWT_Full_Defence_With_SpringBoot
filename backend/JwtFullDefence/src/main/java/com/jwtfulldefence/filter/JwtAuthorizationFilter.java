package com.jwtfulldefence.filter;

import com.jwtfulldefence.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.jwtfulldefence.constant.SecurityConstant.*;


@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {


    JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (isOptionsTypeRequestMethod(request)) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (isAuthorizationHeaderInvalid(authorizationHeader)) {
                filterChain.doFilter(request, response);
                return;
            }
            isAuthorizationHeaderValid(authorizationHeader, request);
        }
        filterChain.doFilter(request, response);

    }

    private boolean isAuthorizationHeaderInvalid(String authorizationHeader) {
        /*TODO change for jwt activation*/
        return (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX));

    }

    private void isAuthorizationHeaderValid(String authorizationHeader, HttpServletRequest request) {

        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        String username = jwtTokenProvider.getSubject(token);

        if (jwtTokenProvider.isTokenValid(username, token) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
            Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }
    }


    private boolean isOptionsTypeRequestMethod(HttpServletRequest request) {
        return (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD));
    }

}
