package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.entity.Banner;
import com.wannabe.app.main.data.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BannerMapper {

    Banner findBannerById(long eventId);

}
