package dontstopjo.ootdrop.domain.post.entity

import dontstopjo.ootdrop.domain.post.enums.MainStyle
import dontstopjo.ootdrop.domain.post.enums.SubStyle
import dontstopjo.ootdrop.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "posts")
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "post_sub_styles",
        joinColumns = [JoinColumn(name = "post_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "sub_style_name")
    var subStyles: MutableList<SubStyle> = mutableListOf(),
)
