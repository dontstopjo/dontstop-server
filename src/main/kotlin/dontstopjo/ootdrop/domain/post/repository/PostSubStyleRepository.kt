package dontstopjo.ootdrop.domain.post.repository

import dontstopjo.ootdrop.domain.post.entity.Post
import dontstopjo.ootdrop.domain.post.entity.PostSubStyle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostSubStyleRepository : JpaRepository<PostSubStyle, Long> {
    fun findByPost(post: Post): MutableList<PostSubStyle>

    @Modifying
    @Query("delete from PostSubStyle pss where pss.post = :post")
    fun deleteAllByPost(post: Post)
}
