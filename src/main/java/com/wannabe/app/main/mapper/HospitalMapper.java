package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.common.CommonDto.HospitalInfo;
import com.wannabe.app.main.data.dto.common.Filter;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetHospitalDetailDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetHospitalEventsDto;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.entity.Hospital;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface HospitalMapper {

    List<Hospital> getHospitals(@Param("filter") Filter filter);

    List<Hospital> getHospitalsExposedRankFirstSorting(@Param("filter") Filter filter);

    Hospital getHospital(@Param("hospitalId") Long hospitalId);

    String getHospitalThumbNail(@Param("groupId") Long groupId);

    GetHospitalDetailDto getHospitalDetail(@Param("hospitalId") Long hospitalId);

    List<GetHospitalEventsDto> getHospitalEvents(
        @Param("hospitalId") Long hospitalId,
        @Param("page") int page,
        @Param("size") int size,
        @Param("sort") String sort,
        @Param("category") List<String> category
    );

    int updateConsultCount(Hospital hospital);

    HospitalInfo findActiveHospitalInfo(long hospitalId);

    Hospital findHospitalBookmarkByHospitalId(
        @Param("hospitalId") Long hospitalId
    );

    long countAll(@Param("filter") Filter filter);

    List<Article> getEventReviewByHospitalId(
        @Param("hospitalId") long hospitalId,
        @Param("page") int page,
        @Param("size") int size,
        @Param("sort") String sort,
        @Param("category") List<String> category
    );

    long countEventReviewByHospitalId(@Param("hospitalId") long hospitalId, @Param("category") List<String> category);

}
