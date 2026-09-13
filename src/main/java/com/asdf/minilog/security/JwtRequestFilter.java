package com.asdf.minilog.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter { // 요청 헤더에 포함된 JWT를 검증, 인증 하는 클래스 - HTTP 요청(Request) 하나당 한 번(Once) 실행되는 필터
    // 로그를 출력하기 위한 Logger
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    // username을 이용하여 DB에서 사용자 정보를 조회하는 서비스
    @Autowired
    private UserDetailsService userDetailsService;

    // JWT 생성,조회,검증 도구
    @Autowired
    private JwtUtil jwtTokenUtil;

    // HTTP 요청이 들어올 때마다 실행되는 필터
    @Override
    protected void doFilterInternal (
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            jakarta.servlet.FilterChain filterChain
    ) throws jakarta.servlet.ServletException, IOException {
        // HTTP 요청 Header에서 Authorization 값을 가져옴
        String requestTokenHeader = request.getHeader("Authorization");

        // JWT에서 꺼낼 사용자 이름
        String username = null;

        // "Bearer "를 제거한 순수 JWT 문자열
        String jwt = null;

        // Authorization 헤더가 존재하고 "Bearer "로 시작하는지 검사
        if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // "Bearer "는 7글자이므로 제거
            jwt = requestTokenHeader.substring(7);

            try {
                // JWT의 subject에서 username을 꺼냄
                username = jwtTokenUtil.getUsernameFromToken(jwt);
            } catch (IllegalArgumentException e) {
                // JWT 자체를 제대로 읽을 수 없는 경우
                logger.error("Unable to get JWT", e);
            } catch (ExpiredJwtException e) {
                // JWT의 만료시간(exp)이 지난 경우
                logger.warn("JWT has expired", e);
            }
        } else {
            // Authorization 헤더가 없거나 "Bearer " 형식이 아닌 경우
            logger.warn("JWT does not begin with Bearer String");
        }

        // JWT에서 username을 정상적으로 얻었고
        // 현재 SecurityContext에 아직 인증 정보가 없다면 인증 작업을 수행
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // username을 이용하여 사용자 정보를 조회
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // JWT가 실제 사용자 정보와 일치하고 만료되지 않았는지 검사
            if(jwtTokenUtil.validateToken(jwt, userDetails)){
                // ★ 인증 객체 생성
                // userDetails → 인증된 사용자 정보
                // null → 비밀번호(credentials) -> 이미 JWT로 인증하므로 비밀번호 필요 없음
                // userDetails.getAuthorities() → ROLE_ADMIN, ROLE_AUTHOR 등의 권한
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 현재 HTTP 요청의 추가 정보를 인증 객체에 저장(예: IP, 세션 등의 웹 요청 정보)
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ★★★★★ 핵심 ★★★★★
                // 검증된 사용자 인증 정보를 Spring Security의 SecurityContext에 등록
                //
                // 여기까지 와야 Spring Security가 "이 요청은 로그인된 사용자의 요청이구나" 라고 인식함
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                // JWT 검증 실패
                logger.warn("JWT is not valid");
            }
        }

        // 현재 필터의 작업이 끝났으므로 다음 필터로 요청을 전달
        // 이걸 호출하지 않으면 요청이 여기서 멈춰버림
        filterChain.doFilter(request, response);
    }
}
