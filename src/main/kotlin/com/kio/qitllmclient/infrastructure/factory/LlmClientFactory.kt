package com.kio.qitllmclient.infrastructure.factory

import com.kio.qitllmclient.common.enums.ModelType
import com.kio.qitllmclient.infrastructure.LlmClient
import com.kio.qitllmclient.infrastructure.ollama.OllamaClient
import org.springframework.stereotype.Component

@Component
class LlmClientFactory(
    private val ollamaClient: OllamaClient
) {
    fun getClient(modelType: ModelType): LlmClient = when (modelType) {
        ModelType.OLLAMA -> ollamaClient
        else -> ollamaClient
    }
}