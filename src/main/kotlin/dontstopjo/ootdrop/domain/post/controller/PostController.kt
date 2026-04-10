package dontstopjo.ootdrop.domain.post.controller

import dontstopjo.ootdrop.domain.post.dto.PostCrateRequestDto
import dontstopjo.ootdrop.domain.post.dto.PostDetailResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostSummaryResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostUpdateRequestDto
import dontstopjo.ootdrop.domain.post.service.PostService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Encoding
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/posts")
@Tag(name = "POST", description = "게시물 API 검색은 나중에 만든다")
class PostController(
    private val postService: PostService
) {
    @GetMapping
    @Operation(summary = "전체 조회")
    fun getPosts(): List<PostSummaryResponseDto> {
        return postService.getPosts()
    }

    @GetMapping("/{postId}")
    @Operation(summary = "1개만 조회 \nTODO()\n지금 링크의 imageURL이 비정상적인 값임 참고하셈")
    fun getPostDetail(
        @PathVariable postId: Long,
        @AuthenticationPrincipal userId: Long
    ): PostDetailResponseDto {
        return postService.getPostDetail(postId, userId)
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "생성")
    fun createPost(
        @RequestBody(
            content = [Content(encoding = [Encoding(name = "data", contentType = "application/json")])]
        )
        @RequestPart("data")
        postCrateRequestDto: PostCrateRequestDto,

        @RequestPart(value = "files", required = true) images: List<MultipartFile>,
        @AuthenticationPrincipal userId: Long
    ){
        postService.createPost(postCrateRequestDto, images, userId)
    }

    @PutMapping("/{postId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "업데이트")
    fun updatePost(
        @PathVariable postId: Long,
        @RequestPart("data") postUpdateRequestDto: PostUpdateRequestDto,
        @RequestPart(value = "files", required = false) files: List<MultipartFile>,
        @AuthenticationPrincipal userId: Long
    ){
        postService.updatePost(postId, postUpdateRequestDto, files, userId)
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "삭제")
    fun deletePost(
        @PathVariable postId: Long,
        @AuthenticationPrincipal userId: Long
    ){
        postService.deletePost(postId, userId)
    }

    @PostMapping("/{postId}/save")
    @Operation(summary = "저장 (찜)")
    fun save(
        @PathVariable postId: Long,
        @AuthenticationPrincipal userId: Long
    ){
        postService.savePost(postId, userId)
    }

    @DeleteMapping("/{postId}/unsave")
    @Operation(summary = "저장 (찜) 해제")
    fun unsave(
        @PathVariable postId: Long,
        @AuthenticationPrincipal userId: Long
    ){
        postService.unSavePost(postId, userId)
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "좋아요 +1")
    fun like(
        @PathVariable postId: Long,
        @AuthenticationPrincipal userId: Long
    ){
        postService.likePost(postId, userId)
    }

    @DeleteMapping("/{postId}/unlike")
    @Operation(summary = "좋아요 -1")
    fun unLike(
        @PathVariable postId: Long,
        @AuthenticationPrincipal userId: Long
    ){
        postService.unLikePost(postId, userId)
    }
}
