package dontstopjo.ootdrop.domain.post.repository

import dontstopjo.ootdrop.domain.post.entity.Post
import dontstopjo.ootdrop.domain.post.entity.SavedPost
import dontstopjo.ootdrop.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SavedPostRepository : JpaRepository<SavedPost, Long> {
    fun countByPost(post: Post): Long
    fun existsByPostAndUser(post: Post, user: User): Boolean
    fun findAllByUser(user: User): List<SavedPost>
    fun deleteAllByPost(post: Post)
}
