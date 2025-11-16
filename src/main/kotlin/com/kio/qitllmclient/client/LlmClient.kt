package com.kio.qitllmclient.client

import com.kio.qitllmclient.model.LlmRequest
import com.kio.qitllmclient.model.LlmResponse

interface LlmClient {
    fun generate(request: LlmRequest): LlmResponse
}