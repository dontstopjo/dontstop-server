package dontstopjo.ootdrop.domain.user.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    val id: String,

    @Column(nullable = false)
    var name: String,

    @Column
    var profileImage: String,

    @Column(nullable = false)
    var description: String = "",
) {
    fun updateInfo(name: String, profileImage: String, description: String) {
        this.name = name
        this.profileImage = profileImage
        this.description = description
    }
}