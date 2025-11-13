package com.kio.qitllmclient.common.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class LlmMessageType(
    val type: String,
    val description: String
) {
    SYSTEM("system", "시스템 메시지"),
    USER("user", "사용자 메시지"),
    ASSISTANT("assistant", "어시스턴트 메시지");

    companion object {
        @JsonCreator
        @JvmStatic
        fun of(type: String): LlmMessageType {
            return LlmMessageType.entries.firstOrNull { it.type == type }
                ?: throw IllegalArgumentException("유효하지 않은 LlmMessageType 타입: $type")
        }
    }
}