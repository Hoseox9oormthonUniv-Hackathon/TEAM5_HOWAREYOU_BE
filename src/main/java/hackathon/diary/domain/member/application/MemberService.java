package hackathon.diary.domain.member.application;

import hackathon.diary.domain.member.domain.entity.Member;
import hackathon.diary.domain.member.dto.response.MemberInfoResponseDto;
import hackathon.diary.domain.member.exception.MemberNotFoundException;
import hackathon.diary.global.utils.GlobalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final GlobalUtil globalUtil;
    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMemberInfo(String email) throws MemberNotFoundException {
        Member member = globalUtil.findByMemberWithEmail(email);
        return MemberInfoResponseDto.from(member);
    }
}
