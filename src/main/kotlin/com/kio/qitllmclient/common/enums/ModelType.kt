package com.kio.qitllmclient.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ModelType(
    @get:JsonValue
    val type: String,
    val description: String
) {
    GPT_4("gpt-4", "GPT-4 모델"),
    OLLAMA("ollama", "Ollama 모델"),
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun of(type: String): ModelType {
            return ModelType.entries.firstOrNull { it.type == type }
                ?: throw IllegalArgumentException("유효하지 않은 ModelType 타입: $type")
        }
    }
}