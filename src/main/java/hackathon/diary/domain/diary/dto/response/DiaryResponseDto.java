package hackathon.diary.domain.diary.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DiaryResponseDto(
        Long diaryId,
        String title,
        String content,
        String imageUrl,
        Boolean isShared,
        LocalDateTime createAt,
        String writer
) {
}
