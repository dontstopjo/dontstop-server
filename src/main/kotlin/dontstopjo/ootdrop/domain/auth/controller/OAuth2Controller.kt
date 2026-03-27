package dontstopjo.ootdrop.domain.auth.controller

import dontstopjo.ootdrop.domain.auth.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth2")
@Tag(name = "AUTH", description = "OAuth2 콜백 API \n요청은 /oauth2/authorization/kakao")
class OAuth2Controller(
    private val authService: AuthService
) {

    @GetMapping("/success")
    @Operation(
        summary = "OAuth2 로그인 성공 콜백",
        description = "OAuth2 로그인 성공 후 결과 정보를 반환합니다."
    )
    fun loginSuccess(
        @Parameter(description = "백앤드 로직중 하나 프론트에서 건들거 없어요")
        @RequestParam accessToken: String,
        @Parameter(description = "백앤드 로직중 하나 프론트에서 건들거 없어요")
        @RequestParam refreshToken: String
    ): Map<String, Any> {
        return mapOf(
            "success" to true,
            "message" to "로그인 성공",
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )
    }

    @GetMapping("/failure")
    @Operation(
        summary = "OAuth2 로그인 실패 콜백",
        description = "OAuth2 로그인 실패 시 오류 정보를 반환합니다."
    )
    fun loginFailure(
        @Parameter(description = "OAuth2 오류 사유", example = "access_denied")
        @RequestParam error: String
    ): Map<String, Any> {
        return mapOf(
            "success" to false,
            "message" to "로그인 실패",
            "error" to error
        )
    }

    @PostMapping("/logout")
    @Operation(
        summary = "로그아웃",
        description = "현재 사용자의 액세스 토큰을 무효화하고 리프레시 토큰을 삭제합니다."
    )
    fun logout(request: HttpServletRequest): ResponseEntity<Void> {
        authService.logout(request)
        return ResponseEntity.ok().build()
    }
}
