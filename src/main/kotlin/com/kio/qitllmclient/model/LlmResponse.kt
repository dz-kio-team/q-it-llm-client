package com.kio.qitllmclient.model

import com.kio.qit.enums.ModelType

data class LlmResponse<T>(
    val content: T,
    val model: ModelType,
    val tokenUsed: Int? = null,
    val latencyMs: Long? = null
)