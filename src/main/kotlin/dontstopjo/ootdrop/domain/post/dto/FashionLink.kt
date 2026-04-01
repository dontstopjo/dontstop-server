package dontstopjo.ootdrop.domain.post.dto

import dontstopjo.ootdrop.domain.post.enums.FashionCategory
import jakarta.persistence.Embeddable

@Embeddable
data class FashionLink(
    val link: String,
    val description: String,
    val category: FashionCategory,
)