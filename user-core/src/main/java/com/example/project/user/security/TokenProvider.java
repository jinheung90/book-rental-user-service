package com.example.project.user.security;





import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.user.dao.ParsedJwtInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "Authorities";

    @Value("${token.access-token-secret}")
    private String secret;
    @Value("${token.issuer}")
    private String issuer;
    @Value("${token.access-token-expired}")
    private Long mAccessTokenExpiration;

    private Key key;

    @PostConstruct
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createJwtAccessTokenByUser(List<String> authorityNames, Long userId) {
        Date validity =  new Date(new Date().getTime() + mAccessTokenExpiration * 1000);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer(issuer)
                .claim(AUTHORITIES_KEY, authorityNames)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    public ParsedJwtInfo getUserIdAndAuthorityByJwtAccessToken(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        return new ParsedJwtInfo(Long.valueOf(claims.getSubject()), new ArrayList<>(authorities), claims.getExpiration());
    }
}
