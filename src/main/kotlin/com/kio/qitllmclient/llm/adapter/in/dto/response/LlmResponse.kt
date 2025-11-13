package com.kio.qitllmclient.llm.adapter.`in`.dto.response

import com.kio.qitllmclient.common.enums.ModelType

data class LlmResponse(
    val content: String,
    val model: ModelType,
    val tokenUsed: Int? = null,
    val latencyMs: Long? = null
)