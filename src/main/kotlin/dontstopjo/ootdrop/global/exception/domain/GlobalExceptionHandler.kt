package dontstopjo.ootdrop.global.exception.domain

import dontstopjo.ootdrop.global.dto.ErrorMessageResponse
import dontstopjo.ootdrop.global.exception.domain.OOTDropException
import org.springframework.http.HttpStatus
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
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorMessageResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorMessageResponse(ex.message ?: "internal server error"))
    }
}