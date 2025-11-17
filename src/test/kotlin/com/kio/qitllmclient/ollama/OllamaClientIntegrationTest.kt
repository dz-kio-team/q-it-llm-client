package com.kio.qitllmclient.ollama

import com.kio.qitllmclient.TestApplication
import com.kio.qitllmclient.model.enums.LlmMessageType
import com.kio.qitllmclient.model.enums.ModelType
import com.kio.qitllmclient.model.LlmMessage
import com.kio.qitllmclient.model.LlmRequest
import com.kio.qitllmclient.client.LlmClientFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.net.URL
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(classes = [TestApplication::class])
class OllamaClientIntegrationTest {

    @Autowired
    lateinit var llmClientFactory: LlmClientFactory

    @Value("\${spring.ai.ollama.base-url:}")
    lateinit var ollamaBaseUrl: String

    @Test
    fun `Ollama 서버로 간단한 질문을 보내고 응답을 받는다`() {
        // 서버 실행 여부 확인
        if (!isOllamaRunning()) {
            println("⚠️ Ollama 서버가 실행 중이 아니므로 테스트를 스킵합니다.")
            return
        }

        // given
        val request = LlmRequest(
            prompt = listOf(
                LlmMessage(
                    type = LlmMessageType.SYSTEM,
                    prompt =
                    """
                        당신은 기술 면접 전문가입니다.
                        특정 기술이나 개념에 대해 깊이 있는 
                        이해도를 평가할 수 있는 면접 질문을 생성합니다.
                        
                        질문 생성 시 고려사항:
                        1. 이론적 지식과 실무 경험을 모두 평가할 수 있어야 합니다.
                        2. 일반적인 질문보다는 실제 문제 상황을 제시하는 질문을 선호합니다.
                        3. 단순 암기가 아닌 개념 이해도를 측정할 수 있어야 합니다.
                        4. 질문은 한국어로 작성합니다.
                        5. 각 질문마다 예상 답변의 핵심 포인트를 포함합니다.
                    """.trimIndent()
                ),
                LlmMessage(
                    type = LlmMessageType.USER,
                    prompt = "3년차 백엔드 개발자에게 적합한 기술 면접 질문 5개를 생성해줘."
                )
            ),
            model = ModelType.OLLAMA
        )

        // when
        val client = llmClientFactory.getClient(ModelType.OLLAMA)
        val response = client.generate(request)

        // then
        Assertions.assertAll(
            { assertNotNull(response) },
            { assertNotNull(response.content) },
            { assertTrue(response.content.isNotEmpty()) },
            { assertNotNull(response.latencyMs) },
            { assertTrue(response.latencyMs!! > 0) }
        )

        println("=== Ollama 응답 ===")
        println("모델: ${response.model}")
        println("응답 내용: ${response.content}")
        println("응답 시간: ${response.latencyMs}ms")
    }

    private fun isOllamaRunning(): Boolean {
        return try {
            URL(ollamaBaseUrl).openConnection().getInputStream().use { true }
        } catch (e: Exception) {
            false
        }
    }
}