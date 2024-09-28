package hackathon.diary.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "액세스 토큰 재발급 | Request")
public class ReIssueRequestDto {
    @NotBlank
    @Schema(description = "RefreshToken")
    private String refreshToken;
}
