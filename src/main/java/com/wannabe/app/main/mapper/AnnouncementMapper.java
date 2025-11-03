package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    List<Announcement> getAnnouncementsActive();

}
