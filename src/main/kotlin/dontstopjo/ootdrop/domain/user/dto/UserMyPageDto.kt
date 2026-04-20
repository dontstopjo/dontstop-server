package dontstopjo.ootdrop.domain.user.dto

import dontstopjo.ootdrop.domain.post.dto.PostSummaryResponseDto

data class UserMyPageDto(
    val userInfoDto: UserInfoDto,
    val publicPosts: List<PostSummaryResponseDto>,
    val privatePosts: List<PostSummaryResponseDto>,
    val savedPosts: List<PostSummaryResponseDto>,
)