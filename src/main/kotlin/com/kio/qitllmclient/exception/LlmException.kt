package com.kio.qitllmclient.exception

import com.kio.qit.exception.BusinessLogicException
import com.kio.qit.exception.ErrorCode

/**
 * LLM Client에서 발생하는 비즈니스 로직 예외
 *
 * @param errorCode LLM 관련 에러 코드
 * @param message 커스텀 에러 메시지 (null이면 errorCode의 기본 메시지 사용)
 * @param additionalInfo LLM 관련 추가 정보 (모델명 등)
 */
open class LlmException(
    errorCode: ErrorCode,
    message: String? = null,
    val additionalInfo: Map<String, Any>? = null
) : BusinessLogicException(
    errorCode = errorCode,
    message = message ?: errorCode.message
) {

    constructor(
        errorCode: ErrorCode,
        modelType: String,
        message: String? = null,
    ) : this(
        errorCode = errorCode,
        message = message,
        additionalInfo = mutableMapOf("model" to modelType)
    )
}