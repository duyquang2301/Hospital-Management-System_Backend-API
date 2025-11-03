package com.wannabe.app.main.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.wannabe.app.main.data.dto.firestore.ChatDTO;
import com.wannabe.app.main.data.dto.firestore.ConnectDTO;
import com.wannabe.app.main.data.dto.firestore.InsertMessageDTO;
import com.wannabe.app.main.data.dto.firestore.LastMessageDTO;
import com.wannabe.app.main.data.state.FirestoreCollection;
import com.wannabe.app.main.data.state.MessageProperty;
import com.wannabe.app.main.exception.paramter.BlockException;
import com.wannabe.app.main.mapper.ChattingMapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirestoreService {

    private final ChattingMapper chattingMapper;

    private final PushService pushService;

    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 채팅방 생성
     *
     * @param userId       사용자 아이디
     * @param targetUserId 채팅 할 상대방 아이디
     * @return long 채팅방 seq
     */
    public long createChat(long userId, long targetUserId) {
        Firestore firestore = FirestoreClient.getFirestore();
        List<Long> users = List.of(userId, targetUserId);
        ChatDTO chatDTO = ChatDTO.from(new LastMessageDTO(), users);
        Long chatSeq = chattingMapper.increaseChattingSeq();
        ApiFuture<WriteResult> apiFuture = firestore
            .collection(FirestoreCollection.CHAT.getCollectionName())
            .document(chatSeq.toString())
            .set(chatDTO);

        return chatSeq;
    }

    /**
     * 채팅 내용 저장
     *
     * @param chatId  채팅방 아이디
     * @param userId  사용자 아이디
     * @param message 채팅 내용
     * @return LastMessageDTO 채팅 정보
     */
    public LastMessageDTO insertChatMessage(long chatId, long userId, String message) {
        validateBlocked(String.valueOf(chatId), userId);
        updateConnect(String.valueOf(chatId), userId);
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection(FirestoreCollection.CHAT.getCollectionName()).document(String.valueOf(chatId));
        InsertMessageDTO insertMessageDTO = InsertMessageDTO.of(message, userId);
        LastMessageDTO insertLastMessageDTO = LastMessageDTO.of(message, userId);
        ApiFuture<WriteResult> apiFuture = docRef.update("lastMessage", insertLastMessageDTO);
        ApiFuture<DocumentReference> apiFutureMessage = docRef.collection(FirestoreCollection.MESSAGES.getCollectionName()).document().getParent()
            .add(insertMessageDTO);

        if (isConnect(String.valueOf(chatId), userId)) {
            ChatDTO chatDTO = getChatDTO(String.valueOf(chatId));
            pushService.sendChatPush(chatDTO.getOtherUserId(userId), userId, chatId, message);
        }

        return insertLastMessageDTO;
    }

    /**
     * 채팅빙 정보 조회
     *
     * @param chatId 채팅방 아이디
     * @return ChatDTO 채팅 객체
     */
    public ChatDTO getChatDTO(String chatId) {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection(FirestoreCollection.CHAT.getCollectionName()).document(chatId);
        ApiFuture<DocumentSnapshot> apiFuture = docRef.get();
        DocumentSnapshot documentSnapshot = null;
        try {
            documentSnapshot = apiFuture.get();
        } catch (Exception e) {
            log.error("!!!!!! FirestoreService.getLastMessage() - error : {}", e.getMessage());
        }

        if (documentSnapshot.exists()) {
            return documentSnapshot.toObject(ChatDTO.class);
        }

        return new ChatDTO();
    }

    /**
     * 채팅방 연결 조회
     *
     * @param userId 사용자 아이디
     * @return ConnectDTO 채팅방 연결 객체
     */
    public ConnectDTO getConnectDTO(long userId) {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection(FirestoreCollection.USER.getCollectionName()).document(String.valueOf(userId));
        ApiFuture<DocumentSnapshot> apiFuture = docRef.get();
        DocumentSnapshot documentSnapshot = null;

        try {
            documentSnapshot = apiFuture.get();
        } catch (Exception e) {
            log.error("!!!!!! FirestoreService.getConnectDTO() - error : {}", e.getMessage());
        }

        if (documentSnapshot.exists()) {

            return ConnectDTO.convert(Objects.requireNonNull(documentSnapshot.get("connectedChat")), documentSnapshot.getDate("connectedAt"));
        }

        return new ConnectDTO();

    }

    /**
     * 차단 된 채팅방 업데이트
     *
     * @param chatId 채팅방 아이디
     * @param userId 사용자 아이디
     */
    public void updateRequestBlock(String chatId, long userId) {
        ChatDTO firestoreChat = getChatDTO(chatId);

        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection(FirestoreCollection.CHAT.getCollectionName()).document(String.valueOf(chatId));
        ApiFuture<WriteResult> apiFuture = docRef.update("requestBlock", addRequestBlockList(firestoreChat, userId).getRequestBlock());
    }

    public void updateUnRequestBlock(String chatId, long userId) {
        ChatDTO firestoreChat = getChatDTO(chatId);

        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection(FirestoreCollection.CHAT.getCollectionName()).document(String.valueOf(chatId));
        ApiFuture<WriteResult> apiFuture = docRef.update("requestBlock", removeRequestBlockList(firestoreChat, userId).getRequestBlock());
    }

    /**
     * 채팅방 연결 요청 업데이트
     *
     * @param chatId 채팅방 아이디
     * @param userId 사용자 아이디
     */
    public void updateRequestConnect(String chatId, long userId) {
        ConnectDTO firestoreConnect = getConnectDTO(userId);

        if (firestoreConnect.getConnectedChat() == null || firestoreConnect.getConnectedChat() == 0) {
            createConnect(chatId, userId);
            return;
        }

        updateConnect(chatId, userId);
    }

    /**
     * 채팅방 나가기 요청 업데이트
     *
     * @param userId 사용자 아이디
     */
    public void updateRequestDisconnect(long userId) {
        ConnectDTO firestoreConnect = getConnectDTO(userId);

        if (firestoreConnect.getConnectedChat() == null || firestoreConnect.getConnectedChat() == 0) {
            return;
        }

        updateDisconnect(userId);
    }

    /**
     * 읽지 않은 메세지 수 조회
     *
     * @param chatId      채팅방 아이디
     * @param otherUserId 다른 사용자 아이디
     * @return long 읽지 않은 메세지 수
     */
    public long getUnreadMessageCount(long chatId, long otherUserId) {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection(FirestoreCollection.CHAT.getCollectionName()).document(String.valueOf(chatId));

        CollectionReference messageRef = docRef.collection(FirestoreCollection.MESSAGES.getCollectionName()).document().getParent();
        ApiFuture<QuerySnapshot> messageSnapshot = messageRef
            .whereEqualTo(MessageProperty.IS_READ.getPropertyName(), false)
            .whereEqualTo(MessageProperty.SEND_USER.getPropertyName(), otherUserId)
            .get();

        List<InsertMessageDTO> messageDTOList = new ArrayList<>();

        try {
            List<QueryDocumentSnapshot> messageList = messageSnapshot.get().getDocuments();
            for (QueryDocumentSnapshot document : messageList) {
                messageDTOList.add(document.toObject(InsertMessageDTO.class));
            }

        } catch (ExecutionException | InterruptedException e) {
            log.error("!!!!! FirestoreService.getUnreadMessageCount() - failed parse collection, otherUserId : {}", otherUserId);
            log.error("!!!!! FirestoreService.getUnreadMessageCount() - failed parse collection, chatId : {}", chatId);
        }

        return messageDTOList.size();
    }

    /**
     * 채팅방 연결 검증
     *
     * @param chatId 채팅방 아이디
     * @param userId 사용자 아이디
     * @return 채팅방 연결 여부
     */
    private boolean isConnect(String chatId, long userId) {
        ConnectDTO firestoreConnect = getConnectDTO(userId);

        if (firestoreConnect.getConnectedChat() == null || firestoreConnect.getConnectedChat() == 0) {
            return false;
        }

        return firestoreConnect.getConnectedChat().equals(Long.valueOf(chatId));
    }

    /**
     * 채팅방 연결 요청 업데이트
     *
     * @param chatId 채팅방 아이디
     * @param userId 사용자 아이디
     */
    private void updateConnect(String chatId, long userId) {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection(FirestoreCollection.USER.getCollectionName()).document(String.valueOf(userId));
        ApiFuture<WriteResult> apiFuture = firestore
            .collection(FirestoreCollection.USER.getCollectionName())
            .document(String.valueOf(userId))
            .update("connectedChat", chatId);

        ApiFuture<WriteResult> apiFutureUpdateDate = firestore
            .collection(FirestoreCollection.USER.getCollectionName())
            .document(String.valueOf(userId))
            .update("connectedAt", new Date());

        ChatDTO chatDTO = getChatDTO(chatId);

        updateRead(chatId, chatDTO.getOtherUserId(userId));
    }

    /**
     * 읽음 처리
     *
     * @param chatId     채팅방 아이디
     * @param sendUserId 보낸 사용자 아이디
     */
    private void updateRead(String chatId, long sendUserId) {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference docRef = firestore.collection(FirestoreCollection.CHAT.getCollectionName()).document(String.valueOf(chatId));

        CollectionReference messageRef = docRef.collection(FirestoreCollection.MESSAGES.getCollectionName()).document().getParent();
        ApiFuture<QuerySnapshot> messageSnapshot = messageRef
            .whereEqualTo(MessageProperty.IS_READ.getPropertyName(), false)
            .whereEqualTo(MessageProperty.SEND_USER.getPropertyName(), sendUserId)
            .get();

        try {
            List<QueryDocumentSnapshot> messageList = messageSnapshot.get().getDocuments();
            for (QueryDocumentSnapshot document : messageList) {
                document.getReference().update(MessageProperty.IS_READ.getPropertyName(), true);
            }

        } catch (ExecutionException | InterruptedException e) {
            log.error("!!!!! FirestoreService.updateRead() - failed parse collection, sendUserId : {}", sendUserId);
            log.error("!!!!! FirestoreService.updateRead() - failed parse collection, chatId : {}", chatId);
        }
    }

    /**
     * 채팅방 나가기 요청 업데이트
     *
     * @param userId 사용자 아아디
     */
    private void updateDisconnect(long userId) {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> apiFuture = firestore
            .collection(FirestoreCollection.USER.getCollectionName())
            .document(String.valueOf(userId))
            .update("connectedChat", null);

        ApiFuture<WriteResult> apiFutureUpdateDate = firestore
            .collection(FirestoreCollection.USER.getCollectionName())
            .document(String.valueOf(userId))
            .update("connectedAt", null);
    }

    /**
     * 연결 요청 생성
     *
     * @param chatId 채팅방 아이디
     * @param userId 사용자 아이디
     */
    private void createConnect(String chatId, long userId) {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> apiFuture = firestore
            .collection(FirestoreCollection.USER.getCollectionName())
            .document(String.valueOf(userId))
            .set(ConnectDTO.of(chatId));
    }

    /**
     * 차단된 사용자 검증
     *
     * @param chatId 채팅방 아이디
     * @param userId 사용자 아이디
     */
    private void validateBlocked(String chatId, long userId) {
        ChatDTO chatDTO = getChatDTO(String.valueOf(chatId));

        if (chatDTO.isBlocked(userId)) {
            throw new BlockException(logger);
        }
    }

    /**
     * 차단된 요청 생성
     *
     * @param firestoreChat 채팅방 정보
     * @param userId        사용자 아이디
     * @return ChatDTO 채팅방 정보
     */
    private ChatDTO addRequestBlockList(ChatDTO firestoreChat, long userId) {
        if (firestoreChat.getRequestBlock() == null || firestoreChat.getRequestBlock().isEmpty()) {
            firestoreChat.addNewRequestBlock(userId);
            return firestoreChat;
        }

        firestoreChat.updateRequestBlock(userId);
        return firestoreChat;
    }

    private ChatDTO removeRequestBlockList(ChatDTO firestoreChat, long userId) {
        if (firestoreChat.getRequestBlock() == null || firestoreChat.getRequestBlock().isEmpty()) {
            return firestoreChat;
        }

        firestoreChat.removeRequestBlock(userId);
        return firestoreChat;
    }
}
