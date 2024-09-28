package hackathon.diary.domain.diary.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hackathon.diary.domain.diary.domain.entity.Diary;
import hackathon.diary.domain.member.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static hackathon.diary.domain.diary.domain.entity.QDiary.diary;

@Slf4j
@Repository
@AllArgsConstructor
public class DiaryCustomRepositoryImpl implements DiaryCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Diary> findPublicDiaryList() {
        return jpaQueryFactory.selectFrom(diary).where(diary.isShared.eq(true)).fetch();
    }

    @Override
    public List<Diary> findRecentDiariesByMember(Member member) {
        return jpaQueryFactory
                .selectFrom(diary)
                .where(diary.member.eq(member))
                .orderBy(diary.createAt.desc())
                .limit(3)
                .fetch();
    }
}
