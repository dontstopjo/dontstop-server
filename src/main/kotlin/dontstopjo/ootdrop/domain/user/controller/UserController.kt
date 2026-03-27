package dontstopjo.ootdrop.domain.user.controller

import dontstopjo.ootdrop.domain.user.dto.UserInfoDto
import dontstopjo.ootdrop.domain.user.dto.UpdateMyInfoDto
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController("user")
class UserController {
    @GetMapping("/me")
    fun info(): UserInfoDto {
        return TODO()
    }

    @PostMapping(
        value = ["/me/update"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun updateMyInfo(
        @RequestPart("data") updateMyInfoDto: UpdateMyInfoDto,
        @RequestPart(value = "files", required = true) images: List<MultipartFile>,
    ){}
}