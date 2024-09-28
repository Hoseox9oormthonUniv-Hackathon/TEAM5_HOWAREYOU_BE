package hackathon.diary.global.utils;

import hackathon.diary.domain.member.domain.entity.Member;
import hackathon.diary.domain.member.domain.repository.MemberRepository;
import hackathon.diary.domain.member.exception.MemberNotFoundException;
import hackathon.diary.global.exception.dto.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GlobalUtil {
    private final MemberRepository memberRepository;

    public Member findByMemberWithEmail(String email) throws MemberNotFoundException {
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
