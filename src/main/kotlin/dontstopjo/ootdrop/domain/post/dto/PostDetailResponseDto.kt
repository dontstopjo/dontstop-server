package dontstopjo.ootdrop.domain.post.dto

import dontstopjo.ootdrop.domain.post.enums.MainStyle
import dontstopjo.ootdrop.domain.post.enums.SubStyle

data class PostDetailResponseDto(
    val title: String,
    val content: String,
    val imageURLs: List<String>,
    val username: String,
    val userId: String,
    val profileImageURL: String,

    val likes: Long,
    val views: Long,
    val saves: Long,

    val isSaved: Boolean,
    val isLiked: Boolean,

    val mainStyle: MainStyle,
    val subStyles: List<SubStyle>,

    val links: List<LinkAndImageDto>,

    val comments: List<CommentDto>,
)