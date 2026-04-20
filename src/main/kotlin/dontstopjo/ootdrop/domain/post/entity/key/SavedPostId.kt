package dontstopjo.ootdrop.domain.post.entity.key

import java.io.Serializable

data class SavedPostId(
    val user: Long? = null,
    val post: Long? = null
) : Serializable
