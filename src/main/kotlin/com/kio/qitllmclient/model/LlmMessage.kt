package com.kio.qitllmclient.model

import com.kio.qitllmclient.model.enums.LlmMessageType

data class LlmMessage(
    val type: LlmMessageType,
    val prompt: String
) {
}