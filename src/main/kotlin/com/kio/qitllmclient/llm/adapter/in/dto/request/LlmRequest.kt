package com.kio.qitllmclient.llm.adapter.`in`.dto.request

import com.kio.qitllmclient.common.enums.ModelType

data class LlmRequest(
    val prompt: List<LlmMessage>,
    val temperature: Double = 0.7,
    val maxTokens: Int = 512,
    val model: ModelType = ModelType.OLLAMA
)