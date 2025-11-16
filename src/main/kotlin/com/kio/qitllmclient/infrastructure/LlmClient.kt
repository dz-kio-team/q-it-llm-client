package com.kio.qitllmclient.infrastructure

import com.kio.qitllmclient.infrastructure.dto.request.LlmRequest
import com.kio.qitllmclient.infrastructure.dto.response.LlmResponse

interface LlmClient {
    fun generate(request: LlmRequest): LlmResponse
}