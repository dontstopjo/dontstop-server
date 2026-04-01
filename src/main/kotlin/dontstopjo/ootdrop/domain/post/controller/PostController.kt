package dontstopjo.ootdrop.domain.post.controller

import dontstopjo.ootdrop.domain.post.dto.PostCrateRequestDto
import dontstopjo.ootdrop.domain.post.dto.PostDetailResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostSummaryResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostUpdateRequestDto
import dontstopjo.ootdrop.domain.post.service.PostService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
    fun getPosts(): ResponseEntity<List<PostSummaryResponseDto>> {
        return ResponseEntity.ok(postService.getPosts())
    }

    @GetMapping("/{postId}")
    @Operation(summary = "1개만 조회")
    fun getPost(@PathVariable postId: Int): ResponseEntity<PostDetailResponseDto> {
        return ResponseEntity.ok(postService.getPost(postId))
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "생성")
    fun createPost(
        @RequestPart("data") postCrateRequestDto: PostCrateRequestDto,
        @RequestPart(value = "files", required = true) images: List<MultipartFile>,
    ): ResponseEntity<Unit> {
        postService.createPost(postCrateRequestDto, images)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/{postId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "업데이트")
    fun updatePost(
        @PathVariable postId: Int,
        @RequestPart("data") postUpdateRequestDto: PostUpdateRequestDto,
        @RequestPart(value = "files", required = true) files: List<MultipartFile>,
    ): ResponseEntity<Unit> {
        postService.updatePost(postId, postUpdateRequestDto, files)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "삭제")
    fun deletePost(@PathVariable postId: Int): ResponseEntity<Unit> {
        postService.deletePost(postId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{postId}/save")
    @Operation(summary = "저장 (찜)")
    fun save(@PathVariable postId: Int): ResponseEntity<Unit> {
        postService.savePost(postId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "좋아요 +1")
    fun like(@PathVariable postId: Int): ResponseEntity<Unit> {
        postService.likePost(postId)
        return ResponseEntity.ok().build()
    }
}
