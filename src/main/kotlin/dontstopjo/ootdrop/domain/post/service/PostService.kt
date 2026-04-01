package dontstopjo.ootdrop.domain.post.service

import dontstopjo.ootdrop.domain.post.dto.PostCrateRequestDto
import dontstopjo.ootdrop.domain.post.dto.PostDetailResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostSummaryResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostUpdateRequestDto
import dontstopjo.ootdrop.domain.post.entity.Post
import dontstopjo.ootdrop.domain.post.repository.PostRepository
import dontstopjo.ootdrop.domain.user.repository.UserRepository
import dontstopjo.ootdrop.global.exception.IdNotFoundException
import dontstopjo.ootdrop.global.s3.S3Service
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val s3Service: S3Service // 범용 S3Service 주입
) {
    @Transactional(readOnly = true)
    fun getPosts(): List<PostSummaryResponseDto> {
        return postRepository.findAll().map { post ->
            PostSummaryResponseDto(
                title = post.title,
                imageURL = s3Service.buildImageUrl(post.imageKeys[0]),
                username = post.user.name,
                userId = post.user.id,
                postId = post.id,

                likes = post.likes,
                views = post.views,
                saves = post.saves,
                mainStyle = post.mainStyle,
                subStyles = post.subStyles
            )
        }
    }

    @Transactional(readOnly = true)
    fun getPost(postId: Long): PostDetailResponseDto {

        val post = postRepository.findPostById(postId)
        val views = postRepository.updateViews(postId)

        post.views++
        return PostDetailResponseDto(
            title = post.title,
            content = post.content,
            imageURLs = post.imageKeys.map { s3Service.buildImageUrl(it) },
            username = post.user.name,
            userId = post.user.id,
            profileImageURL = post.user.profileImage,

            likes = post.likes,
            views = views,
            saves = post.saves,

            isSaved = false,
            isLiked = false,

            mainStyle = post.mainStyle,
            subStyles = post.subStyles,

            links = post.likes,
            comments = TODO(),
        )
    }

    @Transactional
    fun createPost(requestDto: PostCrateRequestDto, images: List<MultipartFile>): PostDetailResponseDto {
        val user = getCurrentUser()
        
        val imageUrls = images.map { s3Service.uploadFile("posts", it) }

        val post = Post(
            user = user,
            title = requestDto.title,
            content = requestDto.content,
            imageKeys = imageUrls, // URL 저장
            isPublic = requestDto.isPublic,
            mainStyle = requestDto.mainStyle,
            subStyles = requestDto.subStyles.toMutableList()
        )
        val savedPost = postRepository.save(post)
        return getPost(savedPost.id!!.toInt())
    }

    @Transactional
    fun updatePost(postId: Int, requestDto: PostUpdateRequestDto, files: List<MultipartFile>): PostDetailResponseDto {
        val post = findPostById(postId)
        
        post.imageKeys.forEach { s3Service.deleteFile(it) }
        val newImageUrls = files.map { s3Service.uploadFile("posts", it) }

        post.title = requestDto.title
        post.content = requestDto.content
        post.imageKeys = newImageUrls
        post.isPublic = requestDto.isPublic
        post.mainStyle = requestDto.mainStyle
        post.subStyles = requestDto.subStyles.toMutableList()
        post.updatedAt = LocalDateTime.now()
        
        return getPost(postId)
    }

    @Transactional
    fun deletePost(postId: Int) {
        val post = findPostById(postId)
        post.imageKeys.forEach { s3Service.deleteFile(it) }
        postRepository.delete(post)
    }

    @Transactional
    fun savePost(postId: Int) {
        // SavedPost 관련 로직 구현
    }

    @Transactional
    fun likePost(postId: Int) {
        val post = findPostById(postId)
        post.likes++
    }
}
