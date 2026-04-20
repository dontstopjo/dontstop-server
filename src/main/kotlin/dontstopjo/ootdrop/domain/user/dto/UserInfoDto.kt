package dontstopjo.ootdrop.domain.user.dto

data class UserInfoDto (
    val userId: Long,
    val profileImageURL: String,
    val username: String,
    val description: String,
)