package dontstopjo.ootdrop.domain.comment.repository

import dontstopjo.ootdrop.domain.comment.entity.Comment
import dontstopjo.ootdrop.domain.post.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository: JpaRepository<Comment, Long> {
    fun findByPost(post: Post): MutableList<Comment>

    @Modifying
    @Query("delete from Comment c where c.post = :post")
    fun deleteAllByPost(post: Post)
}
