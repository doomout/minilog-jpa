package com.asdf.minilog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil implements Serializable {
    // Serializable을 구현했을 때 사용하는 클래스 버전 번호
    // JWT 기능 자체와는 거의 관계 없음
    private static final long serialVersionUID = -2550185165626007488L;

    // JWT 유효시간
    // 5시간 = 5 * 60 * 60초
    public static final long JWT_VALIDITY = 5 * 60 * 60;

    // application.properties의 jwt.secret=${JWT_SECRET} 값을 가져옴
    @Value("${jwt.secret}")
    private String secret;

    // JWT에서 username 꺼내기
    public String getUsernameFromToken(String token) {
        // JWT의 subject(sub)를 가져옴
        // generateToken()에서 username을 subject에 넣었기 때문에
        // 여기서 다시 username을 꺼낼 수 있음
        return getClaimFromToken(token, Claims::getSubject);
    }

    // JWT에서 userId 꺼내기
    public Long getUserIdFromToken(String token) {
        String jwt;

        // HTTP Authorization 헤더가 보통:
        //
        // Authorization: Bearer eyJhbGci...
        //
        // 형태로 들어오기 때문에
        // "Bearer " 부분을 제거하고 JWT 본문만 가져옴
        if (token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        } else {
            jwt = token;
        }

        // JWT Claims에서 userId라는 사용자 정의 값을 꺼냄
        return getClaimFromToken(jwt, claims -> claims.get("userId", Long.class));
    }

    // JWT 만료시간 꺼내기
    public Date getExpirationDateFromToken(String token) {
        // Claims의 expiration(exp) 값을 반환
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // JWT에서 원하는 Claim 하나를 꺼내는 공통 메서드
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        // 먼저 JWT 전체 Claims를 가져옴
        Claims claims = getAllClaimsFromToken(token);

        // 호출자가 원하는 값을 Claims에서 꺼냄
        return claimsResolver.apply(claims);
    }

    // JWT 전체 내용(Claims)을 파싱
    private Claims getAllClaimsFromToken(String token) {
        // .env에서 받은 Base64 비밀키를 디코딩하여 JWT 서명 검증에 사용할 Key 객체 생성
        Key signingKey =
                new SecretKeySpec(
                        Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());

        // JWT를 읽고 서명을 검증한 뒤 JWT 안의 Claims(payload)를 반환
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT가 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        // JWT의 만료시간을 가져옴
        Date expiration = getExpirationDateFromToken(token);

        // 만료시간이 현재시간보다 과거라면 이미 만료된 토큰
        return expiration.before(new Date());
    }

    // JWT 생성
    public String generateToken(UserDetails userDetails, Long userId) {
        // JWT Payload에 넣을 사용자 정의 데이터
        Map<String, Object> claims = new HashMap<>();

        // userId를 JWT 안에 추가
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims) // 사용자 정의 Claims 추가
                .setSubject(userDetails.getUsername()) // JWT subject(sub)에 username 저장
                .setIssuedAt(new Date(System.currentTimeMillis())) // JWT 발급시간(iat)
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + JWT_VALIDITY * 1000)) // JWT 만료시간(exp) 현재시간 + 5시간
                .signWith(
                        new SecretKeySpec( // 비밀키와 HS256 알고리즘으로 JWT에 서명
                                Base64.getDecoder().decode(secret),
                                SignatureAlgorithm.HS256.getJcaName()))
                .compact(); // 최종 JWT 문자열 생성
    }

    // JWT가 현재 사용자에게 유효한 토큰인지 검증
    public Boolean validateToken(String token, UserDetails userDetails) {
        // JWT에 들어 있는 username 조회
        String username = getUsernameFromToken(token);

        // 1. JWT username == 현재 사용자 username
        // 2. JWT가 만료되지 않았음
        //
        // 둘 다 참이면 유효한 토큰
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
