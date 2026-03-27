package dontstopjo.ootdrop.domain.user.repository

import dontstopjo.ootdrop.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findById(id: String): User?
}
