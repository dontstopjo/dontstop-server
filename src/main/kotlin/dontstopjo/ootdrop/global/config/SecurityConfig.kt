package dontstopjo.ootdrop.global.config

import dontstopjo.ootdrop.global.jwt.JwtAuthenticationFilter
import dontstopjo.ootdrop.global.oauth.CustomOAuth2UserService
import dontstopjo.ootdrop.global.oauth.OAuth2AuthenticationFailureHandler
import dontstopjo.ootdrop.global.oauth.OAuth2AuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Spring Security 설정 클래스
 * OAuth2 로그인과 JWT 기반 인증을 구성
 *
 * @property customOAuth2UserService OAuth2 사용자 서비스
 * @property oAuth2AuthenticationSuccessHandler OAuth2 인증 성공 핸들러
 * @property oAuth2AuthenticationFailureHandler OAuth2 인증 실패 핸들러
 * @property jwtAuthenticationFilter JWT 인증 필터
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    /**
     * Spring Security 필터 체인 설정
     * - CSRF 비활성화 (JWT 사용으로 불필요)
     * - JWT 인증 필터 추가
     * - OAuth2 로그인 설정
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // PermitAll for specific paths
                    .requestMatchers(HttpMethod.GET, "/posts").permitAll() // 전체 게시글 조회 API
                    .requestMatchers("/oauth2/success", "/oauth2/failure", "/oauth2/authorization/kakao").permitAll() // OAuth2 콜백 및 시작 엔드포인트
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI
                    .requestMatchers("/swagger-ui", "/v3/api-docs").permitAll() // Swagger UI

                    // Authenticated for specific paths
                    .requestMatchers(HttpMethod.GET, "/posts/{id}").authenticated() // 단일 게시글 조회 API
                    .requestMatchers(HttpMethod.GET, "/me").authenticated() // 내 정보 조회
                    .requestMatchers(HttpMethod.POST, "/me/update").authenticated() // 내 정보 업데이트
                    .requestMatchers(HttpMethod.POST, "/oauth2/logout").authenticated() // 로그아웃
                    .requestMatchers(HttpMethod.POST, "/posts").authenticated() // 게시글 생성
                    .requestMatchers(HttpMethod.PUT, "/posts/{id}").authenticated() // 게시글 수정
                    .requestMatchers(HttpMethod.DELETE, "/posts/{id}").authenticated() // 게시글 삭제
                    .requestMatchers("/comment/**").authenticated() // 댓글 관련 모든 작업
                    .requestMatchers("/mypage/**").authenticated() // 마이페이지 관련 모든 작업

                    // PermitAll for all other paths
                    .anyRequest().permitAll()
            }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler)
            }

        return http.build()
    }

    /**
     * 비밀번호 암호화를 위한 BCrypt 인코더 빈 등록
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
