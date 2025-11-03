package com.wannabe.app.main.service;

import static com.wannabe.app.main.utility.StringUtil.hasText;

import com.wannabe.app.main.data.dto.firestore.ChatDTO;
import com.wannabe.app.main.data.dto.firestore.request.FirstChatRequest;
import com.wannabe.app.main.data.dto.firestore.response.ChattingResponse;
import com.wannabe.app.main.data.dto.user.UserChatProfileDTO;
import com.wannabe.app.main.data.entity.Block;
import com.wannabe.app.main.data.entity.Chatting;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.exception.found.NotFoundUserException;
import com.wannabe.app.main.exception.paramter.AlreadyBlockException;
import com.wannabe.app.main.exception.paramter.InvalidBlockException;
import com.wannabe.app.main.exception.paramter.InvalidChatException;
import com.wannabe.app.main.exception.paramter.InvalidUnblockException;
import com.wannabe.app.main.exception.paramter.NotActiveUserException;
import com.wannabe.app.main.mapper.ChattingMapper;
import com.wannabe.app.main.mapper.UserMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChattingService {

    private final UserMapper userMapper;
    private final ChattingMapper chattingMapper;

    private final CloudFrontService cloudFrontService;
    private final FirestoreService firestoreService;

    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 채팅방 생성
     *
     * @param userId  사용자 아이디
     * @param request 최초 채팅 요청
     * @return 채팅방 생성 정보
     */
    @Transactional
    public ChattingResponse createChatRoom(long userId, FirstChatRequest request) {
        validateFirstChatRequest(request);
        validateUserIds(userId, request.getTargetUserId());
        Chatting findChatting = chattingMapper.findChattingByUserIds(userId, request.getTargetUserId());
        UserChatProfileDTO otherUser = userMapper.findUserChatProfile(request.getTargetUserId())
            .orElseThrow(() -> new NotFoundUserException(logger));
        otherUser.updateSignedUrl(cloudFrontService.generateSignedUrl(otherUser.getProfileImageUrl()));

        if (findChatting != null) {
            return ChattingResponse.from(
                findChatting,
                firestoreService.insertChatMessage(findChatting.getId(), userId, request.getFirstMessage()),
                otherUser
            );
        }

        long chatId = insertChatroomInFirestore(userId, request.getTargetUserId());

        return ChattingResponse.from(
            createChatRoom(chatId, userId, request.getTargetUserId()),
            firestoreService.insertChatMessage(chatId, userId, request.getFirstMessage()),
            otherUser
        );
    }

    /**
     * 채팅방 목록
     *
     * @param userId 사용자 아이디
     * @return 채팅방 목록 정보
     */
    public List<ChattingResponse> getChattingList(long userId) {
        List<Chatting> chattingListByUserId = chattingMapper.findChattingListByUserId(userId);

        if (chattingListByUserId == null || chattingListByUserId.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> blockListByUserId = chattingMapper.findBlockUserIdListByUserId(userId);
        List<ChattingResponse> result = new ArrayList<>();

        for (Chatting chatting : chattingListByUserId) {
            if (blockListByUserId.contains(chatting.getOtherUserId(userId))) {
                continue;
            }

            addChattingRoomList(userId, result, chatting);
        }

        return sortByRecentMessage(result);
    }

    /**
     * 사용자 차단
     *
     * @param userId       사용자 아이디
     * @param targetUserId 차단할 사용자 아이디
     */
    @Transactional
    public void blockUser(long userId, long targetUserId) {
        if (userId == targetUserId) {
            throw new InvalidBlockException(logger);
        }

        Chatting chattingByUserIds = chattingMapper.findChattingByUserIds(userId, targetUserId);

        if (chattingByUserIds == null) {
            chattingMapper.insertBlock(Block.from(userId, targetUserId));
            return;
        }

        firestoreService.updateRequestBlock(chattingByUserIds.getFirebaseKey(), userId);
        chattingMapper.insertBlock(Block.from(userId, targetUserId));
    }

    @Transactional
    public void unblockUser(long userId, long targetUserId) {
        List<Long> blockList = chattingMapper.findBlockUserIdListByUserId(userId);

        for (Long blockUserId : blockList) {
            if (!blockUserId.equals(targetUserId)) {
                continue;
            }

            Chatting findChat = chattingMapper.findChattingByUserIds(userId, targetUserId);

            if (findChat == null) {
                chattingMapper.deleteBlock(Block.from(userId, targetUserId));
                return;
            }

            firestoreService.updateUnRequestBlock(findChat.getFirebaseKey(), userId);
            chattingMapper.deleteBlock(Block.from(userId, targetUserId));
            return;
        }

        throw new InvalidUnblockException(logger);
    }

    /**
     * 채팅
     *
     * @param userId  사용자 아이디
     * @param chatId  채팅방 아이디
     * @param message 채팅 내용
     */
    @Transactional
    public void sendMessage(long userId, long chatId, String message) {
        Chatting chatting = chattingMapper.findChattingByChatId(chatId);

        if (chatting == null) {
            throw new InvalidChatException(logger);
        }

        validateBlockByChat(userId, chatting.getOtherUserId(userId));

        firestoreService.insertChatMessage(chatId, userId, message);
    }

    /**
     * 채팅방 연결
     *
     * @param userId 사용자 아이디
     * @param chatId 채팅방 아이디
     */
    @Transactional
    public void connectChatRoom(long userId, long chatId) {
        Chatting chatting = chattingMapper.findChattingByChatId(chatId);

        if (chatting == null) {
            throw new InvalidChatException(logger);
        }

        validateChattingRoom(chatting, userId);

        firestoreService.updateRequestConnect(chatting.getFirebaseKey(), userId);
    }

    /**
     * 채팅방 연결 해제
     *
     * @param userId 사용자 아이디
     * @param chatId 채팅방 아이디
     */
    @Transactional
    public void disconnectChatRoom(long userId, long chatId) {
        Chatting chatting = chattingMapper.findChattingByChatId(chatId);

        if (chatting == null) {
            throw new InvalidChatException(logger);
        }

        validateChattingRoom(chatting, userId);

        firestoreService.updateRequestDisconnect(userId);
    }

    /**
     * 차단된 채팅 검증
     *
     * @param userId       사용자 아이디
     * @param targetUserId 차단 할 사용자 아이디
     */
    private void validateBlockByChat(long userId, long targetUserId) {
        List<Long> blockUserIdListByUserId = chattingMapper.findBlockUserIdListByUserId(userId);

        if (blockUserIdListByUserId == null && blockUserIdListByUserId.isEmpty()) {
            return;
        }

        if (blockUserIdListByUserId.contains(targetUserId)) {
            throw new AlreadyBlockException(logger);
        }
    }

    /**
     * 채팅방 검증
     *
     * @param chatting   채팅방 정보
     * @param sendUserId 채팅 전송 한 사용자 아이디
     */
    private void validateChattingRoom(Chatting chatting, long sendUserId) {
        if (chatting == null) {
            throw new InvalidChatException(logger);
        }

        if (!chatting.isChattingUser(sendUserId)) {
            throw new InvalidChatException(logger);
        }

        validateBlockByChat(sendUserId, chatting.getOtherUserId(sendUserId));
    }

    /**
     * 채팅방 목록 추가
     *
     * @param userId       사용자 아이디
     * @param chattingList 채팅방 목록
     * @param chatting     채팅방 정보
     */
    private void addChattingRoomList(long userId, List<ChattingResponse> chattingList, Chatting chatting) {
        long otherUserId = chatting.getOtherUserId(userId);
        UserChatProfileDTO otherUser = userMapper.findUserChatProfile(otherUserId)
            .orElseThrow(() -> new NotFoundUserException(logger));
        otherUser.updateSignedUrl(cloudFrontService.generateSignedUrl(otherUser.getProfileImageUrl()));

        ChatDTO firestoreChat = firestoreService.getChatDTO(chatting.getFirebaseKey());

        long unreadCount = userId == firestoreChat.getLastMessage().getSendUser() ? 0
            : firestoreService.getUnreadMessageCount(chatting.getId(), otherUser.getUserId());

        chattingList.add(ChattingResponse.from(chatting, firestoreChat, otherUser, unreadCount));
    }

    /**
     * 최근 메시지 정렬
     *
     * @param chattingList 채팅방 목록
     * @return 정렬한 채팅방 목록
     */
    private List<ChattingResponse> sortByRecentMessage(List<ChattingResponse> chattingList) {
        chattingList.sort((o1, o2) -> {
            if (o1.getLastMessage() == null) {
                return 1;
            }

            if (o2.getLastMessage() == null) {
                return -1;
            }

            return o2.getLastMessage().getCreatedAt().compareTo(o1.getLastMessage().getCreatedAt());
        });

        return chattingList;
    }

    /**
     * 최초 채팅 검증
     *
     * @param request 최초 채팅 요청 정보
     */
    private void validateFirstChatRequest(FirstChatRequest request) {
        if (request.getTargetUserId() == null || !hasText(request.getFirstMessage())) {
            throw new InvalidChatException(logger);
        }
    }

    /**
     * fire store 에 채팅방 생성
     *
     * @param userId       사용자 아이디
     * @param targetUserId 채팅 상대 아이디
     * @return Long 채팅방 아이디
     */
    private Long insertChatroomInFirestore(long userId, long targetUserId) {
        return firestoreService.createChat(userId, targetUserId);
    }

    /**
     * 채팅방 생성
     *
     * @param chatId       채팅방 아이디
     * @param userId       사용자 아이디
     * @param targetUserId 채팅 상대 아이디
     * @return 채팅 정보
     */
    private Chatting createChatRoom(long chatId, long userId, long targetUserId) {
        Chatting chatting = Chatting.from(chatId, userId, targetUserId);
        chattingMapper.insertChatting(chatting);
        return chatting;
    }

    /**
     * 사용자 아이디 검증
     *
     * @param userId       사용자 아이디
     * @param targetUserId 채팅 상대 아이디
     */
    private void validateUserIds(long userId, long targetUserId) {
        if (userId == targetUserId) {
            throw new InvalidChatException(logger);
        }

        getActiveUser(userId);
        getActiveUser(targetUserId);
    }

    /**
     * 활성화된 사용자 조회
     *
     * @param userId 사용자 아이디
     * @return User 사용자 정보
     */
    private User getActiveUser(long userId) {
        User findUser = findUserById(userId);

        if (findUser == null) {
            throw new NotFoundUserException(logger);
        }

        if (findUser.isActiveUser()) {
            return findUser;
        }

        throw new NotActiveUserException(logger);
    }

    /**
     * 사용자 아이디로 사용자 조회
     *
     * @param userId 사용자 아이디
     * @return User 사용자 정보
     */
    private User findUserById(long userId) {
        return userMapper.findUserById(userId);
    }
}
