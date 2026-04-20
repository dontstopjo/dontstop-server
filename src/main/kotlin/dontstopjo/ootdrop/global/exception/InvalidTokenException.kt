package dontstopjo.ootdrop.global.exception

import dontstopjo.ootdrop.global.exception.domain.ErrorCode
import dontstopjo.ootdrop.global.exception.domain.OOTDropException

class InvalidTokenException : OOTDropException(ErrorCode.INVALID_TOKEN)
