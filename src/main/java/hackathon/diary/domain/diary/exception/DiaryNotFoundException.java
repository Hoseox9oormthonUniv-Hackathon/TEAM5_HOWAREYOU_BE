package hackathon.diary.domain.diary.exception;

import hackathon.diary.global.exception.dto.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryNotFoundException extends Throwable {
    private final ErrorCode errorCode;
}
