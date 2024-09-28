package hackathon.diary.domain.member.dto.response;

import hackathon.diary.domain.member.domain.entity.Member;
import lombok.Builder;

@Builder
public record MemberInfoResponseDto(String name) {
    public static MemberInfoResponseDto from(Member member) {
        return MemberInfoResponseDto.builder()
                .name(member.getName())
                .build();
    }
}
