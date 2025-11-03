package com.wannabe.app.main.service;

import com.wannabe.app.main.data.entity.Bookmark;
import com.wannabe.app.main.data.state.BookmarkType;
import com.wannabe.app.main.exception.found.NotFoundBookmarkException;
import com.wannabe.app.main.exception.paramter.AlreadyBookmarkException;
import com.wannabe.app.main.mapper.BookmarkMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkMapper bookmarkMapper;

    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 이벤트 스크랩 등록
     *
     * @param userId  사용자 아아디
     * @param eventId 이벤트 아이디
     */
    public void addEventBookmark(long userId, long eventId) {
        Bookmark eventBookmark = buildEventBookmark(userId, eventId);

        if (isAlreadyBookmarked(eventBookmark)) {
            logger.error("!!!!!! Already bookmarked event. userId: {}, eventId: {}", userId, eventId);
            throw new AlreadyBookmarkException(logger);
        }

        insertBookmark(eventBookmark);
    }

    /**
     * 이벤트 스크랩 해제
     *
     * @param userId  사용자 아이디
     * @param eventId 이벤트 아이디
     */
    public void cancelEventBookmark(long userId, long eventId) {
        Optional<Bookmark> eventBookmark = findEventBookmark(userId, eventId);

        if (eventBookmark.isEmpty()) {
            logger.error("!!!!!! BookmarkService.cancelEventBookmark bookmark not found : userId: {}, eventId: {}", userId, eventId);
            throw new NotFoundBookmarkException(logger);
        }

        bookmarkMapper.deleteBookmark(eventBookmark.get());
    }

    /**
     * 이벤트 스크랩 정보 조회
     *
     * @param userId  사용자 아이디
     * @param eventId 이벤트 아이디
     * @return Optional<Bookmark> 스크랩 정보
     */
    public Optional<Bookmark> findEventBookmark(long userId, long eventId) {
        return findBookmark(buildEventBookmark(userId, eventId));
    }

    /**
     * 스크랩 존재 여부
     *
     * @param bookmark 스크랩 정보
     * @return 스크랩 존재 여부
     */
    private boolean isAlreadyBookmarked(Bookmark bookmark) {
        Optional<Bookmark> foundBookmark = findBookmark(bookmark);
        return foundBookmark.isPresent();
    }

    /**
     * 스크랩 객체 생성
     *
     * @param userId  사용자 아이디
     * @param eventId 이벤트 아이디
     * @return 스크랩 객체
     */
    private Bookmark buildEventBookmark(long userId, long eventId) {
        return new Bookmark(userId, eventId, BookmarkType.EVENT.getBookmarkType());
    }

    /**
     * 스크랩 정보 조회
     *
     * @param bookmark 스크랩 정보
     * @return Optional<Bookmark> 스크랩 접보
     */
    private Optional<Bookmark> findBookmark(Bookmark bookmark) {
        return bookmarkMapper.findBookmark(bookmark);
    }

    /**
     * 스크랩 등록
     *
     * @param bookmark 스크랩 정보
     */
    private void insertBookmark(Bookmark bookmark) {
        bookmarkMapper.insertBookmark(bookmark);
    }
}
