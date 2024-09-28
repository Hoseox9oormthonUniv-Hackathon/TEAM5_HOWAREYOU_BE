package hackathon.diary.domain.auth.application;

import hackathon.diary.domain.auth.dto.ReIssueRequestDto;
import hackathon.diary.domain.auth.exception.InvalidTokenException;
import hackathon.diary.domain.member.domain.entity.Member;
import hackathon.diary.domain.member.domain.repository.MemberRepository;
import hackathon.diary.domain.member.exception.MemberNotFoundException;
import hackathon.diary.global.exception.dto.ErrorCode;
import hackathon.diary.global.jwt.domain.entity.Token;
import hackathon.diary.global.jwt.domain.repository.TokenRepository;
import hackathon.diary.global.jwt.dto.response.TokenDto;
import hackathon.diary.global.jwt.util.JwtTokenProvider;
import hackathon.diary.global.oauth.common.OAuthInfoResponse;
import hackathon.diary.global.oauth.common.OAuthLoginParams;
import hackathon.diary.global.oauth.exception.OAuthException;
import hackathon.diary.global.oauth.service.RequestOAuthInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;

    @Transactional
    public TokenDto login(OAuthLoginParams params) throws OAuthException, MemberNotFoundException {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        String email = findOrCreateMember(oAuthInfoResponse);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        TokenDto tokenDto = jwtTokenProvider.generate(email);
        saveOrUpdateToken(member, tokenDto);

        return tokenDto;
    }

    private String findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return memberRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(Member::getEmail)
                .orElseGet(() -> createNewMember(oAuthInfoResponse));
    }

    private String createNewMember(OAuthInfoResponse oAuthInfoResponse) {
        Member member = Member.builder()
                .email(oAuthInfoResponse.getEmail())
                .name(oAuthInfoResponse.getNickname())
                .socialType(oAuthInfoResponse.getOAuthProvider())
                .build();

        return memberRepository.save(member).getEmail();
    }

    private void saveOrUpdateToken(Member member, TokenDto tokenDto) {
        Token token = tokenRepository.findByMember(member)
                .orElseGet(() -> createNewToken(member, tokenDto));

        token.updateRefreshToken(tokenDto.getRefreshToken());
        tokenRepository.save(token);
    }

    private Token createNewToken(Member member, TokenDto tokenDto) {
        return Token.builder()
                .member(member)
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }

    @Transactional
    public TokenDto generateAccessToken(ReIssueRequestDto reIssueRequestDto) throws MemberNotFoundException, InvalidTokenException {
        Token token = validateRefreshToken(reIssueRequestDto);

        Member member = memberRepository.findById(token.getMember().getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return createNewAccessToken(member, token.getRefreshToken());
    }

    private Token validateRefreshToken(ReIssueRequestDto reIssueRequestDto) throws InvalidTokenException {
        Token token = tokenRepository.findByRefreshToken(reIssueRequestDto.getRefreshToken());
        if (token.getRefreshToken() == null) {
            throw new InvalidTokenException(ErrorCode.TOKEN_EXPIRED);
        }
        return token;
    }

    private TokenDto createNewAccessToken(Member member, String refreshToken) {
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(jwtTokenProvider.generateAccessToken(member.getEmail()))
                .refreshToken(refreshToken)
                .build();
    }
}
