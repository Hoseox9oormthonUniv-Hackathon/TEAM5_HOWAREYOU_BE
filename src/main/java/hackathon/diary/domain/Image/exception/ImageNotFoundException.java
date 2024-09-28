package hackathon.diary.domain.Image.exception;

import hackathon.diary.global.exception.dto.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageNotFoundException extends Exception {
    private final ErrorCode errorCode;
}
