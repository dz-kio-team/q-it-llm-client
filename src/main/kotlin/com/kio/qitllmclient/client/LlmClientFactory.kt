package com.kio.qitllmclient.client

import com.kio.qit.enums.ModelType
import com.kio.qit.exception.ErrorCode
import com.kio.qitllmclient.client.ollama.OllamaClient
import com.kio.qitllmclient.exception.LlmException
import org.springframework.stereotype.Component

@Component
class LlmClientFactory(
    private val ollamaClient: OllamaClient
) {
    fun getClient(modelType: ModelType): LlmClient = when (modelType) {
        ModelType.OLLAMA -> ollamaClient
        ModelType.GPT_4 -> throw LlmException(
            errorCode = ErrorCode.LLM_UNSUPPORTED_MODEL,
            message = "지원하지 않는 LLM 모델: ${modelType.type}"
        )
    }
}