package hackathon.diary.domain.member.domain.entity;

import hackathon.diary.global.jwt.domain.entity.Token;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@Table(name = "member")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(name = "name")
    private String name;

    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "social_type")
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Token token;
}
