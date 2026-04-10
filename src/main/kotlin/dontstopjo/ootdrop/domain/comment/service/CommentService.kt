package dontstopjo.ootdrop.domain.comment.service

import dontstopjo.ootdrop.domain.comment.entity.Comment
import dontstopjo.ootdrop.domain.comment.repository.CommentRepository
import dontstopjo.ootdrop.domain.post.repository.PostRepository
import dontstopjo.ootdrop.domain.user.repository.UserRepository
import dontstopjo.ootdrop.global.exception.PostNotFoundException
import dontstopjo.ootdrop.global.exception.UserNotFoundException
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) {
    fun create(postId: Long, content: String, userId: Long) {
        commentRepository.save(Comment(
            user = userRepository.findUserById(userId)?: throw UserNotFoundException(),
            post = postRepository.findPostById(postId)?: throw PostNotFoundException(),
            content = content,
        ))
    }
}