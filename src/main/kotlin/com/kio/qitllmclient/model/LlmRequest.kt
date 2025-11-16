package com.kio.qitllmclient.model

import com.kio.qitllmclient.model.enums.ModelType

data class LlmRequest(
    val prompt: List<LlmMessage>,
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val model: ModelType = ModelType.OLLAMA
)