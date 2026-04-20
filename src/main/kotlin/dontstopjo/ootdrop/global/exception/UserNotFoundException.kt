package dontstopjo.ootdrop.global.exception

import dontstopjo.ootdrop.global.exception.domain.ErrorCode
import dontstopjo.ootdrop.global.exception.domain.OOTDropException

class UserNotFoundException : OOTDropException(ErrorCode.USER_NOT_FOUND)
