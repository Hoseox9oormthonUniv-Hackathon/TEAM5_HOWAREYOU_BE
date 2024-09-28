package hackathon.diary.domain.diary.domain.repository;

import hackathon.diary.domain.diary.domain.entity.Diary;
import hackathon.diary.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Optional;

public interface DiaryCustomRepository {
    List<Diary> findPublicDiaryList();

    List<Diary> findRecentDiariesByMember(Member member);

    List<Diary> findAllByMember(Member member);
}
