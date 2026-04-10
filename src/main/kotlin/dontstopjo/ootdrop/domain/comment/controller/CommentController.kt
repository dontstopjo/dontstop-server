package dontstopjo.ootdrop.domain.comment.controller

import dontstopjo.ootdrop.domain.comment.service.CommentService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/comments")
@Tag(name = "COMMENT", description = "히히댓글")
class CommentController(
    private val commentService: CommentService,
) {
    @PostMapping("/{postId}")
    fun createComment(
        @PathVariable postId: Long,
        content: String,
        @AuthenticationPrincipal userId: Long
    ) {
        commentService.create(postId, content, userId)
    }
}