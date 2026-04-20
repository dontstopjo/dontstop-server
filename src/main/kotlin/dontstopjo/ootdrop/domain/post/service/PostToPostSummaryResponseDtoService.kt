package dontstopjo.ootdrop.domain.post.service

import dontstopjo.ootdrop.domain.post.dto.PostSummaryResponseDto
import dontstopjo.ootdrop.domain.post.entity.Post
import dontstopjo.ootdrop.domain.post.repository.LikedPostRepository
import dontstopjo.ootdrop.domain.post.repository.PostSubStyleRepository
import dontstopjo.ootdrop.domain.post.repository.SavedPostRepository
import dontstopjo.ootdrop.domain.post.repository.ViewedPostRepository
import dontstopjo.ootdrop.global.exception.IdNotFoundException
import dontstopjo.ootdrop.global.s3.S3Service
import org.springframework.stereotype.Service

@Service
class PostToPostSummaryResponseDtoService(
    private val postSubStyleRepository: PostSubStyleRepository,

    private val likedPostRepository: LikedPostRepository,
    private val savedPostRepository: SavedPostRepository,
    private val viewedPostRepository: ViewedPostRepository,

    private val s3Service: S3Service, // 범용 S3Service 주입
) {
    fun postToPostSummaryResponseDto(post: Post): PostSummaryResponseDto{
        return PostSummaryResponseDto(
            title = post.title,
            imageURL = s3Service.buildImageUrl(post.images[0].imageKey),
            username = post.user.name,
            userId = post.user.id,
            postId = post.id?: throw IdNotFoundException(),

            likes = likedPostRepository.countByPost(post),
            views = viewedPostRepository.countByPost(post),
            saves = savedPostRepository.countByPost(post),

            mainStyle = post.mainStyle,
            subStyles = postSubStyleRepository.findByPost(post).map { it.subStyle }
        )
    }
}