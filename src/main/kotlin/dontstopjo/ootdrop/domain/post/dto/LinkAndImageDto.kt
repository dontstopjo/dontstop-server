package dontstopjo.ootdrop.domain.post.dto

import dontstopjo.ootdrop.domain.post.enums.FashionCategory

data class LinkAndImageDto(
    val link: String,
    val description: String,
    val category: FashionCategory,
    val imageURL: String
)