package dontstopjo.ootdrop.domain.post.service

import dontstopjo.ootdrop.domain.post.repository.PostRepository
import dontstopjo.ootdrop.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val s3PostImageService: S3PostImageService
) {
}
