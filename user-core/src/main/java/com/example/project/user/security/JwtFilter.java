package com.example.project.user.security;

import com.example.project.user.dao.ParsedJwtInfo;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;


import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);

        if(StringUtils.hasText(jwt)) {
            try {
                ParsedJwtInfo parsedJwtInfo = tokenProvider.getUserIdAndAuthorityByJwtAccessToken(jwt);
                setAuthentication(parsedJwtInfo, jwt);
            } catch (ExpiredJwtException e) {
                HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                httpServletResponse.setHeader("jwt-Expired", "true");
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void setAuthentication(ParsedJwtInfo userIdAndAuthorities, String jwt) {
        CustomUserDetail principal = new CustomUserDetail(
                userIdAndAuthorities.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getAuthority())).toList(),
                "",
                "",
                userIdAndAuthorities.getUserId());
        Authentication authentication =  new UsernamePasswordAuthenticationToken(principal, jwt, userIdAndAuthorities.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
