package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.common.CommonDto.HospitalInfo;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.dto.event.PromotionBannerDTO;
import com.wannabe.app.main.data.dto.event.PromotionFilter;
import com.wannabe.app.main.data.dto.request.event.EventCounselRequest;
import com.wannabe.app.main.data.dto.request.event.EventIdRequest;
import com.wannabe.app.main.data.dto.response.event.EventDetailResponse;
import com.wannabe.app.main.data.dto.response.event.PromotionElementResponse;
import com.wannabe.app.main.data.dto.response.event.PromotionListResponse;
import com.wannabe.app.main.data.entity.Bookmark;
import com.wannabe.app.main.data.entity.Counsel;
import com.wannabe.app.main.data.entity.Event;
import com.wannabe.app.main.data.entity.EventHistory;
import com.wannabe.app.main.data.entity.EventImage;
import com.wannabe.app.main.data.entity.PromotionGroup;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.data.state.CounselType;
import com.wannabe.app.main.exception.found.NotFoundEventException;
import com.wannabe.app.main.exception.found.NotFoundPromotionException;
import com.wannabe.app.main.exception.paramter.InvalidCounselException;
import com.wannabe.app.main.mapper.CounselMapper;
import com.wannabe.app.main.mapper.EventHistoryMapper;
import com.wannabe.app.main.mapper.EventMapper;
import com.wannabe.app.main.mapper.PromotionGroupMapper;
import com.wannabe.app.main.response.ListResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final PromotionGroupMapper promotionGroupMapper;
    private final EventHistoryMapper eventHistoryMapper;
    private final CounselMapper counselMapper;

    private final HospitalService hospitalService;
    private final EventImageService eventImageService;
    private final BookmarkService bookmarkService;
    private final CloudFrontService cloudFrontService;

    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 이벤트 상담 신청
     *
     * @param user                사용자 정보
     * @param eventId             이벤트 아이디
     * @param eventCounselRequest 이벤트 신청 내용
     */
    @Transactional
    public void createEventCounsel(User user, long eventId, EventCounselRequest eventCounselRequest) {
        validateCounselRequest(eventCounselRequest);
        Event activeEvent = getActiveEvent(eventId);

        createCounsel(activeEvent, user.getId(), eventCounselRequest.getNotes());
    }

    /**
     * 이벤트 목록 조회
     *
     * @param filter 조회를 위한 필터
     * @return ListResponseDto<GetEventDto> 이벤트 목록
     */
    public ListResponseDto<GetEventDto> getEvents(PromotionFilter filter) {
        List<GetEventDto> events = eventMapper.getEvents(filter).stream()
            .peek(event -> event.setThumbNail(cloudFrontService.generateSignedUrl(event.getThumbNail()))).toList();
        return ListResponseDto.of(events, getEventCount(filter));
    }

    /**
     * 이벤트 스크랩 등록
     *
     * @param user    사용자 정보
     * @param eventId 이벤트 아이디
     */
    public void scrapEvent(User user, long eventId) {
        addEventBookmark(user.getId(), getActiveEvent(eventId));
    }

    /**
     * 이벤트 스크랩 삭제
     *
     * @param userId  사용자 아이디
     * @param eventId 이벤트 아이디
     */
    public void deleteEvent(long userId, long eventId) {
        cancelEventBookmark(userId, eventId);
    }

    /**
     * 이벤트 상세 조회
     *
     * @param eventId 이벤트 이이디
     * @param user    사용자 정보
     * @param request 이벤트 Interaction 를 위한 정보
     * @return EventDetailResponse 이벤트 정보
     */
    @Transactional
    public EventDetailResponse getEventDetail(long eventId, User user, EventIdRequest request) {
        insertEventHistory(eventId, request);
        increaseEventViewCount(eventId);
        return buildEventDetailResponse(eventId, user.getId());
    }

    /**
     * 이벤트 상세 조회
     *
     * @param eventId 이벤트 이이디
     * @return EventDetailResponseSingle 이벤트 정보
     */
    public EventDetailResponse getEventDetailSingle(long eventId) {
        return buildEventDetailSingleResponse(eventId);
    }

    /**
     * 많이 본 추천 이벤트 조회
     *
     * @param eventId 이벤트 아이디
     * @return ListResponseDto<GetEventDto> 이벤트 목록
     */
    public ListResponseDto<GetEventDto> getRecommendEvents(long eventId) {
        getEvent(eventId);
        List<GetEventDto> recommendEvents = eventMapper.findRecommendEvents(eventId).stream()
            .peek(event -> event.setThumbNail(cloudFrontService.generateSignedUrl(event.getThumbNail()))).toList();

        return ListResponseDto.from(recommendEvents);
    }

    /**
     * 기획전 목록
     *
     * @param size   가져올 목록 개수
     * @param cursor 기획전 그룹 아이디
     * @return PromotionListResponse 기획전 목록
     */
    public PromotionListResponse getPromotionList(int size, Long cursor) {
        List<PromotionGroup> promotionGroupList = promotionGroupMapper.findPromotionGroupList(size, cursor);

        if (promotionGroupList == null || promotionGroupList.isEmpty()) {
            return PromotionListResponse.of();
        }

        return PromotionListResponse.of(buildPromotionElementList(promotionGroupList));
    }

    /**
     * 기획전 상세 조회
     *
     * @param promotionId 기획전 아이디
     * @param filter      정렬 필터
     * @return PromotionElementResponse 기획전 상세 내용
     */
    public PromotionElementResponse getPromotion(long promotionId, PromotionFilter filter) {
        PromotionGroup promotionGroup = promotionGroupMapper.findPromotionGroupById(promotionId);

        if (promotionGroup == null) {
            throw new NotFoundPromotionException(logger);
        }

        Long totalCount = findPromotionTotalCount(filter);

        return PromotionElementResponse.of(promotionGroup, totalCount, getEventDtoListByPromotionFilter(filter));
    }

    /**
     * 기획전 총 개수
     *
     * @param filter 정렬 필터
     * @return Long 기획전 개수
     */
    private Long findPromotionTotalCount(PromotionFilter filter) {
        return eventMapper.findPromotionGroupCount(filter);
    }

    /**
     * 기획전 배너 조회
     *
     * @return PromotionBannerDTO 기획전 배너 이미지
     */
    public PromotionBannerDTO getPromotionBanner() {
        List<PromotionBannerDTO> exposedRankFirstPromotionBannerList = promotionGroupMapper.findExposedRankFirstPromotionBannerList();

        if (exposedRankFirstPromotionBannerList == null || exposedRankFirstPromotionBannerList.isEmpty()) {
            return PromotionBannerDTO.of();
        }

        return getRandomPromotionBanner(exposedRankFirstPromotionBannerList);
    }

    /**
     * 이벤트 개수
     *
     * @param filter 정렬 필터
     * @return Long 이벤트 개수
     */
    private Long getEventCount(PromotionFilter filter) {
        return eventMapper.findEventCount(filter);
    }

    /**
     * 랜덤 기획전 배너 목록
     *
     * @param exposedRankFirstPromotionBannerList 기획전 배너 목록
     * @return PromotionBannerDTO 기획전 배너
     */
    private PromotionBannerDTO getRandomPromotionBanner(List<PromotionBannerDTO> exposedRankFirstPromotionBannerList) {
        if (exposedRankFirstPromotionBannerList.size() == 1) {
            return getPromotionBannerUpdateSignedUrl(exposedRankFirstPromotionBannerList.get(0));
        }

        int randomIndex = (int) (Math.random() * exposedRankFirstPromotionBannerList.size());
        return getPromotionBannerUpdateSignedUrl(exposedRankFirstPromotionBannerList.get(randomIndex));
    }

    /**
     * 기획전 배너 이미지 Generate S3 Url
     *
     * @param promotionBannerDTO 배너 정보
     * @return 배너 정보
     */
    private PromotionBannerDTO getPromotionBannerUpdateSignedUrl(PromotionBannerDTO promotionBannerDTO) {
        promotionBannerDTO.updateSignedUrl(cloudFrontService.generateSignedUrl(promotionBannerDTO.getPath()));
        return promotionBannerDTO;
    }

    /**
     * 이벤트 조회수 업데이트
     *
     * @param eventId 이벤트 아이디
     */
    private void increaseEventViewCount(long eventId) {
        eventMapper.increaseEventViewCount(eventId);
    }

    /**
     * 기획전 목록
     *
     * @param promotionGroupList 기획전 목록
     * @return List<PromotionElementResponse> 기획전 목록
     */
    private List<PromotionElementResponse> buildPromotionElementList(List<PromotionGroup> promotionGroupList) {
        if (promotionGroupList == null || promotionGroupList.isEmpty()) {
            return new ArrayList<>();
        }

        List<PromotionElementResponse> promotionElementResponseList = new ArrayList<>();

        for (PromotionGroup promotionGroup : promotionGroupList) {
            List<GetEventDto> list = getExposedEventsByPromotionGroupId(promotionGroup.getId());

            if (list.isEmpty()) {
                continue;
            }

            promotionElementResponseList.add(PromotionElementResponse.of(promotionGroup, list));
        }

        return promotionElementResponseList;
    }

    /**
     * 기획전에 속한 이벤트 목록
     *
     * @param promotionGroupId 기획전 아이디
     * @return List<GetEventDto> 이벤트 목록
     */
    private List<GetEventDto> getExposedEventsByPromotionGroupId(long promotionGroupId) {
        return eventMapper.findExposedEventsByPromotionGroupId(promotionGroupId).stream()
            .peek(event -> event.setThumbNail(cloudFrontService.generateSignedUrl(event.getThumbNail()))).toList();
    }

    /**
     * 정렬 필터를 이용한 이벤트 목록
     *
     * @param filter 정렬 필터
     * @return List<GetEventDto> 이벤트 목록
     */
    private List<GetEventDto> getEventDtoListByPromotionFilter(PromotionFilter filter) {
        return eventMapper.findEventsByPromotionFilter(filter).stream()
            .peek(event -> event.setThumbNail(cloudFrontService.generateSignedUrl(event.getThumbNail()))).toList();
    }

    /**
     * 이벤트 이력 저장
     *
     * @param eventId            이벤트 아이디
     * @param beforeEventRequest 바로 직전 조회한 이벤트 아이디
     */
    private void insertEventHistory(long eventId, EventIdRequest beforeEventRequest) {
        if (beforeEventRequest == null || beforeEventRequest.getBeforeEventId() == null) {
            return;
        }

        getEvent(eventId);
        getEvent(beforeEventRequest.getBeforeEventId());

        eventHistoryMapper.insertEventHistory(EventHistory.of(eventId, beforeEventRequest.getBeforeEventId()));
    }

    /**
     * Active 상태인 이벤트 조회
     *
     * @param eventId 이벤트 아이디
     * @return Event 정보
     */
    public Event getActiveEvent(long eventId) {
        Event findEvent = findActiveEvent(eventId);

        if (findEvent != null) {
            return findEvent;
        }

        log.error("!!!!!! EventService.getActiveEvent() - findEvent is null. eventId: {}", eventId);
        throw new NotFoundEventException(logger);
    }

    /**
     * 이벤트 상세 정보
     *
     * @param eventId 이벤트 아이디
     * @param userId  사용자 아이디
     * @return EventDetailResponse 이벤트 상세 정보
     */
    private EventDetailResponse buildEventDetailResponse(long eventId, long userId) {
        Event activeEvent = getActiveEvent(eventId);
        List<EventImage> eventImages = getEventImages(activeEvent.getImageGroupId());
        Optional<Bookmark> eventClip = findEventBookmark(userId, eventId);

        return EventDetailResponse.builder()
            .id(activeEvent.getId())
            .thumbnail(getSignedEventThumbnail(eventId))
            .name(activeEvent.getName())
            .counselCount(activeEvent.getConsultCount())
            .cost(activeEvent.getPrice())
            .hospitalInfo(buildActiveHospitalInfo(activeEvent.getHospitalId()))
            .startDate(activeEvent.getDateStarted())
            .endDate(activeEvent.getDateEnd())
            .eventImage(buildEventImages(eventImages))
            .isBookMark(YN.of(isBookMark(eventClip)))
            .build();
    }

    /**
     * 이벤트 상세 정보
     *
     * @param eventId 이벤트 아이디
     * @return EventDetailResponseSingle 이벤트 상세 정보
     */
    private EventDetailResponse buildEventDetailSingleResponse(long eventId) {
        Event activeEvent = getActiveEvent(eventId);
        List<EventImage> eventImages = getEventImages(activeEvent.getImageGroupId());
        return EventDetailResponse.builder()
            .id(activeEvent.getId())
            .thumbnail(getSignedEventThumbnail(eventId))
            .name(activeEvent.getName())
            .counselCount(activeEvent.getConsultCount())
            .cost(activeEvent.getPrice())
            .hospitalInfo(buildActiveHospitalInfo(activeEvent.getHospitalId()))
            .startDate(activeEvent.getDateStarted())
            .endDate(activeEvent.getDateEnd())
            .eventImage(buildEventImages(eventImages))
            .isBookMark(YN.of(false))
            .build();
    }


    /**
     * 이벤트 이미지 Generate S3 Url
     *
     * @param eventImages 이벤트 이미지 목록
     * @return List<String> 이벤트 이미지 S3 Url 목록
     */
    private List<String> buildEventImages(List<EventImage> eventImages) {
        return eventImages.stream()
            .map(EventImage::getPath)
            .map(this::makePreSignedUrl)
            .toList();
    }

    /**
     * 이벤트 스크랩 여부
     *
     * @param eventBookmark 스크랩 정보
     * @return 스크랩 여부
     */
    private boolean isBookMark(Optional<Bookmark> eventBookmark) {
        return eventBookmark.isPresent();
    }

    /**
     * 이벤트 썸네일 생성
     *
     * @param eventId 이벤트 아이디
     * @return String 이벤트 썸네일
     */
    private String getSignedEventThumbnail(long eventId) {
        GetEventDto eventDTO = getEventDtoById(eventId);
        return makePreSignedUrl(eventDTO.getThumbNail());
    }

    /**
     * S3 Url 생성
     *
     * @param path 이미지 저장 경로
     * @return String S3 Url
     */
    private String makePreSignedUrl(String path) {
        if (!hasText(path)) {
            return "";
        }

        return cloudFrontService.generateSignedUrl(path);
    }

    /**
     * 이벤트 스크랩 조회
     *
     * @param userId  사용자 아이디
     * @param eventId 이벤트 아이디
     * @return Optional<Bookmark> 스크랩 내용
     */
    private Optional<Bookmark> findEventBookmark(long userId, long eventId) {
        return bookmarkService.findEventBookmark(userId, eventId);
    }

    /**
     * 이벤트 이미지 조회
     *
     * @param eventImageGroupId 이벤트 이미지 그룹 아이디 조회
     * @return List<EventImage> 이벤트 이미지
     */
    private List<EventImage> getEventImages(Long eventImageGroupId) {
        return eventImageService.getEventImages(eventImageGroupId);
    }

    /**
     * 이벤트 스크랩 취소
     *
     * @param userId  사용자 아이디
     * @param eventId 이벤트 아이디
     */
    private void cancelEventBookmark(long userId, long eventId) {
        bookmarkService.cancelEventBookmark(userId, eventId);
    }

    /**
     * 이벤트 상담 신청 수 업데이트
     *
     * @param event 이벤트 정보
     */
    private void increaseConsultCount(Event event) {
        event.increaseConsultCount();
        eventMapper.increaseConsultCount(event);
    }

    /**
     * 이벤트 아이디 로 상태가 ACTIVE 인 이벤트 조회
     *
     * @param eventId 이벤트 아이디
     * @return 이벤트 정보
     */
    private Event findActiveEvent(long eventId) {
        return eventMapper.findActiveEventById(eventId);
    }

    /**
     * 이벤트 조회
     *
     * @param eventId 이벤트 아이디
     * @return Event 이벤트 정보
     */
    private Event getEvent(long eventId) {
        Event findEvent = findEvent(eventId);

        if (findEvent != null) {
            return findEvent;
        }

        log.error("!!!!!! EventService.getEvent() - findEvent is null. eventId: {}", eventId);
        throw new NotFoundEventException(logger);
    }

    /**
     * 이벤트 조회
     *
     * @param eventId 이벤트 아이디
     * @return Event 이벤트 정보
     */
    private Event findEvent(long eventId) {
        return eventMapper.findEventById(eventId);
    }

    /**
     * 이벤트 정보 조회
     *
     * @param eventId 이벤트 아이디
     * @return GetEventDto 이벤트 정보
     */
    private GetEventDto getEventDtoById(long eventId) {
        GetEventDto eventDtoById = findEventDtoById(eventId);

        if (eventDtoById != null) {
            return eventDtoById;
        }

        log.error("!!!!!! EventService.getEventDtoById() - eventDtoById is null. eventId: {}", eventId);
        throw new NotFoundEventException(logger);
    }

    /**
     * 이벤트 아이디로 이벤트 정보 조회
     *
     * @param eventId 이벤트 아이디
     * @return GetEventDto 이벤트 정보
     */
    private GetEventDto findEventDtoById(long eventId) {
        return eventMapper.findEventDtoById(eventId);
    }

    /**
     * 이벤트 상담 신청 업데이트
     *
     * @param event  이벤트 정보
     * @param userId 사용자 아이디
     * @param note   이벤트 상담 신청 내용
     */
    private void insertCounselEvent(Event event, long userId, String note) {
        Counsel counsel = new Counsel(
            userId,
            CounselType.EVENT,
            event.getId(),
            note
        );

        counselMapper.insertEventCounsel(counsel);
    }

    /**
     * 상담 신청 생성
     *
     * @param event  이벤트 정보
     * @param userId 사용자 아이디
     * @param note   이벤트 상담 신청 내용
     */
    private void createCounsel(Event event, long userId, String note) {
        insertCounselEvent(event, userId, note);
        increaseConsultCount(event);
    }

    /**
     * 이벤트 상담 내용 검증
     *
     * @param request 이벤트 상담 신청 내용
     */
    private void validateCounselRequest(EventCounselRequest request) {
        if (request == null) {
            throw new InvalidCounselException(logger);
        }
    }

    /**
     * 병원 정보 조회
     *
     * @param hospitalId 병원 아이디
     * @return HospitalInfo 병원 정보
     */
    private HospitalInfo buildActiveHospitalInfo(long hospitalId) {
        HospitalInfo activeHospitalInfo = hospitalService.getActiveHospitalInfo(hospitalId);
        activeHospitalInfo.setThumbNail(activeHospitalInfo.getThumbNail());
        return activeHospitalInfo;
    }

    /**
     * 이벤트 스크랩 등록
     *
     * @param userId 사용자 아이디
     * @param event  이벤트 정보
     */
    private void addEventBookmark(long userId, Event event) {
        bookmarkService.addEventBookmark(userId, event.getId());
    }

    private boolean hasText(String value) {
        return StringUtils.hasText(value);
    }

    /**
     * 모든 이벤트 목록
     *
     * @param page 현재 페이지
     * @param size 가져올 사이즈
     * @return ListResponseDto<Event> 이벤트 목록
     */
    public ListResponseDto<Event> getAllEventList(int page, int size) {
        List<Event> eventList = eventMapper.findAll(page, size);
        return ListResponseDto.of(eventList, eventMapper.countAll());
    }

}
