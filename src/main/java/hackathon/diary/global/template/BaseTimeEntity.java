package hackathon.diary.global.template;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners( value = { AuditingEntityListener.class } )
public abstract class BaseTimeEntity {
    @CreatedDate
    @Column(name = "create_at")
    private LocalDateTime createAt;
}
