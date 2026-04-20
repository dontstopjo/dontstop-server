package dontstopjo.ootdrop.domain.user.controller

import dontstopjo.ootdrop.domain.user.dto.UserInfoDto
import dontstopjo.ootdrop.domain.user.dto.UpdateMyInfoDto
import dontstopjo.ootdrop.domain.user.dto.UserMyPageDto
import dontstopjo.ootdrop.domain.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Encoding
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController("user")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/me")
    fun info(
        @AuthenticationPrincipal userId: Long,
    ): ResponseEntity<UserInfoDto> {
        return ResponseEntity.ok(userService.getMyInfo(userId))
    }

    @PutMapping(
        value = ["/me/update"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    @Operation(summary = "내정보 수정")
    fun updateMyInfo(
        @AuthenticationPrincipal userId: Long,
        @RequestBody(
            content = [Content(encoding = [Encoding(name = "data", contentType = "application/json")])],
            required = false
        ) updateMyInfoDto: UpdateMyInfoDto?,
        @RequestPart(value = "files", required = false) images: MultipartFile?,
    ){
        userService.updateMyInfo(updateMyInfoDto, images, userId)
    }


    @GetMapping("/mypage/{userId}")
    @Operation(summary = "마이페이지")
    fun readMyPage(
        @PathVariable userId: Long,
    ): UserMyPageDto {
        return userService.readMyPage(userId)
    }
}
