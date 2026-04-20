package dontstopjo.ootdrop.domain.user.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    val id: Long,

    @Column(nullable = false)
    var name: String,

    @Column
    var profileImageUrl: String,

    @Column(nullable = false)
    var description: String = "",
) {
    fun updateInfo(name: String, profileImageUrl: String, description: String) {
        this.name = name
        this.profileImageUrl = profileImageUrl
        this.description = description
    }
}