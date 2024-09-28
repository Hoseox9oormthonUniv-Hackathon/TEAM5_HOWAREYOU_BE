package hackathon.diary.domain.auth.exception;

import hackathon.diary.global.exception.dto.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class InvalidTokenException extends Throwable {
    private final ErrorCode errorCode;
}
