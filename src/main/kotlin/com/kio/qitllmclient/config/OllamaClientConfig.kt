package com.kio.qitllmclient.config

import com.kio.qitllmclient.client.LlmClientFactory
import com.kio.qitllmclient.client.ollama.OllamaClient
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Ollama LLM 클라이언트 설정 클래스
 * OllamaChatModel이 있을 때만 활성화됩니다.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OllamaChatModel::class)
class OllamaClientConfig {

    @Bean
    @ConditionalOnMissingBean
    fun ollamaClient(chatModel: OllamaChatModel): OllamaClient {
        return OllamaClient(chatModel)
    }

    @Bean
    @ConditionalOnMissingBean
    fun llmClientFactory(ollamaClient: OllamaClient?): LlmClientFactory {
        return LlmClientFactory(ollamaClient)
    }
}
