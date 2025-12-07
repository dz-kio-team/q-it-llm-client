package com.kio.qitllmclient.annotation

import com.kio.qitllmclient.config.LlmSharedAutoConfig
import org.springframework.context.annotation.Import

/**
 * EnableLlmLibrary 어노테이션은 라이브러리의 LLM 관련 빈을 활성화하는 데 사용됩니다.
 * 이 어노테이션을 사용하면 LlmSharedAutoConfig에 정의된 스프링 빈들이 애플리케이션 컨텍스트에 등록됩니다.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(LlmSharedAutoConfig::class)
annotation class EnableLlmLibrary()
