package dontstopjo.ootdrop.global.exception

enum class ErrorCode(
    val status: Int,
    val message: String
) {
    // Auth
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
}
