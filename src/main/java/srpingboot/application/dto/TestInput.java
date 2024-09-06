package srpingboot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TestInput {

    @Schema(description = "조회 인자 1", name = "param1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String param1;

    @Schema(description = "조회 인자 2", name = "param2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String param2;
}