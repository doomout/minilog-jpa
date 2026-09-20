package com.asdf.minilog.config;

import com.asdf.minilog.security.JwtAuthenticationEntryPoint;
import com.asdf.minilog.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 이 클래스가 Spring의 설정 클래스임을 나타낸다.
// 내부의 @Bean 메서드가 반환하는 객체를 Spring 컨테이너에 등록한다.
@Configuration

// Spring Security의 웹 보안 기능을 활성화한다.
// 현재 Spring Boot에서는 보안 자동 설정도 제공하므로
// 반드시 직접 선언해야 하는 어노테이션은 아니다.
@EnableWebSecurity

// @PreAuthorize, @PostAuthorize 등을 이용한
// 메서드 단위의 접근 권한 검사를 활성화한다.
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{
    // 인증되지 않은 사용자가 보호된 API에 접근했을 때
    // HTTP 401 오류 응답을 생성하는 클래스
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // HTTP 요청의 JWT를 추출하고 검증하여
    // Spring Security에 사용자 인증 정보를 등록하는 필터
    private JwtRequestFilter jwtRequestFilter;

    /*
     * 생성자 주입을 이용하여 필요한 보안 객체를 전달받는다.
     *
     * Spring은 이미 등록된 JwtAuthenticationEntryPoint와
     * JwtRequestFilter 객체를 찾아 생성자에 전달한다.
     *
     * 생성자가 하나뿐이면 @Autowired를 생략해도 된다.
     */
    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtRequestFilter jwtRequestFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /*
     * 사용자 비밀번호를 암호화하는 PasswordEncoder를 등록한다.
     *
     * BCrypt는 비밀번호를 단방향 해시로 변환하는 알고리즘이다.
     *
     * 예:
     * 사용자가 입력한 비밀번호: password123
     * DB에 저장되는 값: $2a$10$...
     *
     * BCrypt는 같은 비밀번호를 암호화해도
     * 솔트 때문에 매번 다른 해시값이 생성될 수 있다.
     *
     * 로그인할 때는 matches()를 이용하여
     * 입력한 비밀번호와 저장된 해시값이 일치하는지 검증한다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * 사용자 인증을 처리하는 AuthenticationManager를
     * Spring Bean으로 등록한다.
     *
     * AuthenticationManager는 로그인 과정에서
     * 전달받은 인증 정보를 검증하는 역할을 한다.
     *
     * 일반적인 사용자명/비밀번호 인증에서는
     * UserDetailsService와 PasswordEncoder 등을 이용하여
     * 사용자 정보와 비밀번호를 검증할 수 있다.
     *
     * JWT를 생성하는 로그인 API에서
     * AuthenticationManager를 사용할 수 있다.
     */
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        // Spring Security가 구성한 AuthenticationManager를 반환한다.
        return configuration.getAuthenticationManager();
    }

    /*
     * HTTP 요청에 적용할 보안 정책과 필터를 설정한다.
     *
     * SecurityFilterChain은 클라이언트의 HTTP 요청이
     * Controller에 도달하기 전에 실행되는 보안 필터들의 체인이다.
     *
     * 이 메서드에서 인증, 인가, 세션 관리,
     * 예외 처리 등의 정책을 설정한다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                /*
                 * CSRF 보호 기능을 비활성화한다.
                 *
                 * CSRF는 사용자가 로그인한 상태를 악용하여
                 * 의도하지 않은 요청을 보내도록 유도하는 공격이다.
                 *
                 * 이 프로젝트는 JWT를 이용한 Stateless API를
                 * 구현하고 있으므로 CSRF 보호를 비활성화한다.
                 *
                 * 단, JWT를 쿠키로 자동 전송하는 구조라면
                 * CSRF 보호가 다시 필요할 수 있다.
                 */
                .csrf(AbstractHttpConfigurer::disable)
                /*
                 * HTTP 요청별 접근 권한을 설정한다.
                 *
                 * requestMatchers()로 요청 경로와 HTTP 메서드를 지정하고
                 * permitAll(), hasRole(), authenticated() 등을 이용해
                 * 해당 요청의 접근 허용 조건을 설정한다.
                 *
                 * 위에서부터 순서대로 검사하므로
                 * 구체적인 규칙을 먼저 작성하는 것이 중요하다.
                 */
                .authorizeHttpRequests(
                (requests) -> requests

                        /*
                         * 로그인 API와 Swagger 관련 API는
                         * 인증되지 않은 사용자도 접근할 수 있다.
                         *
                         * permitAll():
                         * 로그인 여부와 관계없이 접근 허용
                         *
                         * 로그인 API는 JWT를 발급받기 전에
                         * 호출해야 하므로 인증 없이 접근할 수 있어야 한다.
                         */
                        .requestMatchers("/api/v2/auth/login","/swagger-ui/**","/v3/api-docs/**")
                        .permitAll()

                        /*
                         * POST /api/v2/user/{userId}
                         *
                         * 지정한 경로의 POST 요청을
                         * 로그인 여부와 관계없이 허용한다.
                         *
                         * 주의:
                         * 실제 회원가입 API가 이 경로를 사용하는지
                         * UserController에서 확인해야 한다.
                         */
                        .requestMatchers(HttpMethod.POST,"/api/v2/user/{userId}")
                        .permitAll()

                        /*
                         * GET /api/v2/user/{userId}
                         *
                         * 특정 사용자의 정보를 조회하는 API에
                         * 인증 없이 접근할 수 있도록 허용한다.
                         *
                         * 단, 사용자 정보 중 비밀번호나 개인정보가
                         * 외부에 노출되지 않도록 주의해야 한다.
                         */
                        .requestMatchers(HttpMethod.GET, "/api/v2/user/{userId}")
                        .permitAll()

                        /*
                         * DELETE /api/v2/user/{userId}
                         *
                         * 특정 사용자를 삭제하는 API는
                         * ADMIN 역할을 가진 사용자만 접근할 수 있다.
                         *
                         * hasRole("ADMIN")은 내부적으로
                         * ROLE_ADMIN 권한이 있는지 검사한다.
                         *
                         * 따라서 Role enum에 정의했던
                         * ROLE_ADMIN과 연결된다.
                         */
                        .requestMatchers(HttpMethod.DELETE, "/api/v2/user/{userId}")
                        .hasRole("ADMIN")

                        /*
                         * 위에서 별도로 허용하지 않은 모든 요청은
                         * 인증된 사용자만 접근할 수 있다.
                         *
                         * authenticated():
                         * 정상적으로 인증된 사용자라면 접근 허용
                         *
                         * 단, 이 설정만으로 모든 사용자의
                         * 데이터 접근 권한까지 검증되는 것은 아니다.
                         */
                        .anyRequest()
                        .authenticated())

                /*
                 * 인증 과정에서 예외가 발생했을 때의 처리 방법을 설정한다.
                 *
                 * 인증되지 않은 사용자가 보호된 API에 접근하면
                 * JwtAuthenticationEntryPoint의 commence()가 호출되어
                 * HTTP 401 응답을 생성한다.
                 */
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                /*
                 * 서버의 HTTP 세션 생성 정책을 설정한다.
                 *
                 * STATELESS:
                 * Spring Security가 사용자 인증 상태를 유지하기 위한
                 * HTTP 세션을 생성하거나 사용하지 않도록 설정한다.
                 *
                 * 따라서 클라이언트는 보호된 API를 요청할 때마다
                 * JWT를 전달해야 한다.
                 *
                 * JwtRequestFilter는 요청마다 JWT를 검증하고
                 * 현재 요청의 인증 정보를 SecurityContext에 등록한다.
                 */
                .sessionManagement(
                        sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        /*
         * JWT 인증 필터를 Spring Security 필터 체인에 추가한다.
         *
         * addFilterBefore(A, B):
         * B 필터보다 A 필터가 먼저 실행되도록 등록한다.
         *
         * 여기서는 UsernamePasswordAuthenticationFilter보다
         * JwtRequestFilter가 먼저 실행되도록 설정한다.
         *
         * 따라서 JWT를 이용한 사용자 인증 정보를
         * 후속 보안 처리 단계에서 사용할 수 있다.
         */
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);


        /*
         * 지금까지 설정한 보안 정책과 필터를 기반으로
         * SecurityFilterChain 객체를 생성하여 반환한다.
         *
         * Spring Security는 이 객체를 이용하여
         * HTTP 요청에 보안 정책을 적용한다.
         */
        return httpSecurity.build();
    }
}
