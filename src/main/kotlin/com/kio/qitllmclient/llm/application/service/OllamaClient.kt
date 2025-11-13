package com.kio.qitllmclient.llm.application.service

import com.kio.qit.annotation.UseCase
import com.kio.qitllmclient.llm.adapter.`in`.dto.request.LlmRequest
import com.kio.qitllmclient.llm.adapter.`in`.dto.response.LlmResponse
import org.springframework.ai.ollama.OllamaChatModel

@UseCase
class OllamaClient(
    private val chatModel: OllamaChatModel
): LlmClient {
    override fun generate(request: LlmRequest): LlmResponse {
        TODO("ollama client 구현")
    }
}