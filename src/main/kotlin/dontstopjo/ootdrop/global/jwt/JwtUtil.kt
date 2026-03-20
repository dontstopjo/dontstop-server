package dontstopjo.ootdrop.global.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
 *
 * @property jwtProperties JWT 설정 정보
 */
@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtUtil(
    private val jwtProperties: JwtProperties
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    /**
     * Access Token 생성
     *
     * @param userId 사용자 ID
     * @param providerId 유니크키
     * @param role 사용자 권한
     * @return 생성된 Access Token
     */
    fun generateAccessToken(userId: Long, providerId: String, role: String): String {
        return generateToken(userId, providerId, role, jwtProperties.accessTokenValidity)
    }

    /**
     * Refresh Token 생성
     *
     * @param userId 사용자 ID
     * @param providerId 유니크키
     * @param role 사용자 권한
     * @return 생성된 Refresh Token
     */
    fun generateRefreshToken(userId: Long, providerId: String, role: String): String {
        return generateToken(userId, providerId, role, jwtProperties.refreshTokenValidity)
    }

    /**
     * JWT 토큰 생성 내부 메서드
     *
     * @param userId 사용자 ID
     * @param providerId 사용자 이메일
     * @param role 사용자 권한
     * @param validityInMilliseconds 토큰 유효 시간 (밀리초)
     * @return 생성된 JWT 토큰
     */
    private fun generateToken(userId: Long, providerId: String, role: String, validityInMilliseconds: Long): String {
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("providerId", providerId)
            .claim("role", role)
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey)
            .compact()
    }

    /**
     * JWT 토큰에서 Claims 추출
     *
     * @param token JWT 토큰
     * @return 토큰의 Claims
     */
    fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    fun getUserId(token: String): Long {
        return getClaims(token).subject.toLong()
    }

    /**
     * JWT 토큰에서 providerId 추출
     *
     * @param token JWT 토큰
     * @return 사용자 providerId
     */
    fun getProviderId(token: String): String {
        return getClaims(token).get("providerId", String::class.java)
    }

    /**
     * JWT 토큰에서 권한 추출
     *
     * @param token JWT 토큰
     * @return 사용자 권한
     */
    fun getRole(token: String): String {
        return getClaims(token).get("role", String::class.java)
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * @param token JWT 토큰
     * @return 유효한 토큰이면 true, 그렇지 않으면 false
     */
    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}
