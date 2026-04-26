package dontstopjo.ootdrop.domain.post.repository

import dontstopjo.ootdrop.domain.post.entity.Post
import dontstopjo.ootdrop.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long>, PostRepositoryCustom {

    fun findPostById(id: Long): Post?

    fun findAllByOrderByCreatedAtDesc(): MutableList<Post>

    fun findByUserAndIsPublicOrderByCreatedAtDesc(user: User, isPublic: Boolean): MutableList<Post>
}
