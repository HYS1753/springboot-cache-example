package srpingboot.application.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@OpenAPIDefinition(
        info = @Info(title = "srpingboot-cache-example",
                description = "springboot cache test API 명세서",
                version = "v1"))
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return OpenApi -> OpenApi.servers(getServersItem());
    }

    private List<Server> getServersItem() {
        List<Server> servers = new ArrayList<Server>();
        servers.add(new Server().url("/"));
        return servers;
    }

    @Bean
    public GroupedOpenApi searchApi() {

        return GroupedOpenApi.builder()
                .group("springboot cache test API v1")
                .addOpenApiCustomizer(openApiCustomizer())
                .pathsToMatch("/**")
                .build();
    }
}
