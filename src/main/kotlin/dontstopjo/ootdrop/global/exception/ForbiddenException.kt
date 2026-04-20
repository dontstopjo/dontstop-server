package dontstopjo.ootdrop.global.exception

import dontstopjo.ootdrop.global.exception.domain.ErrorCode
import dontstopjo.ootdrop.global.exception.domain.OOTDropException

class ForbiddenException : OOTDropException(ErrorCode.FORBIDDEN_ACCESS)