package dontstopjo.ootdrop.domain.post.dto

import dontstopjo.ootdrop.domain.post.enums.FashionCategory

data class LinkDto(
    val link: String,
    val description: String,
    val category: FashionCategory,
)