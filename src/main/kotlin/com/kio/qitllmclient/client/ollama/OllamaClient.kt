package com.kio.qitllmclient.client.ollama

import com.kio.qitllmclient.model.enums.LlmMessageType
import com.kio.qitllmclient.client.LlmClient
import com.kio.qitllmclient.model.LlmRequest
import com.kio.qitllmclient.model.LlmResponse
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.stereotype.Component

@Component
class OllamaClient(
    private val chatModel: OllamaChatModel
): LlmClient {
    override fun generate(request: LlmRequest): LlmResponse {
        val startTime = System.currentTimeMillis()
        val prompt = buildPrompt(request)
        val content = ChatClient.create(chatModel)
            .prompt(prompt)
            .call()
            .content()
        val latencyMs = System.currentTimeMillis() - startTime
        return LlmResponse(
            content = content.toString(),
            model = request.model,
            latencyMs = latencyMs
        )
    }

    private fun buildPrompt(request: LlmRequest): Prompt {
        val messages = request.prompt.map {
            when(it.type) {
                LlmMessageType.USER -> UserMessage(it.prompt)
                LlmMessageType.SYSTEM -> SystemMessage(it.prompt)
                LlmMessageType.ASSISTANT -> AssistantMessage(it.prompt)
            }
        }
        return Prompt.builder()
            .messages(messages)
            .build()
    }
}