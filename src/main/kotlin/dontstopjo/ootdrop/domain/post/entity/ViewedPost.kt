package dontstopjo.ootdrop.domain.post.entity

import dontstopjo.ootdrop.domain.post.entity.key.ViewedPostId
import dontstopjo.ootdrop.domain.user.entity.User
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "viewed_posts")
@IdClass(ViewedPostId::class)
class ViewedPost(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        foreignKey = ForeignKey(name = "FK_VIEWED_POST_ON_USER", value = ConstraintMode.CONSTRAINT)
    )
    val user: User,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "post_id",
        foreignKey = ForeignKey(name = "FK_VIEWED_POST_ON_POST", value = ConstraintMode.CONSTRAINT)
    )
    val post: Post
)