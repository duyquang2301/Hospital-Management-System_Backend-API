package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.Counsel;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CounselMapper {

    void insertVirtualCounsel(Counsel counsel);

    void insertHospitalCounsel(Counsel counsel);

    void insertEventCounsel(Counsel counsel);

    void updateImageGroupId(@Param("counselId") Long counselId, @Param("imageGroupId") Long imageGroupId);

    List<Counsel> findCounselByVirtualId(
        @Param("virtualId") Long virtualId,
        @Param("userId") Long userId
    );

    List<Counsel> findAllCounselByUserId(
        @Param("userId") Long userId,
        @Param("page") int page,
        @Param("size") int size
    );

    List<Counsel> findAllCounselByUserIdAndId(
        @Param("userId") Long userId,
        @Param("virtualId") Long virtualId,
        @Param("page") int page,
        @Param("size") int size
    );

    Optional<Counsel> findEventCounselByUserIdAndId(
        @Param("userId") Long userId,
        @Param("counselId") Long counselId
    );

    Optional<Counsel> findCounselByUserIdAndId(
        @Param("userId") Long userId,
        @Param("counselId") Long counselId
    );

    long countAllByUserId(@Param("userId") Long userId);

    long countAllByUserIdAndVirtualId(@Param("userId") Long userId, @Param("virtualId") Long virtualId);
}
