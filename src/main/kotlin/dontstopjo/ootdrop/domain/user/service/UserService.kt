package dontstopjo.ootdrop.domain.user.service

import dontstopjo.ootdrop.domain.post.repository.PostRepository
import dontstopjo.ootdrop.domain.post.repository.SavedPostRepository
import dontstopjo.ootdrop.domain.post.service.PostToPostSummaryResponseDtoService
import dontstopjo.ootdrop.domain.user.dto.UpdateMyInfoDto
import dontstopjo.ootdrop.domain.user.dto.UserInfoDto
import dontstopjo.ootdrop.domain.user.dto.UserMyPageDto
import dontstopjo.ootdrop.domain.user.repository.UserRepository
import dontstopjo.ootdrop.global.exception.UserNotFoundException
import dontstopjo.ootdrop.global.s3.S3Service
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    private val postToPostSummaryResponseDtoService: PostToPostSummaryResponseDtoService,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val savedPostRepository: SavedPostRepository,
    private val s3Service: S3Service // 범용 S3Service 주입
) {
    @Transactional(readOnly = true)
    fun getMyInfo(userId: Long): UserInfoDto {
        val user = userRepository.findUserById(userId)?: throw UserNotFoundException()
        return UserInfoDto(
            username = user.name,
            profileImageURL = user.profileImageUrl,
            description = user.description,
            userId = user.id
        )
    }

    @Transactional
    fun updateMyInfo(updateMyInfoDto: UpdateMyInfoDto?, image: MultipartFile?, userId: Long) {
        val user = userRepository.findUserById(userId)?: throw UserNotFoundException()
        if(s3Service.isOurS3Url(user.profileImageUrl)) {
            s3Service.deleteFile(s3Service.getKeyFromUrl(user.profileImageUrl))
        }
        val profileImageUrl =
            if(image == null)
                user.profileImageUrl
            else
                s3Service.buildImageUrl (s3Service.uploadFile("profiles", image))

        user.updateInfo(
            name = updateMyInfoDto?.username?: user.name,
            profileImageUrl = profileImageUrl,
            description = updateMyInfoDto?.description?: user.description,
        )

        userRepository.save(user)
    }

    fun readMyPage(userId: Long): UserMyPageDto{
        val user = userRepository.findUserById(userId)?: throw UserNotFoundException()
        val publicPosts = postRepository.findByUserAndIsPublicOrderByCreatedAtDesc(user, true)
        val privatePosts = postRepository.findByUserAndIsPublicOrderByCreatedAtDesc(user, false)
        val savedPosts = savedPostRepository.findAllByUser(user)

        return UserMyPageDto(
            userInfoDto = UserInfoDto(
                userId = userId,
                profileImageURL = user.profileImageUrl,
                username = user.name,
                description = user.description,
            ),
            publicPosts = publicPosts.map { postToPostSummaryResponseDtoService.postToPostSummaryResponseDto(it) },
            privatePosts = privatePosts.map { postToPostSummaryResponseDtoService.postToPostSummaryResponseDto(it) },
            savedPosts = savedPosts.map { postToPostSummaryResponseDtoService.postToPostSummaryResponseDto(it.post) }
        )
    }
}
