package dontstopjo.ootdrop.domain.post.repository

import dontstopjo.ootdrop.domain.post.entity.Post
import dontstopjo.ootdrop.domain.post.entity.ViewedPost
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ViewedPostRepository : JpaRepository<ViewedPost, Long> {
    fun countByPost(post: Post): Long
    fun deleteAllByPost(post: Post)
}
