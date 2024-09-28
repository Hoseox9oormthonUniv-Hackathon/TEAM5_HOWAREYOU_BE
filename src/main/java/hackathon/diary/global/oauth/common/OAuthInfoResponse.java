package hackathon.diary.global.oauth.common;

import hackathon.diary.domain.member.domain.entity.SocialType;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    SocialType getOAuthProvider();
}