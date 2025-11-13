package com.kio.qitllmclient.llm.adapter.`in`.dto.request

import com.kio.qitllmclient.common.enums.LlmMessageType

data class LlmMessage(
    val type: LlmMessageType,
    val prompt: String
) {
}