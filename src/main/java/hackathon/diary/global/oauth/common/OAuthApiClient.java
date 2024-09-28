package hackathon.diary.global.oauth.common;

import hackathon.diary.domain.member.domain.entity.SocialType;

public interface OAuthApiClient {
    SocialType oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthInfoResponse requestOauthInfo(String accessToken);
}
