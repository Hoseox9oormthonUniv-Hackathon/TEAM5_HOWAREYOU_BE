package hackathon.diary.global.oauth.common;

import hackathon.diary.domain.member.domain.entity.SocialType;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    SocialType oAuthProvider();
    MultiValueMap<String, String> makeBody();
}
