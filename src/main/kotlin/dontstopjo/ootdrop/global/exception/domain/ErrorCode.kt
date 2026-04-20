package dontstopjo.ootdrop.global.exception.domain

enum class ErrorCode(
    val status: Int,
    val message: String
) {
    // Auth
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),

    FORBIDDEN_ACCESS(403, "해당 자원에 대한 접근 권한이 없습니다."),
    NOT_POST_OWNER(403, "해당 게시글의 수정/삭제 권한이 없습니다."),

    // Member / Common
    ID_NOT_FOUND(404, "id를 찾을 수 없습니다."),
    USER_NOT_FOUND(404, "user를 찾을 수 없습니다."),
    POST_NOT_FOUND(404, "post를 찾을 수 없습니다."),

    // Notification
    DUPLICATE_NOTIFICATION(409, "알람이 이미 등록되어 있습니다.")
}