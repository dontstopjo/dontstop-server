package dontstopjo.ootdrop.domain.post.repository

import dontstopjo.ootdrop.domain.post.entity.Post
import dontstopjo.ootdrop.domain.post.enums.MainStyle
import dontstopjo.ootdrop.domain.post.enums.SubStyle

interface PostRepositoryCustom {
    fun findPostsByCriteria(
        keyword: String?,
        mainStyle: MainStyle?,
        subStyles: List<SubStyle>?
    ): List<Post>
}
