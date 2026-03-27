package dontstopjo.ootdrop.domain.post.dto

import dontstopjo.ootdrop.domain.post.enums.MainStyle
import dontstopjo.ootdrop.domain.post.enums.SubStyle

data class PostSummaryResponseDto(
    val title: String,
    val imageURL: String,
    val username: String,
    val userId: String,

    val likes: Long,
    val views: Long,
    val saves: Long,

    val mainStyle: MainStyle,
    val subStyles: List<SubStyle>,
)