package com.kio.qitllmclient.ollama

import com.kio.qit.enums.LlmMessageType
import com.kio.qit.enums.ModelType
import com.kio.qit.exception.ErrorCode
import com.kio.qitllmclient.client.ollama.OllamaClient
import com.kio.qitllmclient.exception.LlmException
import com.kio.qitllmclient.model.LlmMessage
import com.kio.qitllmclient.model.LlmRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OllamaClientUnitTest {

    private val mockChatModel: OllamaChatModel = mockk(relaxed = true)
    private val ollamaClient = OllamaClient(mockChatModel)

    private fun createTestRequest(): LlmRequest {
        return LlmRequest(
            prompt = listOf(
                LlmMessage(
                    type = LlmMessageType.USER,
                    prompt = "테스트 질문"
                )
            ),
            model = ModelType.OLLAMA
        )
    }

    @Test
    fun `Ollama 서버 연결 실패 시 명확한 예외 메시지를 반환한다`() {
        // given
        val request = createTestRequest()

        every { mockChatModel.call(any(Prompt::class)) } throws ResourceAccessException(
            "Connection refused",
            ConnectException("Connection refused")
        )

        // when
        val exception = assertThrows<LlmException> {
            ollamaClient.generate(request, String::class.java)
        }

        // then
        Assertions.assertAll(
            { assertEquals(ErrorCode.LLM_API_CALL_FAILED, exception.errorCode) },
            { assertEquals("LLM 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인하세요.", exception.message) },
            { assertEquals(ModelType.OLLAMA.type, exception.additionalInfo?.get("model")) }
        )
        println("예외 메시지: ${exception.message}")
    }

    @Test
    fun `Ollama 서버 응답 타임아웃 시 타임아웃 메시지를 반환한다`() {
        // given
        val request = createTestRequest()

        every { mockChatModel.call(any(Prompt::class)) } throws ResourceAccessException(
            "Read timed out",
            SocketTimeoutException("Read timed out")
        )

        // when
        val exception = assertThrows<LlmException> {
            ollamaClient.generate(request, String::class.java)
        }

        // then
        Assertions.assertAll(
            { assertEquals(ErrorCode.LLM_API_CALL_FAILED, exception.errorCode) },
            { assertTrue(exception.message!!.startsWith("LLM 서버 응답 타임아웃 (")) },
            { assertTrue(exception.message!!.endsWith("ms 경과)")) },
            { assertEquals(ModelType.OLLAMA.type, exception.additionalInfo?.get("model")) }
        )
        println("예외 메시지: ${exception.message}")
    }

    @Test
    fun `HTTP 400 에러 발생 시 클라이언트 오류 메시지를 반환한다`() {
        // given
        val request = createTestRequest()

        val exepctedModel = request.model.type
        val expectedStatus = HttpStatus.BAD_REQUEST
        val expectedMessage = "${expectedStatus.value()} ${expectedStatus.name}"

        every { mockChatModel.call(any(Prompt::class)) } throws HttpClientErrorException(
            expectedStatus
        )

        // when
        val exception = assertThrows<LlmException> {
            ollamaClient.generate(request, String::class.java)
        }

        // then
        Assertions.assertAll(
            { assertEquals(ErrorCode.LLM_API_CALL_FAILED, exception.errorCode) },
            { assertEquals("LLM API 클라이언트 오류 ($expectedStatus): $expectedMessage", exception.message) },
            { assertEquals(exepctedModel, exception.additionalInfo?.get("model")) }
        )
        println("예외 메시지: ${exception.message}")
    }

    @Test
    fun `HTTP 404 에러 발생 시 클라이언트 오류 메시지를 반환한다`() {
        // given
        val request = createTestRequest()
        val exepctedModel = request.model.type
        val expectedStatus = HttpStatus.NOT_FOUND
        val expectedMessage = "${expectedStatus.value()} ${expectedStatus.name}"

        every { mockChatModel.call(any(Prompt::class)) } throws HttpClientErrorException(
            expectedStatus
        )

        // when
        val exception = assertThrows<LlmException> {
            ollamaClient.generate(request, String::class.java)
        }

        // then
        Assertions.assertAll(
            { assertEquals(ErrorCode.LLM_API_CALL_FAILED, exception.errorCode) },
            { assertEquals("LLM API 클라이언트 오류 ($expectedStatus): $expectedMessage", exception.message) },
            { assertEquals(exepctedModel, exception.additionalInfo?.get("model")) }
        )
        println("예외 메시지: ${exception.message}")
    }

    @Test
    fun `HTTP 500 에러 발생 시 서버 오류 메시지를 반환한다`() {
        // given
        val request = createTestRequest()

        val exepctedModel = request.model.type
        val expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR
        val expectedMessage = "${expectedStatus.value()} ${expectedStatus.name}"

        every { mockChatModel.call(any(Prompt::class)) } throws HttpServerErrorException(
            expectedStatus
        )

        // when
        val exception = assertThrows<LlmException> {
            ollamaClient.generate(request, String::class.java)
        }

        // then
        Assertions.assertAll(
            { assertEquals(ErrorCode.LLM_API_CALL_FAILED, exception.errorCode) },
            { assertEquals("LLM API 서버 오류 ($expectedStatus): $expectedMessage", exception.message) },
            { assertEquals(exepctedModel, exception.additionalInfo?.get("model")) }
        )
        println("예외 메시지: ${exception.message}")
    }

    @Test
    fun `HTTP 503 에러 발생 시 서버 오류 메시지를 반환한다`() {
        // given
        val request = createTestRequest()
        val expectedModel = request.model.type
        val expectedStatus = HttpStatus.SERVICE_UNAVAILABLE
        val expectedMessage = "${expectedStatus.value()} ${expectedStatus.name}"

        every { mockChatModel.call(any(Prompt::class)) } throws HttpServerErrorException(
            expectedStatus
        )

        // when
        val exception = assertThrows<LlmException> {
            ollamaClient.generate(request, String::class.java)
        }

        // then
        Assertions.assertAll(
            { assertEquals(ErrorCode.LLM_API_CALL_FAILED, exception.errorCode) },
            { assertEquals("LLM API 서버 오류 ($expectedStatus): $expectedMessage", exception.message) },
            { assertEquals(expectedModel, exception.additionalInfo?.get("model")) }
        )
        println("예외 메시지: ${exception.message}")
    }

    @Test
    fun `LLM 응답이 null인 경우 NULL_RESPONSE 예외를 발생시킨다`() {
        // given
        val request = createTestRequest()
        val expectedModel = request.model.type
        val expectedContentType = InterviewQuestionsResponse::class.java

        // ChatResponse를 반환하지만 결과가 비어있어 entity 변환 시 null이 되는 상황 시뮬레이션
        every { mockChatModel.call(any(Prompt::class)) } returns ChatResponse(emptyList())

        // when
        val exception = assertThrows<LlmException> {
            ollamaClient.generate(request, expectedContentType)
        }

        // then
        Assertions.assertAll(
            { assertEquals(ErrorCode.LLM_NULL_RESPONSE, exception.errorCode) },
            { assertEquals("LLM 응답이 null입니다. contentType: ${expectedContentType.name}", exception.message) },
            { assertEquals(expectedModel, exception.additionalInfo?.get("model")) }
        )
        println("예외 메시지: ${exception.message}")
    }

    @Test
    fun `예상치 못한 예외 발생 기본 예외 메시지를 반환한다`() {
        // given
        val request = createTestRequest()
        val expectedModel = request.model.type

        every { mockChatModel.call(any(Prompt::class)) } throws RuntimeException("Unexpected error")

        // when
        val exception = assertThrows<LlmException> {
            ollamaClient.generate(request, String::class.java)
        }

        // then
        Assertions.assertAll(
            { assertEquals(ErrorCode.LLM_API_CALL_FAILED, exception.errorCode) },
            { assertEquals(ErrorCode.LLM_API_CALL_FAILED.message, exception.message) },
            { assertEquals(expectedModel, exception.additionalInfo?.get("model")) }
        )
        println("예외 메시지: ${exception.message}")
    }
}