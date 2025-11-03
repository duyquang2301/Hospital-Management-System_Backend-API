package com.wannabe.app.main.service;

import static com.wannabe.app.main.utility.constant.PushConstant.COMMENT_PUSH_BODY;
import static com.wannabe.app.main.utility.constant.PushConstant.COMMENT_PUSH_TITLE;
import static com.wannabe.app.main.utility.constant.PushConstant.COUNSEL_PUSH_BODY;
import static com.wannabe.app.main.utility.constant.PushConstant.COUNSEL_PUSH_TITLE;
import static com.wannabe.app.main.utility.constant.PushConstant.RE_COMMENT_PUSH_BODY;
import static com.wannabe.app.main.utility.constant.PushConstant.RE_COMMENT_PUSH_TITLE;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.wannabe.app.main.data.entity.Counsel;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.CounselType;
import com.wannabe.app.main.exception.found.NotFoundCounselException;
import com.wannabe.app.main.exception.found.NotFoundUserException;
import com.wannabe.app.main.exception.paramter.InvalidCounselException;
import com.wannabe.app.main.mapper.CounselMapper;
import com.wannabe.app.main.mapper.UserMapper;
import com.wannabe.app.main.utility.StringUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class PushService {

    private final RedisService redisService;
    private final CounselMapper counselMapper;
    private final UserMapper userMapper;

    @Value("${domain.backend}")
    private String BACKEND_URL;
    @Value("${domain.article-path}")
    private String ARTICLE_PATH;
    @Value("${domain.review-path}")
    private String REVIEW_PATH;
    @Value("${domain.event-counsel-path}")
    private String EVENT_COUNSEL_PATH;
    @Value("${domain.hospital-counsel-path}")
    private String HOSPITAL_COUNSEL_PATH;

    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * TODO 미사용
     * 댓글 푸시 보내기
     *
     * @param userId      사용자 아이디
     * @param articleId   게시글 아이디
     * @param articleType 게시글 타입
     */
    public void sendCommentPush(long userId, long articleId, ArticleType articleType) {
        Message message = sendMessage(
            findDeviceToken(userId),
            COMMENT_PUSH_TITLE,
            COMMENT_PUSH_BODY,
            buildCommentMessageData(userId, articleId, articleType.equals(ArticleType.ARTICLE))
        );

        if (message == null) {
            return;
        }

        FirebaseMessaging.getInstance().sendAsync(message);
    }

    /**
     * TODO 미사용
     * 답글 푸시 보내기
     *
     * @param userId      사용자 아이디
     * @param articleId   게시글 아이디
     * @param articleType 게시글 타입
     */
    public void sendReCommentPush(long userId, long articleId, ArticleType articleType) {
        Message message = sendMessage(
            findDeviceToken(userId),
            RE_COMMENT_PUSH_TITLE,
            RE_COMMENT_PUSH_BODY,
            buildCommentMessageData(userId, articleId, articleType.equals(ArticleType.ARTICLE))
        );

        if (message == null) {
            return;
        }

        FirebaseMessaging.getInstance().sendAsync(message);
    }

    /**
     * 채팅 푸시 보내기
     *
     * @param receiveUserId 푸시 받을 사용자 아이디
     * @param sendUserId    푸시 보낸 사용자 아이디
     * @param chatId        채팅방 아이디
     * @param message       채팅 내용
     */
    public void sendChatPush(long receiveUserId, long sendUserId, long chatId, String message) {
        User sendUser = userMapper.findUserById(sendUserId);
        User receiveUser = userMapper.findUserById(receiveUserId);

        if (sendUser == null || receiveUser == null) {
            throw new NotFoundUserException(logger);
        }

        Message messageObj = sendMessage(
            findDeviceToken(receiveUserId),
            sendUser.getNickname(),
            message,
            buildChatMessageData(receiveUserId, chatId)
        );

        if (messageObj == null) {
            return;
        }

        FirebaseMessaging.getInstance().sendAsync(messageObj);
    }

    /**
     * 병원 상담 신청 푸시 보내기
     *
     * @param userId       사용자 아이디
     * @param counselId    상담 아이디
     * @param hospitalName 병원 이름
     */
    public void sendHospitalCounselPush(long userId, long counselId, String hospitalName) {
        validateHospitalCounsel(userId, counselId);
        Message message = sendMessage(
            findDeviceToken(userId),
            COUNSEL_PUSH_TITLE,
            buildCounselBody(hospitalName),
            buildHospitalCounselMessageData(userId, counselId)
        );

        if (message == null) {
            return;
        }

        FirebaseMessaging.getInstance().sendAsync(message);
    }

    /**
     * 이벤트 상담 신청 푸시 보내기
     *
     * @param userId    사용자 아아디
     * @param counselId 상담 아이디
     * @param eventName 이벤트 이름
     */
    public void sendEventCounselPush(long userId, long counselId, String eventName) {
        validateEventCounsel(userId, counselId);
        Message message = sendMessage(
            findDeviceToken(userId),
            COUNSEL_PUSH_TITLE,
            buildCounselBody(eventName),
            buildEventCounselMessageData(userId, counselId)
        );

        if (message == null) {
            return;
        }

        FirebaseMessaging.getInstance().sendAsync(message);
    }

    /**
     * 이벤트 상담 검증
     *
     * @param userId    사용자 아이디
     * @param counselId 상담 아이디
     */
    private void validateEventCounsel(long userId, long counselId) {
        Counsel counsel = counselMapper.findCounselByUserIdAndId(userId, counselId)
            .orElseThrow(() -> new NotFoundCounselException(logger));

        if (isValidEventCounsel(counsel)) {
            return;
        }

        throw new InvalidCounselException(logger);
    }

    /**
     * 이벤트 상담 & 완료 여부 검증
     *
     * @param counsel 상담 정보
     * @return boolean 검증 여부
     */
    private boolean isValidEventCounsel(Counsel counsel) {
        return counsel.getCounselType().equals(CounselType.EVENT);
    }

    /**
     * 병원 상담 검증
     *
     * @param userId    사용자 아이디
     * @param counselId 상담 아이디
     */
    private void validateHospitalCounsel(long userId, long counselId) {
        Counsel counsel = counselMapper.findCounselByUserIdAndId(userId, counselId)
            .orElseThrow(() -> new NotFoundCounselException(logger));

        if (isValidHospitalCounsel(counsel)) {
            return;
        }

        logger.error("!!!!! PushService.validateHospitalCounsel() : invalid hospital counsel : {}", counsel.getId());
        throw new InvalidCounselException(logger);
    }

    /**
     * 병원 상담 & 완료 여부 검증
     *
     * @param counsel 상담 정보
     * @return boolean 검증 여부
     */
    private boolean isValidHospitalCounsel(Counsel counsel) {
        return counsel.isHospitalCounsel();
    }

    /**
     * 알림 내역 문구 생성
     *
     * @param title 상담 신청 이름(병원 / 이벤트)
     * @return String 알림 문구
     */
    private String buildCounselBody(String title) {
        return String.format(COUNSEL_PUSH_BODY, title);
    }

    /**
     * 채팅 메세지 객체 생성
     *
     * @param receiveUserId 채팅 받을 사용자 아이디
     * @param chatId        채팅 아이디
     * @return Map 채팅 메세지 객체
     */
    private Map<String, String> buildChatMessageData(long receiveUserId, long chatId) {
        String accessToken = redisService.getAccessToken(receiveUserId) == null ? "" : redisService.getAccessToken(receiveUserId);
        String url = BACKEND_URL + "/chat/" + chatId;
        return Map.of(
            "type", "CHAT",
            "url", url,
            "userId", String.valueOf(receiveUserId),
            "accessToken", accessToken
        );
    }

    /**
     * 이벤트 상담 메세지 객체 생성
     *
     * @param userId    사용자 아이디
     * @param counselId 상담 아이디
     * @return Map 이벤트 상담 메세지 객체
     */
    private Map<String, String> buildEventCounselMessageData(long userId, long counselId) {
        String accessToken = redisService.getAccessToken(userId) == null ? "" : redisService.getAccessToken(userId);
        String url = BACKEND_URL + EVENT_COUNSEL_PATH + counselId;
        return Map.of(
            "type", "COUNSEL",
            "url", url,
            "userId", String.valueOf(userId),
            "accessToken", accessToken
        );
    }

    /**
     * 병원 상담 메세지 객체 생성
     *
     * @param userId    사용자 아이디
     * @param counselId 상담 아이디
     * @return 병원 상담 메세지 객체
     */
    private Map<String, String> buildHospitalCounselMessageData(long userId, long counselId) {
        String accessToken = redisService.getAccessToken(userId) == null ? "" : redisService.getAccessToken(userId);
        String url = BACKEND_URL + HOSPITAL_COUNSEL_PATH + counselId;
        return Map.of(
            "type", "COUNSEL",
            "url", url,
            "userId", String.valueOf(userId),
            "accessToken", accessToken
        );
    }

    /**
     * 댓글 메세지 객체 생성
     *
     * @param userId    사용자 아이디
     * @param articleId 게시글 아이디
     * @param isArticle 게시글 여부
     * @return 댓글 메세지 객체
     */
    private Map<String, String> buildCommentMessageData(long userId, long articleId, boolean isArticle) {
        String accessToken = redisService.getAccessToken(userId) == null ? "" : redisService.getAccessToken(userId);
        String url = BACKEND_URL + getCommunityPath(isArticle) + articleId;
        return Map.of(
            "url", url,
            "userId", String.valueOf(userId),
            "accessToken", accessToken
        );
    }

    /**
     * 게시글 url path 생성
     *
     * @param isArticle 게시글 타입 검증 여부
     * @return String 게시글 url path
     */
    private String getCommunityPath(boolean isArticle) {
        return isArticle ? ARTICLE_PATH : REVIEW_PATH;
    }

    /**
     * 사용자 아이디로 디바이스 토큰 조회
     *
     * @param userId 사용자 아이디
     * @return String 디바이스 토큰
     */
    private String findDeviceToken(long userId) {
        return userMapper.findDeviceToken(userId);
    }

    /**
     * 메세지 전송
     *
     * @param targetToken 전달할 토큰
     * @param title       메세지 제목
     * @param body        메세지 내용
     * @param data        메세지 정보
     * @return Message 메세지
     */
    private Message sendMessage(String targetToken, String title, String body, Map<String, String> data) {
        if (!StringUtil.hasText(targetToken)) {
            return null;
        }

        return Message.builder()
            .setToken(targetToken)
            .setApnsConfig(ApnsConfig.builder()
                .setAps(Aps.builder()
                    .setContentAvailable(true)
//                    .setSound("default")
                    .build()
                )
                .build()
            )
            .setNotification(com.google.firebase.messaging.Notification.builder()
                .setTitle(title)
                .setBody(body)
                .setImage(null)
                .build()
            )
            .putAllData(data)
            .build();
    }
}
