package hackathon.diary.global.oauth.exception;

import hackathon.diary.global.exception.dto.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class OAuthException extends Exception {
    private final ErrorCode errorCode;
}
