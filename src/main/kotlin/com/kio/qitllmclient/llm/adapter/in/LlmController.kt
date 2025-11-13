package com.kio.qitllmclient.llm.adapter.`in`

import com.kio.qitllmclient.llm.adapter.`in`.dto.request.LlmRequest
import com.kio.qitllmclient.llm.adapter.`in`.dto.response.LlmResponse
import com.kio.qitllmclient.llm.application.service.factory.LlmClientFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/llm")
class LlmController(
    private val clientFactory: LlmClientFactory
) {
    @PostMapping()
    fun chat(@RequestBody request: LlmRequest): LlmResponse {
        val llmClient = clientFactory.getClient(request.model)
        return llmClient.generate(request)
    }
}