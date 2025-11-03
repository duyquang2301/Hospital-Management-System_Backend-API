package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.dto.event.PromotionFilter;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.entity.Event;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventMapper {

    Event findActiveEventById(long eventId);

    int increaseConsultCount(Event event);

    List<GetEventDto> getEvents(PromotionFilter filter);

    Long findEventCount(PromotionFilter filter);

    Long findPromotionGroupCount(PromotionFilter filter);

    Event findEventById(long eventId);

    GetEventDto findEventDtoById(long eventId);

    void increaseEventViewCount(long eventId);

    // TODO 목록 몇개 보여줄건지 물어보기
    List<Article> findByHospitalId(@Param("hospitalId") Long hospitalId);

    List<Event> findActiveEventByHospitalId(@Param("hospitalId") long hospitalId);

    List<GetEventDto> findRecommendEvents(long eventId);

    List<GetEventDto> findExposedEventsByPromotionGroupId(long promotionGroupId);

    List<GetEventDto> findEventsByPromotionFilter(PromotionFilter filter);

    GetEventDto findEventBookmarkByEventId(@Param("eventId") Long eventId);

    List<Event> findAll(@Param("page") int page, @Param("size") int size);

    long countAll();

    long countAllEvent();

    long countAllEventByHospitalId(@Param("hospitalId") long hospitalId, @Param("category") List<String> category);

}
