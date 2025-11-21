package com.kio.qitllmclient.client

import com.kio.qit.exception.ErrorCode
import com.kio.qitllmclient.exception.LlmException
import com.kio.qitllmclient.model.LlmRequest
import com.kio.qitllmclient.model.LlmResponse
import io.github.oshai.kotlinlogging.KLogger
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * LlmClient 추상 클래스
 *
 * 모든 LLM 클라이언트 구현체에서 공통으로 사용하는 예외 처리, 로깅, 레이턴시를 제공합니다.
 *
 * ### 사용 방법
 * 1. 이 클래스를 상속받습니다
 * 2. `logger` 프로퍼티를 구현합니다
 * 3. `doGenerate()` 메서드를 구현합니다 (실제 LLM 호출 로직)
 *
 * ### 예시
 * ```kotlin
 * @Component
 * class OllamaClient(
 *     private val chatModel: OllamaChatModel
 * ) : AbstractLlmClient() {
 *     override val logger = KotlinLogging.logger {}
 *
 *     override fun <T> doGenerate(request: LlmRequest, contentType: Class<T>): T {
 *         // LLM 모델 호출 로직만 구현
 *     }
 * }
 * ```
 */
abstract class AbstractLlmClient : LlmClient {

    protected abstract val logger: KLogger

    /**
     * 이 메서드는 예외 처리, 로깅, 레이턴시 측정을 자동으로 수행하며,
     * 실제 LLM 호출은 `doGenerate()`를 통해 하위 클래스에서 구현합니다.
     */
    override fun <T> generate(request: LlmRequest, contentType: Class<T>): LlmResponse<T> {
        val startTime = System.currentTimeMillis()

        return try {
            logger.info { "LLM 요청 시작 - 모델: ${request.model}" }

            val content = doGenerate(request, contentType)
                ?: throw LlmException(
                    errorCode = ErrorCode.LLM_NULL_RESPONSE,
                    message = "LLM 응답이 null입니다. contentType: ${contentType.name}"
                )

            val latencyMs = System.currentTimeMillis() - startTime
            logger.info { "LLM 요청 완료 - 소요시간: ${latencyMs}ms" }

            LlmResponse(
                content = content,
                model = request.model,
                latencyMs = latencyMs
            )
        } catch (e: LlmException) {
            logger.error(e) { "LLM 예외 발생: ${e.message}" }
            throw e
        } catch (e: ResourceAccessException) {
            handleResourceAccessException(e, startTime)
        } catch (e: HttpClientErrorException) {
            handleHttpClientError(e, startTime)
        } catch (e: HttpServerErrorException) {
            handleHttpServerError(e, startTime)
        } catch (e: Exception) {
            handleUnexpectedException(e, startTime)
        }
    }

    /**
     * 실제 LLM 생성 로직을 구현하는 메서드
     *
     * 하위 클래스에서 이 메서드만 구현하면 예외 처리, 로깅 등은 자동으로 처리됩니다.
     *
     * @param request LLM 요청 객체
     * @param contentType 응답 타입
     * @return LLM 생성 결과 (null이면 안 됨)
     * @throws Exception 발생한 예외는 상위 클래스에서 처리
     */
    protected abstract fun <T> doGenerate(request: LlmRequest, contentType: Class<T>): T?

    /**
     * 리소스 접근 예외 처리 (타임아웃, 연결 실패 등)
     */
    private fun handleResourceAccessException(e: ResourceAccessException, startTime: Long): Nothing {
        val latencyMs = System.currentTimeMillis() - startTime
        val errorMessage = when (e.cause) {
            is SocketTimeoutException -> "LLM 서버 응답 타임아웃 (${latencyMs}ms 경과)"
            is ConnectException -> "LLM 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인하세요."
            else -> "LLM 서버 연결 중 오류가 발생했습니다: ${e.message}"
        }
        logger.error(e) { errorMessage }
        throw LlmException(
            errorCode = ErrorCode.LLM_API_CALL_FAILED,
            message = errorMessage,
            cause = e
        )
    }

    /**
     * HTTP 클라이언트 에러 처리 (4xx)
     */
    private fun handleHttpClientError(e: HttpClientErrorException, startTime: Long): Nothing {
        val latencyMs = System.currentTimeMillis() - startTime
        logger.error(e) { "LLM API 클라이언트 오류 - 상태 코드: ${e.statusCode}, 소요시간: ${latencyMs}ms" }
        throw LlmException(
            errorCode = ErrorCode.LLM_API_CALL_FAILED,
            message = "LLM API 클라이언트 오류 (${e.statusCode}): ${e.message}",
            cause = e
        )
    }

    /**
     * HTTP 서버 에러 처리 (5xx)
     */
    private fun handleHttpServerError(e: HttpServerErrorException, startTime: Long): Nothing {
        val latencyMs = System.currentTimeMillis() - startTime
        logger.error(e) { "LLM API 서버 오류 - 상태 코드: ${e.statusCode}, 소요시간: ${latencyMs}ms" }
        throw LlmException(
            errorCode = ErrorCode.LLM_API_CALL_FAILED,
            message = "LLM API 서버 오류 (${e.statusCode}): ${e.message}",
            cause = e
        )
    }

    /**
     * 예상치 못한 예외 처리
     */
    private fun handleUnexpectedException(e: Exception, startTime: Long): Nothing {
        val latencyMs = System.currentTimeMillis() - startTime
        logger.error(e) { "LLM 호출 중 예상치 못한 예외 발생 - 소요시간: ${latencyMs}ms" }
        throw LlmException(
            errorCode = ErrorCode.LLM_API_CALL_FAILED,
            message = "LLM API 호출 중 오류가 발생했습니다: ${e.message}",
            cause = e
        )
    }
}