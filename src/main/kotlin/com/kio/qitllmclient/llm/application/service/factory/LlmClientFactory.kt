package com.kio.qitllmclient.llm.application.service.factory

import com.kio.qitllmclient.common.enums.ModelType
import com.kio.qitllmclient.llm.application.service.LlmClient
import com.kio.qitllmclient.llm.application.service.OllamaClient
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