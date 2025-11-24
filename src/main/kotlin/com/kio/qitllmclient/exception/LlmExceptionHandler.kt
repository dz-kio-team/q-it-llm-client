package com.kio.qitllmclient.exception

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Order(Ordered.HIGHEST_PRECEDENCE)  // GlobalExceptionHandler보다 우선순위 높음
@ConditionalOnWebApplication    // 웹 애플리케이션에서만 활성화 (Batch 서비스 등에서는 비활성화)
@RestControllerAdvice
class LlmExceptionHandler {

    @ExceptionHandler(LlmException::class)
    fun handleLlmException(ex: LlmException):
            ResponseEntity<Map<String, Any?>> {
        val response = mutableMapOf<String, Any?>(
            "message" to (ex.message ?: ex.errorCode.message),
            "errorCode" to ex.errorCode.name
        )

        // LLM 관련 추가 정보가 있으면 포함
        ex.additionalInfo?.let { info ->
            response["additionalInfo"] = info
        }

        return ResponseEntity
            .status(HttpStatus.valueOf(ex.errorCode.code))
            .body(response)
    }

}