package hackathon.diary.global.jwt.util;


import hackathon.diary.global.jwt.dto.response.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.access.expiration}")
    private Long accessTokenExpireTime;

    @Value("${spring.jwt.refresh.expiration}")
    private Long refreshTokenExpireTime;

    @Value("${spring.jwt.secretKey}")
    private String secret;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] key = Decoders.BASE64URL.decode(secret);
        this.key = Keys.hmacShaKeyFor(key);
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (UnsupportedJwtException | MalformedJwtException exception) {
            log.error("JWT is not valid");
            throw new JwtException("유효하지 않은 토큰");
        } catch (SignatureException exception) {
            log.error("JWT signature validation fails");
            throw new JwtException("시그니처가 유효하지 않은 토큰");
        } catch (ExpiredJwtException exception) {
            log.error("JWT expired");
            throw new JwtException("만료된 토큰");
        } catch (IllegalArgumentException exception) {
            log.error("JWT is null or empty or only whitespace");
            throw new JwtException("값이 들어있지 않은 토큰");
        } catch (Exception exception) {
            log.error("JWT validation fails", exception);
        }
        return false;
    }
    public TokenDto generate(String email) {
        String accessToken = generateAccessToken(email);
        String refreshToken = generateRefreshToken();
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    public String generateAccessToken(String email) {
        Date date = new Date();
        Date accessExpiryDate = new Date(date.getTime() + accessTokenExpireTime);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(date)
                .setExpiration(accessExpiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken() {
        Date date = new Date();
        Date refreshExpiryDate = new Date(date.getTime() + refreshTokenExpireTime);

        return Jwts.builder()
                .setExpiration(refreshExpiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        return new UsernamePasswordAuthenticationToken(email, "", Collections.emptyList());
    }
}
