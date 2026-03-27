package dontstopjo.ootdrop.global.config

import dontstopjo.ootdrop.global.jwt.JwtAuthenticationFilter
import dontstopjo.ootdrop.global.oauth.CustomOAuth2UserService
import dontstopjo.ootdrop.global.oauth.OAuth2AuthenticationFailureHandler
import dontstopjo.ootdrop.global.oauth.OAuth2AuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
                    .requestMatchers(
                        "/posts/**",
                        "/mypage",
                        "/mypage/**",
                        "/comment/**"
                    ).authenticated()
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
