package dontstopjo.ootdrop.global.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
 *
 * @property jwtProperties JWT 설정 정보
 */
@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtUtil(
    private val jwtProperties: JwtProperties,
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${spring.datasource.name}") private val dbName: String
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun getRefreshTokenValidity(): Long = jwtProperties.refreshTokenValidity

    /**
     * Access Token 생성
     *
     * @param userId 사용자 ID
     * @param id 유니크키
     * @param role 사용자 권한
     * @return 생성된 Access Token
     */
    fun generateAccessToken(name: String, id: String, role: String): String {
        return generateToken(name, id, role, jwtProperties.accessTokenValidity)
    }

    /**
     * Refresh Token 생성
     *
     * @param userId 사용자 ID
     * @param id 유니크키
     * @param role 사용자 권한
     * @return 생성된 Refresh Token
     */
    fun generateRefreshToken(name: String, id: String, role: String): String {
        val refreshToken = generateToken(name, id, role, jwtProperties.refreshTokenValidity)
        val key = "${dbName}:refreshToken:${id}"
        redisTemplate.opsForValue().set(key, refreshToken, jwtProperties.refreshTokenValidity, TimeUnit.MILLISECONDS)
        return refreshToken
    }

    /**
     * JWT 토큰 생성 내부 메서드
     *
     * @param name 사용자 이름
     * @param id 사용자 이메일
     * @param validityInMilliseconds 토큰 유효 시간 (밀리초)
     * @return 생성된 JWT 토큰
     */
    private fun generateToken(name: String, id: String, role: String, validityInMilliseconds: Long): String {
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .subject(id)
            .claim("name", name)
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
     * JWT 토큰에서 id 추출
     *
     * @param token JWT 토큰
     * @return 사용자 id
     */
    fun getId(token: String): String {
        return getClaims(token).subject
    }

    fun getName(token: String): String {
        val claims = getClaims(token)
        // "name"이라는 키로 저장된 값을 꺼내옴
        return claims["name"]?.toString() ?: ""
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
            // 토큰 만료 여부 확인
            if (claims.expiration.before(Date())) {
                return false
            }
            // 블랙리스트에 있는지 확인
            val blacklistKey = "${dbName}:blacklist:${token}"
            redisTemplate.hasKey(blacklistKey) == false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Redis에 저장된 리프레시 토큰을 가져옵니다.
     *
     * @param id 사용자 ID
     * @return Redis에 저장된 리프레시 토큰 또는 null
     */
    fun getRefreshToken(id: String): String? {
        val key = "${dbName}:refreshToken:${id}"
        return redisTemplate.opsForValue().get(key)
    }

    /**
     * Redis에서 리프레시 토큰을 삭제합니다.
     *
     * @param id 사용자 ID
     */
    fun deleteRefreshToken(id: String) {
        val key = "${dbName}:refreshToken:${id}"
        redisTemplate.delete(key)
    }

    /**
     * Access Token을 블랙리스트에 추가합니다.
     *
     * @param accessToken 블랙리스트에 추가할 Access Token
     */
    fun addAccessTokenToBlacklist(accessToken: String) {
        val claims = getClaims(accessToken)
        val expiration = claims.expiration.time - Date().time // 남은 유효 시간
        if (expiration > 0) {
            val blacklistKey = "${dbName}:blacklist:${accessToken}"
            redisTemplate.opsForValue().set(blacklistKey, "logout", expiration, TimeUnit.MILLISECONDS)
        }
    }
}
