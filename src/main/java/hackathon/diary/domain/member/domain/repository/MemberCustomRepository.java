package hackathon.diary.domain.member.domain.repository;




import hackathon.diary.domain.member.domain.entity.Member;

import java.util.Optional;

public interface MemberCustomRepository {
    Optional<Member> findByEmail(String email);
}

