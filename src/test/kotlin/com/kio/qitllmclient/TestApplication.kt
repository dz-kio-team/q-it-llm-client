package com.kio.qitllmclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

/**
 * 테스트 전용 Spring Boot Application 클래스
 * 라이브러리 프로젝트이므로 실제 main Application은 없지만,
 * 통합 테스트를 위해 Spring Context를 로드하기 위한 클래스
 *
 * DataSource 자동 설정 제외: 이 프로젝트는 데이터베이스를 사용하지 않음
 */
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class TestApplication

fun main(args: Array<String>) {
    runApplication<TestApplication>(*args)
}