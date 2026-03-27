package dontstopjo.ootdrop.global.exception

open class OOTDropException(
    val errorCode: ErrorCode
): RuntimeException()