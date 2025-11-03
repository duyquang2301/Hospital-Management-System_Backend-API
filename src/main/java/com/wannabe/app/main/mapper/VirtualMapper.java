package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.virutality.GetMyVirtualSurgeryDto;
import com.wannabe.app.main.data.entity.VirtualSurgery;
import com.wannabe.app.main.data.entity.VirtualSurgeryPart;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VirtualMapper {

    Optional<VirtualSurgery> findVirtualSurgeryByUserId(
        @Param("id") long id,
        @Param("userId") long userId
    );

    VirtualSurgery findVirtualSurgeryById(long id);

    void insertVirtualSurgery(VirtualSurgery virtualSurgery);

    void updateGroupIds(VirtualSurgery virtualSurgery);

    void updateVirtualSurgeryImage(VirtualSurgery virtualSurgery);

    void deleteVirtualCategory(@Param("virtualId") long virtualId);

    void updateOnSideGroupIds(VirtualSurgery virtualSurgery);

    void insertVirtualCategory(VirtualSurgeryPart virtualSurgeryPart);

    int updateCounselState(VirtualSurgery virtualSurgery);

    List<VirtualSurgeryPart> findVirtualSurgeryPartByVirtualId(long virtualId);

    List<VirtualSurgery> findAllByUserId(Long userId, int page, int size);

    void deleteVirtualSurgeryById(@Param("virtualId") Long virtualId);

    int countAllByUserId(@Param("userId") long userId);

    List<GetMyVirtualSurgeryDto> findCounselByUserId(
        @Param("userId") long userId,
        @Param("page") int page,
        @Param("size") int size
    );

}
