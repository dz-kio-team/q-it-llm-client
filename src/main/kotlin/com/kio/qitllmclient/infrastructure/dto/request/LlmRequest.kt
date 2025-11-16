package com.kio.qitllmclient.infrastructure.dto.request

import com.kio.qitllmclient.common.enums.ModelType

data class LlmRequest(
    val prompt: List<LlmMessage>,
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val model: ModelType = ModelType.OLLAMA
)