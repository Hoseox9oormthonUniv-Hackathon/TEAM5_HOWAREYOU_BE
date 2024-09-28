package hackathon.diary.domain.auth.api;

import hackathon.diary.domain.auth.application.AuthService;
import hackathon.diary.domain.auth.dto.ReIssueRequestDto;
import hackathon.diary.domain.auth.exception.InvalidTokenException;
import hackathon.diary.domain.member.exception.MemberNotFoundException;
import hackathon.diary.global.exception.dto.ErrorResponse;
import hackathon.diary.global.jwt.dto.response.TokenDto;
import hackathon.diary.global.oauth.exception.OAuthException;
import hackathon.diary.global.oauth.kakao.KakaoLoginParams;
import hackathon.diary.global.template.ResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Controller", description = "Auth API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "카카오 소셜 로그인", description = "카카오 소셜 로그인입니다.")
    @Parameter(name = "authorizationCode", description = "인가코드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오 로그인 성공", content = @Content(schema = @Schema(implementation = TokenDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "OAuth 인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/kakao")
    public ResponseTemplate<TokenDto> loginKakao(@RequestBody KakaoLoginParams params) throws OAuthException, MemberNotFoundException {
        return new ResponseTemplate<>(HttpStatus.OK, "카카오 로그인 성공", authService.login(params));
    }

    @Operation(summary = "액세스 토큰 재발급", description = "토큰 만료 시, 액세스 토큰을 재발급 받습니다.")
    @Parameter(name = "refreshToken", description = "refreshToken")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세스 토큰 재발급 성공", content = @Content(schema = @Schema(implementation = TokenDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "사용자 인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reissue")
    public ResponseTemplate<TokenDto> newAccessToken(@RequestBody ReIssueRequestDto reIssueRequestDto) throws MemberNotFoundException, InvalidTokenException {
        TokenDto newToken = authService.generateAccessToken(reIssueRequestDto);
        return new ResponseTemplate<>(HttpStatus.OK, "액세스 토큰 발급", newToken);
    }
}
