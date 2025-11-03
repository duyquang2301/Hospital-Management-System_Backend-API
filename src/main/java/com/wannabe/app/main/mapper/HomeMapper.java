package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.entity.Banner;
import com.wannabe.app.main.data.entity.Hospital;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HomeMapper {

    List<GetEventDto> findAllPopularEvent(
        @Param("page") int page,
        @Param("size") int size
    );

    List<Hospital> findAllPopularHospital(
        @Param("page") int page,
        @Param("size") int size
    );

    List<Article> findAllPopularReviews(
        @Param("page") int page,
        @Param("size") int size
    );

    // TODO LIMIT 20 으로 고정 할 것인지?
    List<Banner> findAllBanners();

    List<Banner> findAllMainBanners();
}
