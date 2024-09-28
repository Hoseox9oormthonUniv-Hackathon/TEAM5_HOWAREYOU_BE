package hackathon.diary.domain.diary.domain.repository;

import hackathon.diary.domain.diary.domain.entity.Diary;
import hackathon.diary.domain.member.domain.entity.Member;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryCustomRepository {
}
