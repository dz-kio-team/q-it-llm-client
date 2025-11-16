package com.kio.qitllmclient.infrastructure.dto.request

import com.kio.qitllmclient.common.enums.LlmMessageType

data class LlmMessage(
    val type: LlmMessageType,
    val prompt: String
) {
}