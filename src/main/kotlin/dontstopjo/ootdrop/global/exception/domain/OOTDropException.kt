package dontstopjo.ootdrop.global.exception.domain

open class OOTDropException(
    val errorCode: ErrorCode
): RuntimeException()