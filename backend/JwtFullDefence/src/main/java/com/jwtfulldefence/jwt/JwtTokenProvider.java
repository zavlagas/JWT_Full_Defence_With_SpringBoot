package com.jwtfulldefence.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.jwtfulldefence.security.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jwtfulldefence.constant.SecurityConstant.*;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;


    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT
                .create()
                .withIssuer(COMPANY_ISSUER)
                .withAudience(COMPANY_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(
                        new Date(System.currentTimeMillis()
                                + ACCESS_TOKEN_EXPIRATION_TIME))
                /*
                TODO : Change the algorithm HMAC512 because  is not secured
                 */
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }


    public List<GrantedAuthority> getAuthorities(String token) {

        String[] claims = getClaimFromToken(token);

        return Stream.of(claims)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Authentication getAuthentication(
            String username,
            List<GrantedAuthority> authorities,
            HttpServletRequest request) {


        UsernamePasswordAuthenticationToken userPasswordAuthToken =
                new UsernamePasswordAuthenticationToken(username,
                        null,
                        authorities);

        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());

    }


    private String[] getClaimFromToken(String token) {

        JWTVerifier verifier = getJWTVerifier();

        return verifier
                .verify(token)
                .getClaim(AUTHORITIES)
                .asArray(String.class);

    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {

            /*
            TODO : Change the algorithm HMAC512 because is not secured
             */
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT
                    .require(algorithm)
                    .withIssuer(COMPANY_ISSUER).build();

        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {

        List<String> authorities = userPrincipal
                .getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList());

        return (authorities.toArray(new String[0]));
    }
}
