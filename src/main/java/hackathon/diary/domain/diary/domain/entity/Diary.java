package hackathon.diary.domain.diary.domain.entity;

import hackathon.diary.domain.member.domain.entity.Member;
import hackathon.diary.global.template.BaseTimeEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@Table(name = "diary")
@NoArgsConstructor
@DynamicUpdate
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(columnDefinition="TEXT", name = "content")
    private String content;

    @Lob
    @Column(columnDefinition="TEXT", name = "voice_text")
    private String voiceText;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_shared")
    private Boolean isShared;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateIsShared() {
        this.isShared = !this.isShared;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
