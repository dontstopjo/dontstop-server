package dontstopjo.ootdrop.domain.post.entity

import dontstopjo.ootdrop.domain.post.dto.FashionLink
import dontstopjo.ootdrop.domain.post.enums.MainStyle
import dontstopjo.ootdrop.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Embeddable
data class PostImage(
    @Column(nullable = false)
    val imageKey: String,

    @Column(nullable = false, name = "image_order")
    val order: Int
)

@Entity
@Table(
    name = "posts",
    indexes = [
        Index(name = "idx_post_main_style", columnList = "main_style"),
        Index(name = "idx_post_user_id", columnList = "user_id"),
        Index(name = "idx_post_title", columnList = "title")
    ]
)
class Post(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "post_images", // 테이블명 변경 (권장)
        joinColumns = [JoinColumn(name = "post_id")]
    )
    @OrderColumn(name = "list_index")
    var images: MutableList<PostImage> = mutableListOf(),

    @Column(nullable = false)
    var isPublic: Boolean,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var mainStyle: MainStyle,

    @ElementCollection
    @CollectionTable(
        name = "post_links", // 별도로 생성될 테이블 이름
        joinColumns = [JoinColumn(name = "post_id")] // 외래키 설정
    )
    var fashionLink: MutableList<FashionLink> = mutableListOf(),
)
