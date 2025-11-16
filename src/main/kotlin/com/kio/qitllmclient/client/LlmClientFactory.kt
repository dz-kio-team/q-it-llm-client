package com.kio.qitllmclient.client

import com.kio.qitllmclient.model.enums.ModelType
import com.kio.qitllmclient.client.ollama.OllamaClient
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