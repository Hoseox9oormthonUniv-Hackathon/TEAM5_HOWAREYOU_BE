package hackathon.diary.domain.member.exception;

import hackathon.diary.global.exception.dto.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class MemberNotFoundException extends Exception {
    private final ErrorCode errorCode;
}
