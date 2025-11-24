package com.kio.qitllmclient.client

import com.kio.qitllmclient.model.LlmRequest
import com.kio.qitllmclient.model.LlmResponse

interface LlmClient {
    fun <T> generate(request: LlmRequest, contentType: Class<T>): LlmResponse<T>
}