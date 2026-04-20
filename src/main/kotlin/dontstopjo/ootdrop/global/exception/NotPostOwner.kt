package dontstopjo.ootdrop.global.exception

import dontstopjo.ootdrop.global.exception.domain.ErrorCode
import dontstopjo.ootdrop.global.exception.domain.OOTDropException

class NotPostOwner : OOTDropException(ErrorCode.NOT_POST_OWNER)