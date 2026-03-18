package dontstopjo.ootdrop.domain.test

import dontstopjo.ootdrop.domain.user.repository.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
@Tag(name = "test", description = "로그인 테스트 api")
class TestController(
    private val userRepository: UserRepository
) {

    @GetMapping("/me")
    @Operation(
        summary = "인증 사용자 정보 조회 테스트",
        description = "JWT 토큰을 통해 인증된 사용자의 providerId를 반환합니다."
    )
    fun getMyInfo(
        @AuthenticationPrincipal providerId: String
    ): Map<String, Any> {
        return mapOf(
            "success" to true,
            "providerId" to providerId,
            "name" to (userRepository.findByProviderId(providerId)?.name ?: "기본 이름")
        )
    }
}
