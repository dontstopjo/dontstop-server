package dontstopjo.ootdrop.global.exception.domain

import dontstopjo.ootdrop.global.dto.ErrorMessageResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(OOTDropException::class)
    fun handleOOTDropException(ex: OOTDropException): ResponseEntity<ErrorMessageResponse> {
        val message = ex.errorCode.message
        return ResponseEntity.status(ex.errorCode.status).body(ErrorMessageResponse(message))
    }
}