package com.kio.qitllmclient.client.ollama

import com.kio.qit.enums.LlmMessageType
import com.kio.qitllmclient.client.AbstractLlmClient
import com.kio.qitllmclient.model.LlmRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.OllamaChatModel

/**
 * Ollama LLM 클라이언트 구현체
 *
 * Spring AI의 OllamaChatModel을 사용하여 Ollama 서버와 통신합니다.
 * 예외 처리, 로깅, 레이턴시 측정은 AbstractLlmClient에서 자동으로 처리됩니다.
 */
class OllamaClient(
    private val chatModel: OllamaChatModel
) : AbstractLlmClient() {

    override val logger = KotlinLogging.logger {}

    /**
     * Ollama LLM 호출을 수행합니다.
     */
    override fun <T> doGenerate(request: LlmRequest, contentType: Class<T>): T? {
        val prompt = buildPrompt(request)
        return ChatClient.create(chatModel)
            .prompt(prompt)
            .call()
            .entity(contentType)
    }
    
    private fun buildPrompt(request: LlmRequest): Prompt {
        val messages = request.prompt.map {
            when (it.type) {
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