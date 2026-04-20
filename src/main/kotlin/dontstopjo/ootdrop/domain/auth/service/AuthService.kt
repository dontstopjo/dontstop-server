package dontstopjo.ootdrop.domain.auth.service

import dontstopjo.ootdrop.global.exception.InvalidTokenException
import dontstopjo.ootdrop.global.jwt.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtUtil: JwtUtil
) {
    fun logout(request: HttpServletRequest) {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val accessToken = authorizationHeader.substring(7)
            try {
                val userId = jwtUtil.getId(accessToken)

                jwtUtil.addAccessTokenToBlacklist(accessToken)
                jwtUtil.deleteRefreshToken(userId)
            } catch (e: Exception) {
                throw InvalidTokenException()
            }
        } else {
            throw InvalidTokenException()
        }
    }
}
