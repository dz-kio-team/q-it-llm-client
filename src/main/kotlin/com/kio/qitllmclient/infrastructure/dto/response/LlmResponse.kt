package com.kio.qitllmclient.infrastructure.dto.response

import com.kio.qitllmclient.common.enums.ModelType

data class LlmResponse(
    val content: String,
    val model: ModelType,
    val tokenUsed: Int? = null,
    val latencyMs: Long? = null
)