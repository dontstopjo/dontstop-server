package dontstopjo.ootdrop.global.exception

import dontstopjo.ootdrop.global.exception.domain.ErrorCode
import dontstopjo.ootdrop.global.exception.domain.OOTDropException

class PostNotFoundException : OOTDropException(ErrorCode.POST_NOT_FOUND)
