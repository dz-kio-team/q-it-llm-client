package com.kio.qitllmclient.model

import com.kio.qit.enums.LlmMessageType

data class LlmMessage(
    val type: LlmMessageType,
    val prompt: String
)