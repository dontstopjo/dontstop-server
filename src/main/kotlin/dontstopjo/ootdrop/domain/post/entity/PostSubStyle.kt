package dontstopjo.ootdrop.domain.post.entity

import dontstopjo.ootdrop.domain.post.enums.SubStyle
import jakarta.persistence.*

@Entity
@Table(
    name = "posts_sub_styles",
    indexes = [
        Index(name = "idx_post_id", columnList = "post_id"),
        Index(name = "idx_sub_style_name", columnList = "sub_style")
    ]
)
class PostSubStyle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "post_id",
        foreignKey = ForeignKey(name = "FK_POST_SUB_STYLE_ON_POST", value = ConstraintMode.CONSTRAINT)
    )
    val post: Post,

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_style", nullable = false)
    val subStyle: SubStyle
)