package srpingboot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class TestResult {

    @Schema(description = "조회 결과 메시지", name = "message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String message;
}
