package dontstopjo.ootdrop.domain.post.controller

import dontstopjo.ootdrop.domain.post.dto.PostCrateRequestDto
import dontstopjo.ootdrop.domain.post.dto.PostDetailResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostSummaryResponseDto
import dontstopjo.ootdrop.domain.post.dto.PostUpdateRequestDto
import dontstopjo.ootdrop.domain.post.service.PostService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
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
    @Operation(
        summary = "전체 조회",
    )
    fun getPosts(): List<PostSummaryResponseDto>{
        return TODO()
    }

    @GetMapping("/{postId}")
    @Operation(
        summary = "1개만 조회",
    )
    fun getPost(
        @PathVariable postId: Int
    ): PostDetailResponseDto{
        return TODO()
    }

    @PostMapping(
        value = ["/crate"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE] // 필수!
    )
    @Operation(
        summary = "생성",
    )
    fun cratePost(
        @RequestPart("data") postCrateRequestDto: PostCrateRequestDto,
        @RequestPart(value = "files", required = true) images: List<MultipartFile>,
    ){
        TODO()
    }

    @PatchMapping(
        value = ["/update"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE] // 필수!
    )
    @Operation(
        summary = "업데이트",
    )
    fun updatePost(
        @RequestPart postId:Int,
        @RequestPart("data") postUpdateRequestDto: PostUpdateRequestDto,
        @RequestPart(value = "files", required = true) files: List<MultipartFile>,

    ){
        TODO()
    }

    @DeleteMapping("delete")
    @Operation(
        summary = "삭제",
    )
    fun deletePost(
        @RequestPart postId:Int,
    ){
        TODO()
    }

    @PostMapping("/save")
    @Operation(
        summary = "저장 (찜)",
    )
    fun save(
        @RequestPart postId:Int,
    ){
        TODO()
    }

    @PostMapping("/like")
    @Operation(
        summary = "좋아요 +1",
    )
    fun like(
        @RequestPart postId:Int,
    ){
        TODO()
    }
}
