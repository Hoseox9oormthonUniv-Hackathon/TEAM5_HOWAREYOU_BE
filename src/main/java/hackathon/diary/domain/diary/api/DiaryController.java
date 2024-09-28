package hackathon.diary.domain.diary.api;

import hackathon.diary.domain.diary.application.DiaryService;
import hackathon.diary.domain.diary.dto.request.DiaryRequestDto;
import hackathon.diary.domain.diary.dto.request.EditDiaryRequestDto;
import hackathon.diary.domain.diary.exception.DiaryAccessDeniedException;
import hackathon.diary.domain.diary.exception.DiaryNotFoundException;
import hackathon.diary.domain.diary.exception.OpenAiApiException;
import hackathon.diary.domain.member.exception.MemberNotFoundException;
import hackathon.diary.global.template.ResponseTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/v1/diary")
@Tag(name = "Diary Controller", description = "Diary API")
public class DiaryController {
    private final DiaryService diaryService;

    @GetMapping("/")
    public ResponseTemplate<?> getPublicDiaryList() {
        return new ResponseTemplate<>(HttpStatus.OK, "공유된 일기 리스트 가져오기 성공", diaryService.getPublicDiaryList());
    }

    @PostMapping("/")
    public ResponseTemplate<?> generateDiary(@AuthenticationPrincipal String email, DiaryRequestDto diaryRequestDto) throws OpenAiApiException, MemberNotFoundException, IOException {
        return new ResponseTemplate<>(HttpStatus.CREATED, "일기 생성 성공", diaryService.saveDiary(email, diaryRequestDto.voiceText(), diaryRequestDto.image()));
    }

    @GetMapping("/{id}")
    public ResponseTemplate<?> getDiary(@AuthenticationPrincipal String email, @PathVariable Long id) throws DiaryNotFoundException, MemberNotFoundException, DiaryAccessDeniedException {
        return new ResponseTemplate<>(HttpStatus.OK, "일기 가져오기 성공", diaryService.getDiary(email, id));
    }

    @PutMapping("/")
    public ResponseTemplate<?> editDiary(@AuthenticationPrincipal String email, @RequestBody EditDiaryRequestDto editDiaryRequestDto) throws MemberNotFoundException, DiaryAccessDeniedException, DiaryNotFoundException {
        diaryService.editDiary(email, editDiaryRequestDto.title(), editDiaryRequestDto.content(), editDiaryRequestDto.diaryId());
        return new ResponseTemplate<>(HttpStatus.OK, "일기 수정 성공");
    }

    @GetMapping("/my")
    public ResponseTemplate<?> myDiary(@AuthenticationPrincipal String email) throws MemberNotFoundException {
        return new ResponseTemplate<>(HttpStatus.OK, "내 일기 가져오기 성공", diaryService.getMyDairy(email));
    }
    @GetMapping("/recent")
    public ResponseTemplate<?> recentDiary(@AuthenticationPrincipal String email) throws MemberNotFoundException {
        return new ResponseTemplate<>(HttpStatus.OK, "최근 일기 불러오기 성공", diaryService.getRecentDiaryList(email));
    }
    @PutMapping("/share/{id}")
    public ResponseTemplate<?> isShared(@AuthenticationPrincipal String email, @PathVariable Long id) throws DiaryAccessDeniedException, DiaryNotFoundException, MemberNotFoundException {
        diaryService.isShared(email, id);
        return new ResponseTemplate<>(HttpStatus.OK, "공유 상태 변경");
    }

    @DeleteMapping("/{id}")
    public ResponseTemplate<?> deleteDiary(@AuthenticationPrincipal String email, @PathVariable Long id) throws DiaryAccessDeniedException, DiaryNotFoundException, MemberNotFoundException {
        diaryService.deleteDiary(email, id);
        return new ResponseTemplate<>(HttpStatus.PERMANENT_REDIRECT, "일기 삭제 성공");
    }
}
