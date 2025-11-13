package com.kio.qitllmclient.llm.application.service

import com.kio.qitllmclient.llm.adapter.`in`.dto.request.LlmRequest
import com.kio.qitllmclient.llm.adapter.`in`.dto.response.LlmResponse

interface LlmClient {
    fun generate(request: LlmRequest): LlmResponse
}