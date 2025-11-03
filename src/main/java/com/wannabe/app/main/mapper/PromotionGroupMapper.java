package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.event.PromotionBannerDTO;
import com.wannabe.app.main.data.entity.PromotionGroup;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PromotionGroupMapper {

    List<PromotionGroup> findPromotionGroupList(
        @Param("size") int size,
        @Param("cursor") Long cursor
    );

    PromotionGroup findPromotionGroupById(long promotionGroupId);

    List<PromotionBannerDTO> findExposedRankFirstPromotionBannerList();
}
