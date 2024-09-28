package hackathon.diary.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseTemplate<T> {
    int statusCode;
    String message;
    T data;

    public ResponseTemplate(HttpStatus httpStatus, String message, T data) {
        this.statusCode = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    public ResponseTemplate(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value();
        this.message = message;
    }
}
