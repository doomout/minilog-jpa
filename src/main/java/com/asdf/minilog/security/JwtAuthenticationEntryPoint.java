package com.asdf.minilog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/*
 * 인증되지 않은 사용자가 보호된 API에 접근했을 때 호출되는 클래스.
 *
 * 예:
 * - JWT 토큰이 없음
 * - 인증 정보가 없음
 * - 인증되지 않은 상태에서 로그인 필요 API에 접근
 *
 * Spring Security가 "이 사용자는 인증되지 않았다"고 판단하면
 * AuthenticationEntryPoint의 commence()를 호출한다.
 *
 * 여기서는 사용자에게
 * HTTP 401 Unauthorized + JSON 오류 메시지를 반환한다.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /*
     * 인증되지 않은 사용자의 요청을 처리하는 메서드.
     *
     * Spring Security가 인증 실패를 감지하면 자동으로 호출한다.
     *
     * request       : 사용자가 보낸 HTTP 요청
     * response      : 사용자에게 보낼 HTTP 응답
     * authException : 인증 과정에서 발생한 예외 정보
     */
    @Override
    public void commence(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        // HTTP 상태 코드를 401 Unauthorized로 설정한다.
        // 401은 "이 요청을 처리하려면 인증이 필요합니다"라는 의미이다.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 응답 데이터가 JSON 형식임을 클라이언트에게 알려준다.
        response.setContentType("application/json");

        // 클라이언트에게 전달할 JSON 데이터를 만들기 위한 Map
        Map<String, String> responseBody = new HashMap<>();

        // 최종적으로 {"message":"Unauthorized"} 형태가 된다.
        responseBody.put("message", "Unauthorized");

        // Java 객체를 JSON 문자열로 변환하기 위해 Jackson의 ObjectMapper를 사용한다.
        ObjectMapper objectMapper = new ObjectMapper();
        // Map 객체를 JSON 문자열로 변환한다.
        // {"message":"Unauthorized"}
        String jsonResponse = objectMapper.writeValueAsString(responseBody);

        // 만들어진 JSON을 HTTP 응답 본문(body)에 기록한다.
        response.getWriter().write(jsonResponse);
    }
}
