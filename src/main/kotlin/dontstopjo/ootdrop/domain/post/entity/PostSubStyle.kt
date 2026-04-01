package dontstopjo.ootdrop.domain.post.entity

import dontstopjo.ootdrop.domain.post.enums.SubStyle
import jakarta.persistence.*

@Entity
@Table(
    name = "posts_sub_styles",
    indexes = [
        Index(name = "idx_post_id", columnList = "post_id"),
        Index(name = "idx_sub_style_name", columnList = "sub_style_name")
    ]
)
class PostSubStyle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    val post: Post,

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_style_name", nullable = false)
    val subStyleName: SubStyle
)