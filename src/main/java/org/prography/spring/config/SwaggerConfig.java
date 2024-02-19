package org.prography.spring.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "탁구 게임 서비스 API 명세서",
                description = " 탁구 게임 명세서입니다.",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi userApi() {
        String[] paths = {"/user/**"};
        return GroupedOpenApi.builder()
                .group("유저(User) API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi teamApi() {
        String[] paths = {"/team/**"};
        return GroupedOpenApi.builder()
                .group("팀 변경 API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi serverStatusApi() {
        String[] paths = {"/health/**"};
        return GroupedOpenApi.builder()
                .group("헬스 체크(서버 상태 체크) API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi roomApi() {
        String[] paths = {"/room/**"};
        return GroupedOpenApi.builder()
                .group("방(Room) API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi initializationApi() {
        String[] paths = {"/init/**"};
        return GroupedOpenApi.builder()
                .group("초기화 API")
                .pathsToMatch(paths)
                .build();
    }
}
