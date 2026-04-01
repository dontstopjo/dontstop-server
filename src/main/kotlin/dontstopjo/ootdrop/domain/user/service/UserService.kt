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
            username = user,
            profileImageURL = user.profileImage,
            description = user.description
        )
    }

    @Transactional
    fun updateMyInfo(updateMyInfoDto: UpdateMyInfoDto, images: List<MultipartFile>): UserInfoDto {
        val user = getCurrentUser()

        val imageUrl = if (images.isNotEmpty()) {
            // 기존 이미지가 있다면 삭제
            if (user.profileImage.isNotBlank()) {
                s3Service.deleteFile(user.profileImage)
            }
            // 새 이미지 업로드
            s3Service.uploadFile("profiles", images[0])
        } else {
            user.profileImage
        }

        user.updateInfo(
            name = updateMyInfoDto.username,
            profileImage = imageUrl,
            description = updateMyInfoDto.description
        )

        val updatedUser = userRepository.save(user)

        return UserInfoDto(
            username = updatedUser.name,
            profileImageURL = updatedUser.profileImage,
            description = updatedUser.description
        )
    }

    private fun getCurrentUser() = userRepository.findById(
        (SecurityContextHolder.getContext().authentication?.principal
            ?: throw UserNotFoundException())
            .toString()
    ) ?: throw UserNotFoundException()
}
