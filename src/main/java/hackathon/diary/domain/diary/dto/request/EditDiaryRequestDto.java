package hackathon.diary.domain.diary.dto.request;

public record EditDiaryRequestDto(
        Long diaryId,
        String title,
        String content
) {
}
