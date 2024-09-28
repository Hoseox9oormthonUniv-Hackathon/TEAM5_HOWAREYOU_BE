package hackathon.diary.domain.diary.domain.repository;

import hackathon.diary.domain.diary.domain.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryCustomRepository {
}
