package com.kio.qitllmclient.ollama

import com.kio.qitllmclient.TestApplication
import com.kio.qitllmclient.client.LlmClientFactory
import com.kio.qitllmclient.model.LlmMessage
import com.kio.qitllmclient.model.LlmRequest
import com.kio.qitllmclient.model.enums.LlmMessageType
import com.kio.qitllmclient.model.enums.ModelType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(classes = [TestApplication::class])
class OllamaClientIntegrationTest {

    @Autowired
    lateinit var llmClientFactory: LlmClientFactory

    @Value("\${spring.ai.ollama.base-url:}")
    lateinit var ollamaBaseUrl: String

    @ParameterizedTest
    @ValueSource(ints = [1, 3, 5])
    fun `요청 질문 개수에 맞춰 Ollama 서버로 면접 질문을 생성하고 응답을 받는다`(questionCount: Int) {
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
                        특정 기술이나 개념에 대해 깊이 있는 이해도를 평가할 수 있는 면접 질문을 생성합니다.

                        반드시 다음 JSON 형식으로만 응답하세요:
                        {
                          "questions": [
                            {
                              "question": "면접 질문 내용",
                              "keyPoint": "이 질문의 의도와 평가하고자 하는 핵심 포인트를 상세히 해설"
                            }
                          ]
                        }

                        질문 생성 시 고려사항:
                        1. 이론적 지식과 실무 경험을 모두 평가할 수 있어야 합니다.
                        2. 일반적인 질문보다는 실제 문제 상황을 제시하는 질문을 선호합니다.
                        3. 단순 암기가 아닌 개념 이해도를 측정할 수 있어야 합니다.
                        4. 질문은 한국어로 작성합니다.
                        5. 각 질문마다 예상 답변의 핵심 포인트를 문장 형태로 포함합니다.

                        **keyPoint 작성 지침 (중요):**
                        - 단순 키워드 나열이 아닌, 2-3문장의 상세한 해설을 작성하세요.
                        - 이 질문이 "무엇을" 평가하려는지 명확히 설명하세요.
                        - 어떤 개념, 기술, 경험을 확인하고자 하는지 구체적으로 서술하세요.
                        - 예상 답변에서 나와야 할 핵심 요소들을 포함하세요.

                        예시:
                        - 나쁜 예: "트랜잭션 관리, 동시성 제어"
                        - 좋은 예: "이 질문은 데이터베이스 트랜잭션의 ACID 특성에 대한 이해도를 평가하며, 특히 동시성 제어 상황에서 발생할 수 있는 문제점(Dirty Read, Non-Repeatable Read 등)과 이를 해결하기 위한 격리 수준(Isolation Level) 설정 경험을 확인합니다. 또한 실무에서 성능과 데이터 정합성 사이의 트레이드오프를 어떻게 고려하는지 평가합니다."

                        주의: 반드시 유효한 JSON 형식으로만 응답하고, 추가 설명은 포함하지 마세요.
                    """.trimIndent()
                ),
                LlmMessage(
                    type = LlmMessageType.USER,
                    prompt = "3년차 백엔드 개발자에게 적합한 기술 면접 질문 ${questionCount}개를 생성해줘."
                )
            ),
            model = ModelType.OLLAMA
        )

        // when
        val client = llmClientFactory.getClient(ModelType.OLLAMA)
        val response = client.generate(request, InterviewQuestionsResponse::class.java)

        // then
        Assertions.assertAll(
            { assertNotNull(response) },
            { assertNotNull(response.content) },
            { assertNotNull(response.content.questions) },
            { assertTrue(response.content.questions.isNotEmpty()) },
            { assertEquals(response.content.questions.size, questionCount) },
            {
                response.content.questions.forEach { question ->
                    assertNotNull(question.question)
                    assertTrue(question.question.isNotEmpty())
                    assertNotNull(question.keyPoint)
                    assertTrue(question.keyPoint.isNotEmpty())
                }
            },
            { assertNotNull(response.latencyMs) },
            { assertTrue(response.latencyMs!! > 0) }
        )

        println("=== LLM 모델 응답 ===")
        println("모델: ${response.model}")
        println("응답 시간: ${response.latencyMs}ms")
        println("\n생성된 면접 질문 (총 ${response.content.questions.size}개):\n")
        response.content.questions.forEachIndexed { index, question ->
            println("${index + 1}. ${question.question}")
            println("   핵심 포인트: ${question.keyPoint}")
            println()
        }
    }

    private fun isOllamaRunning(): Boolean {
        return try {
            URL(ollamaBaseUrl).openConnection().getInputStream().use { true }
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * 면접 질문 응답 객체
 */
data class InterviewQuestionsResponse(
    val questions: List<InterviewQuestion>
)

/**
 * 면접 질문
 */
data class InterviewQuestion(
    val question: String,
    val keyPoint: String
)