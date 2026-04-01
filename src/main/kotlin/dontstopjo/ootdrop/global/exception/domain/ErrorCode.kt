package dontstopjo.ootdrop.global.exception.domain

enum class ErrorCode(
    val status: Int,
    val message: String
) {
    // Common
    ENUM_VALUE_NOT_FOUND(400, "enum 값을 찾을 수 없습니다."),

    // Member / Common
    ID_NOT_FOUND(404, "id를 찾을 수 없습니다."),

    USER_NOT_FOUND(404, "user를 찾을 수 없습니다."),

    // Auth
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),

    // Notification
    DUPLICATE_NOTIFICATION(409, "알람이 이미 등록되어 있습니다.")
}