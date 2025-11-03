package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.EventImage;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventImageMapper {

    List<EventImage> findEventImagesByEventId(Long eventImageGroupId);
}
