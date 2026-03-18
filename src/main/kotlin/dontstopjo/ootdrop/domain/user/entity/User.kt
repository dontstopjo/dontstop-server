package dontstopjo.ootdrop.domain.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    var name: String,

    @Column
    var profileImage: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: OAuth2Provider,

    @Column(nullable = false)
    val providerId: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun updateInfo(name: String, profileImage: String) {
        this.name = name
        this.profileImage = profileImage
        this.updatedAt = LocalDateTime.now()
    }
}

enum class OAuth2Provider {
    KAKAO
}
