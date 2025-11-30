package com.kio.qitllmclient.client

import com.kio.qit.enums.ModelType
import com.kio.qit.exception.ErrorCode
import com.kio.qitllmclient.client.ollama.OllamaClient
import com.kio.qitllmclient.exception.LlmException

/**
 * LLM 클라이언트 팩토리
 *
 * ModelType에 따라 적절한 LlmClient 구현체를 반환합니다.
 * OllamaClientConfig에서 빈으로 등록됩니다.
 */
class LlmClientFactory(
    private val ollamaClient: OllamaClient?
) {
    fun getClient(modelType: ModelType): LlmClient = when (modelType) {
        ModelType.OLLAMA -> ollamaClient ?: throw LlmException(
            errorCode = ErrorCode.LLM_UNSUPPORTED_MODEL,
            message = "Ollama 클라이언트를 사용할 수 없습니다. Spring AI Ollama 의존성을 확인해주세요."
        )
        ModelType.GPT_4 -> throw LlmException(
            errorCode = ErrorCode.LLM_UNSUPPORTED_MODEL,
            message = "지원하지 않는 LLM 모델: ${modelType.type}"
        )
    }
}