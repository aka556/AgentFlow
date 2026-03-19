package org.xiaoyu.xchatmind.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import jakarta.servlet.http.HttpSession;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {
    @Bean
    public OpenAPI customOpenAPI(HttpSession httpSession) {
        return new OpenAPI()
                .info(new Info()
                        .title("XChatMind API")
                        .version("1.0.0")
                        .description("XChatMind API Documentation")
                        .termsOfService("https://doc.xiaominfo.com")
                        .contact(new Contact()
                                .name("xiaoyu"))
                        .license(new License().name("Apache 2.0")
                                .url("https://doc.xiaominfo.com"))
                );
    }

    @Bean
    public GroupedOpenApi agentApi() {
        return GroupedOpenApi.builder().group("Agent管理")
                .pathsToMatch("/api/agents/**")
                .build();
    }

    @Bean
    public GroupedOpenApi chatApi() {
        return GroupedOpenApi.builder().group("Chat管理")
                .pathsToMatch("/api/chat-messages/**")
                .build();
    }

    @Bean
    public GroupedOpenApi sessionApi() {
        return GroupedOpenApi.builder().group("Session管理")
                .pathsToMatch("/api/chat-sessions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi documentApi() {
        return GroupedOpenApi.builder().group("Document管理")
                .pathsToMatch("/api/documents/**")
                .build();
    }

    @Bean
    public GroupedOpenApi knowledgeBaseApi() {
        return GroupedOpenApi.builder().group("Knowledge Base管理")
                .pathsToMatch("/api/knowledge-bases/**")
                .build();
    }
}
