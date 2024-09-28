package hackathon.diary.global.exception;

import hackathon.diary.domain.diary.exception.DiaryAccessDeniedException;
import hackathon.diary.domain.diary.exception.DiaryNotFoundException;
import hackathon.diary.domain.diary.exception.OpenAiApiException;
import hackathon.diary.domain.member.exception.MemberNotFoundException;
import hackathon.diary.global.exception.dto.ErrorCode;
import hackathon.diary.global.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    /*
     * HTTP 405 Exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.getStatus().value())
                .body(new ErrorResponse(ErrorCode.METHOD_NOT_ALLOWED));
    }

    /*
     * HTTP 404 NOT_FOUND
     */
    @ExceptionHandler(MemberNotFoundException.class)
    protected ResponseEntity<ErrorResponse> memberNotFoundException(final MemberNotFoundException e) {
        log.error("MemberNotFoundException: {}", e.getErrorCode());
        return ResponseEntity
                .status(e.getErrorCode().getStatus().value())
                .body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(DiaryNotFoundException.class)
    protected ResponseEntity<ErrorResponse> diaryNotFoundException(final DiaryNotFoundException e) {
        log.error("DiaryNotFoundException: {}", e.getErrorCode());
        return ResponseEntity
                .status(e.getErrorCode().getStatus().value())
                .body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(DiaryAccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> diaryAccessDeniedException(final DiaryAccessDeniedException e) {
        log.error("DiaryAccessDeniedException: {}", e.getErrorCode());
        return ResponseEntity
                .status(e.getErrorCode().getStatus().value())
                .body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(OpenAiApiException.class)
    protected ResponseEntity<ErrorResponse> handleOpenAiApiException(final OpenAiApiException e) {
        log.error("OpenAiApiException: {}", e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus().value())
                .body(new ErrorResponse(e.getErrorCode()));
    }
}
