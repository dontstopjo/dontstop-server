package dontstopjo.ootdrop.domain.user.service

import dontstopjo.ootdrop.domain.user.dto.UpdateMyInfoDto
import dontstopjo.ootdrop.domain.user.dto.UserInfoDto
import dontstopjo.ootdrop.domain.user.repository.UserRepository
import dontstopjo.ootdrop.global.exception.UserNotFoundException
import dontstopjo.ootdrop.global.s3.S3Service
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    private val userRepository: UserRepository,
    private val s3Service: S3Service // 범용 S3Service 주입
) {
    @Transactional(readOnly = true)
    fun getMyInfo(): UserInfoDto {
        val user = getCurrentUser()
        return UserInfoDto(
            username = user.name,
            profileImageURL = user.profileImageUrl,
            description = user.description
        )
    }

    @Transactional
    fun updateMyInfo(updateMyInfoDto: UpdateMyInfoDto, image: MultipartFile): UserInfoDto {
        val user = getCurrentUser()

        if(s3Service.isOurS3Url(user.profileImageUrl)) {
            s3Service.deleteFile(s3Service.getKeyFromUrl(user.profileImageUrl))
        }
        val profileImageUrl = s3Service.uploadFile("profiles", image)

        user.updateInfo(
            name = updateMyInfoDto.username,
            profileImageUrl = profileImageUrl,
            description = updateMyInfoDto.description
        )

        val updatedUser = userRepository.save(user)

        return UserInfoDto(
            username = updatedUser.name,
            profileImageURL = updatedUser.profileImageUrl,
            description = updatedUser.description
        )
    }

    private fun getCurrentUser() = userRepository.findUserById(
        (SecurityContextHolder.getContext().authentication?.principal
            ?: throw UserNotFoundException())
            .toString().toLong()
    ) ?: throw UserNotFoundException()
}
