package hackathon.diary.domain.diary.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.diary.domain.Image.application.ImageService;
import hackathon.diary.domain.diary.domain.entity.Diary;
import hackathon.diary.domain.diary.domain.repository.DiaryRepository;
import hackathon.diary.domain.diary.dto.response.DiaryResponseDto;
import hackathon.diary.domain.diary.exception.DiaryAccessDeniedException;
import hackathon.diary.domain.diary.exception.DiaryNotFoundException;
import hackathon.diary.domain.diary.exception.OpenAiApiException;
import hackathon.diary.domain.member.domain.entity.Member;
import hackathon.diary.domain.member.exception.MemberNotFoundException;
import hackathon.diary.global.exception.dto.ErrorCode;
import hackathon.diary.global.utils.GlobalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    @Value("${spring.openai.api.key}")
    private String apiKey;

    private final GlobalUtil globalUtil;
    private final ImageService imageService;

    private final DiaryRepository diaryRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL_NAME = "gpt-4o-mini";
    private static final String SYSTEM_MSG_CONTENT =
            "너는 친절한 AI야. 사용자로부터 제공된 음성 텍스트와 이미지를 바탕으로 일기의 제목과 내용을 JSON 형식으로 만들어줘. " +
                    "title은 10자 이하, content의 길이는 300자 이상 350자 이하로 만들어줘." +
                    "응답은 반드시 다음 JSON 형식으로 해줘: {\"title\": \"일기 제목\", \"content\": \"일기 내용\"}";

    public DiaryResponseDto saveDiary(String email, String voiceText, MultipartFile image) throws OpenAiApiException, MemberNotFoundException, IOException {
        Member member = globalUtil.findByMemberWithEmail(email);
        String imageUrl = imageService.uploadImage(image);
        Map<String, String> generatedDiary = externalOpenAiApi(voiceText, imageUrl);  // OpenAI API 호출 및 결과 반환

        log.info("Generated Title: {}", generatedDiary.get("title"));
        log.info("Generated Content: {}", generatedDiary.get("content"));
        log.info("Generated Image: {}", imageUrl);

        Diary diary = createDiary(generatedDiary, voiceText, member, imageUrl);
        diaryRepository.save(diary);
        return createDiaryResponseDto(diary);
    }

    @Transactional(readOnly = true)
    public List<DiaryResponseDto> getPublicDiaryList() {
        return diaryRepository.findPublicDiaryList().stream()
                .map(this::createDiaryResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DiaryResponseDto> getRecentDiaryList(String email) throws MemberNotFoundException {
        Member member = globalUtil.findByMemberWithEmail(email);

        return diaryRepository.findRecentDiariesByMember(member).stream()
                .map(this::createDiaryResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiaryResponseDto getDiary(@AuthenticationPrincipal String email, Long id) throws DiaryNotFoundException, MemberNotFoundException, DiaryAccessDeniedException {
        Member member = globalUtil.findByMemberWithEmail(email);
        Diary diary = diaryRepository.findById(id).orElseThrow(() -> new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND));
        if(!diary.getIsShared() && !diary.getMember().equals(member)) {
            throw new DiaryAccessDeniedException(ErrorCode.DIARY_ACCESS_DENIED);
        }
        return createDiaryResponseDto(diary);
    }

    @Transactional
    public void isShared(String email, Long id) throws MemberNotFoundException, DiaryNotFoundException, DiaryAccessDeniedException {
        Member member = globalUtil.findByMemberWithEmail(email);
        Diary diary = diaryRepository.findById(id).orElseThrow(()-> new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND));

        if(!diary.getMember().equals(member)) {
            throw new DiaryAccessDeniedException(ErrorCode.DIARY_ACCESS_DENIED);
        }
        diary.updateIsShared();
    }

    @Transactional
    public void deleteDiary(String email, Long id) throws DiaryAccessDeniedException, DiaryNotFoundException, MemberNotFoundException {
        Member member = globalUtil.findByMemberWithEmail(email);
        Diary diary = diaryRepository.findById(id).orElseThrow(()-> new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND));
        if(!diary.getMember().equals(member)) {
            throw new DiaryAccessDeniedException(ErrorCode.DIARY_ACCESS_DENIED);
        }
        imageService.deleteImage(diary.getImageUrl());
        diaryRepository.delete(diary);
    }

    @Transactional
    public void editDiary(String email, String title, String content, Long id) throws MemberNotFoundException, DiaryNotFoundException, DiaryAccessDeniedException {
        Member member = globalUtil.findByMemberWithEmail(email);
        Diary diary = diaryRepository.findById(id).orElseThrow(()-> new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND));
        if(!diary.getMember().equals(member)) {
            throw new DiaryAccessDeniedException(ErrorCode.DIARY_ACCESS_DENIED);
        }
        diary.updateTitleAndContent(title, content);
    }


    private Diary createDiary(Map<String, String> generatedDiary, String voiceText, Member member, String imageUrl) {
        return Diary.builder()
                .title(generatedDiary.get("title"))
                .content(generatedDiary.get("content"))
                .imageUrl(imageUrl)
                .voiceText(voiceText)
                .isShared(false)
                .member(member)
                .build();
    }

    private DiaryResponseDto createDiaryResponseDto(Diary diary) {
        return DiaryResponseDto.builder()
                .writer(diary.getMember().getName())
                .createAt(diary.getCreateAt())
                .imageUrl(diary.getImageUrl())
                .diaryId(diary.getDiaryId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .isShared(diary.getIsShared())
                .build();
    }

    // OpenAI API 호출 로직
    private Map<String, String> externalOpenAiApi(String userMsg, String imageUrl) throws OpenAiApiException {
        HttpHeaders headers = createHeaders();
        String body = createRequestBody(userMsg, imageUrl);

        String apiResponse = callOpenAiApi(headers, body);
        log.info("OpenAI API Response: {}", apiResponse);

        return parseApiResponse(apiResponse);  // 응답 파싱 후 반환
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return headers;
    }

    private String createRequestBody(String userMsg, String imageUrl) throws OpenAiApiException {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", MODEL_NAME);
        bodyMap.put("messages", createMessages(userMsg, imageUrl));
        try {
            return objectMapper.writeValueAsString(bodyMap);
        } catch (JsonProcessingException e) {
            throw new OpenAiApiException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }

    private List<Map<String, String>> createMessages(String userMsg, String imageUrl) {
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", SYSTEM_MSG_CONTENT);
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userMsg);
        userMessage.put("imageUrl", imageUrl);
        messages.add(userMessage);

        return messages;
    }

    private String callOpenAiApi(HttpHeaders headers, String body) throws OpenAiApiException {
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new OpenAiApiException(ErrorCode.OPENAI_API_ERROR);
        }

        return response.getBody();
    }

    private Map<String, String> parseApiResponse(String apiResponse) throws OpenAiApiException {
        try {
            Map<?, ?> responseMap = objectMapper.readValue(
                    objectMapper.readTree(apiResponse).path("choices").get(0).path("message").path("content").asText(),
                    Map.class
            );
            Map<String, String> result = new HashMap<>();
            result.put("title", String.valueOf(responseMap.get("title")));
            result.put("content", String.valueOf(responseMap.get("content")));
            return result;
        } catch (JsonProcessingException e) {
            throw new OpenAiApiException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }
}
