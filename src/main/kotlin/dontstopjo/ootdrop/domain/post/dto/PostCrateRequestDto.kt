package dontstopjo.ootdrop.domain.post.dto

import dontstopjo.ootdrop.domain.post.enums.MainStyle
import dontstopjo.ootdrop.domain.post.enums.SubStyle

data class PostCrateRequestDto(
    val title : String,
    val description: String,

    val mainStyle: MainStyle,
    val subStyles: List<SubStyle>,

    val links: List<LinkDto>,
    val isPublic: Boolean
)