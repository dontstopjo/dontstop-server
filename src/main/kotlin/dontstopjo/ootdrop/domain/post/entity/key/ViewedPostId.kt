package dontstopjo.ootdrop.domain.post.entity.key

import java.io.Serializable

data class ViewedPostId(
    val user: Long? = null,
    val post: Long? = null
) : Serializable
