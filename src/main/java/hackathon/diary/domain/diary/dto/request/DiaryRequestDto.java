package hackathon.diary.domain.diary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "일기 생성 | Request")
public record DiaryRequestDto(
        @NotBlank
        @Schema(description = "voiceText")
        @RequestPart
        String voiceText,
        @RequestPart(required = false) MultipartFile image
) {
}
