package com.kio.qitllmclient.model

import com.kio.qitllmclient.model.enums.ModelType

data class LlmResponse(
    val content: String,
    val model: ModelType,
    val tokenUsed: Int? = null,
    val latencyMs: Long? = null
)