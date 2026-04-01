package dontstopjo.ootdrop.domain.post.entity

import dontstopjo.ootdrop.domain.post.dto.FashionLink
import dontstopjo.ootdrop.domain.post.enums.MainStyle
import dontstopjo.ootdrop.domain.post.enums.SubStyle
import dontstopjo.ootdrop.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

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
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "post_image_keys",
        joinColumns = [JoinColumn(name = "post_id")]
    )
    @Column(nullable = false)
    var imageKeys: List<String>,

    @Column(nullable = false)
    var isPublic: Boolean,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var likes: Long = 0,

    @Column(nullable = false)
    var views: Long = 0,

    @Column(nullable = false)
    var saves: Long = 0,

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
