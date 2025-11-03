package com.wannabe.app.main.service;

import com.wannabe.app.main.data.entity.EventImage;
import com.wannabe.app.main.exception.found.NotFoundEventException;
import com.wannabe.app.main.mapper.EventImageMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventImageService {


    private final EventImageMapper eventImageMapper;
    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 이벤트 이미지 파일 목록 조회
     *
     * @param eventImageGroupId 이벤트 이미지 그룹 아이디
     * @return List<EventImage> 이벤트 이미지 파일 목록
     */
    public List<EventImage> getEventImages(Long eventImageGroupId) {
        List<EventImage> eventImages = findEventImages(eventImageGroupId);

        if (eventImages != null && !eventImages.isEmpty()) {
            return eventImages;
        }

        logger.error("EventImageService.getEventImages: eventImages is null or empty");
        throw new NotFoundEventException(logger);
    }

    /**
     * 이벤트 이미지 파일 목록 조회
     *
     * @param eventImageGroupId 이벤트 이미지 그룹 아이디
     * @return List<EventImage> 이벤트 이미지 파일 목록
     */
    private List<EventImage> findEventImages(Long eventImageGroupId) {
        return eventImageMapper.findEventImagesByEventId(eventImageGroupId);
    }
}
