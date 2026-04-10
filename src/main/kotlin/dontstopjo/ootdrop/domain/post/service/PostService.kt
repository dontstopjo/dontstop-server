package dontstopjo.ootdrop.domain.post.service

import dontstopjo.ootdrop.domain.comment.dto.CommentDto
import dontstopjo.ootdrop.domain.comment.repository.CommentRepository
import dontstopjo.ootdrop.domain.post.dto.FashionLinkDto
import dontstopjo.ootdrop.domain.post.dto.PostCrateRequestDto
import dontstopjo.ootdrop.domain.post.dto.PostDetailResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostSummaryResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostUpdateRequestDto
import dontstopjo.ootdrop.domain.post.entity.LikedPost
import dontstopjo.ootdrop.domain.post.entity.Post
import dontstopjo.ootdrop.domain.post.entity.PostImage
import dontstopjo.ootdrop.domain.post.entity.PostSubStyle
import dontstopjo.ootdrop.domain.post.entity.SavedPost
import dontstopjo.ootdrop.domain.post.entity.ViewedPost
import dontstopjo.ootdrop.domain.post.repository.LikedPostRepository
import dontstopjo.ootdrop.domain.post.repository.PostRepository
import dontstopjo.ootdrop.domain.post.repository.PostSubStyleRepository
import dontstopjo.ootdrop.domain.post.repository.SavedPostRepository
import dontstopjo.ootdrop.domain.post.repository.ViewedPostRepository
import dontstopjo.ootdrop.domain.user.repository.UserRepository
import dontstopjo.ootdrop.global.exception.ForbiddenException
import dontstopjo.ootdrop.global.exception.IdNotFoundException
import dontstopjo.ootdrop.global.exception.NotPostOwner
import dontstopjo.ootdrop.global.exception.PostNotFoundException
import dontstopjo.ootdrop.global.exception.UserNotFoundException
import dontstopjo.ootdrop.global.s3.S3Service
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val postSubStyleRepository: PostSubStyleRepository,
    private val commentRepository: CommentRepository,

    private val likedPostRepository: LikedPostRepository,
    private val savedPostRepository: SavedPostRepository,
    private val viewedPostRepository: ViewedPostRepository,

    private val s3Service: S3Service // 범용 S3Service 주입
) {
    @Transactional(readOnly = true)
    fun getPosts(): List<PostSummaryResponseDto> {
        return postRepository.findAllByOrderByCreatedAtDesc()
            .filter{ it.isPublic }
            .map { post ->
                PostSummaryResponseDto(
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

    @Transactional
    fun getPostDetail(postId: Long, userId: Long): PostDetailResponseDto {
        val post = postRepository.findPostById(postId)?: throw PostNotFoundException()
        if(!post.isPublic && post.user.id != userId){
            throw ForbiddenException()
        }
        viewedPostRepository.save(
            ViewedPost(
                user = post.user,
                post = post,
            )
        )

        return PostDetailResponseDto(
            title = post.title,
            content = post.content,
            imageURLs = post.images.sortedBy { it.order }.map { it.imageKey },
            username = post.user.name,
            userId = post.user.id,
            profileImageURL = post.user.profileImageUrl,

            likes = likedPostRepository.countByPost(post),
            views = viewedPostRepository.countByPost(post),
            saves = savedPostRepository.countByPost(post),

            isSaved = savedPostRepository.existsByPostAndUser(post, post.user) ,
            isLiked = likedPostRepository.existsByPostAndUser(post, post.user),

            mainStyle = post.mainStyle,
            subStyles = postSubStyleRepository.findByPost(post).map { it.subStyle },
            links = post.fashionLink.map {
                FashionLinkDto(
                    link = it.link,
                    description = it.description,
                    category = it.category,
                    imageURL = "나중에 개발함" // TODO()
                )
            },
            comments = commentRepository.findByPost(post).map {
                CommentDto(
                    text = it.content,
                    profileImageURL = it.user.profileImageUrl,
                    username = it.user.name,
                )
            }
        )
    }

    @Transactional
    fun createPost(requestDto: PostCrateRequestDto, images: List<MultipartFile>, userId: Long) {
        val imageKeys = images.map { s3Service.uploadFile("posts", it) }

        val post = Post(
            user = userRepository.findUserById(userId)?: throw UserNotFoundException(),
            title = requestDto.title,
            content = requestDto.content,

            images = imageKeys.mapIndexed {
                index, key -> PostImage( imageKey = key, order = index )
            }.toMutableList(),

            isPublic = requestDto.isPublic,
            mainStyle = requestDto.mainStyle,
        )
        postRepository.save(post)
    }

    @Transactional
    fun updatePost(postId: Long, requestDto: PostUpdateRequestDto, files: List<MultipartFile>, userId: Long) {
        val post = postRepository.findPostById(postId)?: throw PostNotFoundException()
        if(post.user.id != userId) throw NotPostOwner()

        post.images.filter {
            // 유지하지 않을것 삭제
            !requestDto.imageURLs.map { s3Service.getKeyFromUrl(it.url) }.contains(it.imageKey)
        }.forEach { s3Service.deleteFile(it.imageKey) }

        val savedImageUrls = requestDto.imageURLs.map {
            PostImage(
                imageKey = s3Service.getKeyFromUrl(it.url),
                order = it.order
            )
        }.toMutableList()
        savedImageUrls.addAll(
            //files랑 requestDto.newFileOrders를 PostImage로 매핑해서 저장하는거
            files
                .mapIndexed {
                    index, file ->
                        PostImage(
                            imageKey = s3Service.uploadFile("posts", file),
                            order = requestDto.newFileOrders[index]
                        )
                }.toMutableList()
        )


        post.title = requestDto.title
        post.content = requestDto.content
        post.images = savedImageUrls
        post.isPublic = requestDto.isPublic
        post.mainStyle = requestDto.mainStyle
        post.updatedAt = LocalDateTime.now()

        postSubStyleRepository.deleteByPost(post)
        postSubStyleRepository.saveAll(
            requestDto.subStyles.map {
                PostSubStyle(
                    post = post,
                    subStyle = it
                )
            }
        )
        postRepository.save(post)
    }

    @Transactional
    fun deletePost(postId: Long, userId: Long) {
        val post = postRepository.findPostById(postId)?: throw PostNotFoundException()
        if(post.user.id != userId) throw NotPostOwner()

        postSubStyleRepository.deleteAllByPost(post)
        likedPostRepository.deleteAllByPost(post)
        viewedPostRepository.deleteAllByPost(post)
        savedPostRepository.deleteAllByPost(post)

        post.images.forEach { s3Service.deleteFile(it.imageKey) }
        postRepository.delete(post)
    }

    @Transactional
    fun savePost(postId: Long, userId: Long) {
        savedPostRepository.save(
            SavedPost(
                user = userRepository.findUserById(userId)?: throw UserNotFoundException(),
                post = postRepository.findPostById(postId)?: throw PostNotFoundException()
            )
        )
    }

    @Transactional
    fun unSavePost(postId: Long, userId: Long) {
        savedPostRepository.delete(
            SavedPost(
                user = userRepository.findUserById(userId)?: throw UserNotFoundException(),
                post = postRepository.findPostById(postId)?: throw PostNotFoundException()
            )
        )
    }

    @Transactional
    fun likePost(postId: Long, userId: Long) {
        likedPostRepository.save(
            LikedPost(
                user = userRepository.findUserById(userId)?: throw UserNotFoundException(),
                post = postRepository.findPostById(postId)?: throw PostNotFoundException()
            )
        )
    }

    @Transactional
    fun unLikePost(postId: Long, userId: Long) {
        likedPostRepository.delete(
            LikedPost(
                user = userRepository.findUserById(userId)?: throw UserNotFoundException(),
                post = postRepository.findPostById(postId)?: throw PostNotFoundException()
            )
        )
    }
}
