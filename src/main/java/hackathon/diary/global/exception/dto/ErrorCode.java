package hackathon.diary.global.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /*
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    OAUTH_BAD_REQUEST(HttpStatus.BAD_REQUEST, "OAuth 요청이 잘못되었습니다."),

    /*
     * 401 UNAUTHORIZED: 인가되지 않음.
     */
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "토큰 에러입니다."),

    DIARY_ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "접근할 수 없는 일기입니다."),

    /*
     * 404 NOT_FOUND: 존재하지 않음.
     */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "일기 정보를 찾을 수 없습니다."),

    /*
     * 405 METHOD_NOT_ALLOWED: 허용되지 않은 Request Method 호출
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),

    /*
     * 500 INTERNAL_SERVER_ERROR: 내부 서버 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다."),
    JSON_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 처리 중 오류가 발생했습니다."),
    /*
     * 502 BAD_GATEWAY: 외부 API 호출 오류
     */
    OPENAI_API_ERROR(HttpStatus.BAD_GATEWAY, "OpenAI API 호출 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
