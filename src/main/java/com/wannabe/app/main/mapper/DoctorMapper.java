package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.common.CommonDto.DoctorInfo;
import com.wannabe.app.main.data.dto.doctor.DoctorDTO;
import com.wannabe.app.main.data.dto.doctor.DoctorDetailDTO;
import com.wannabe.app.main.data.entity.Article;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface DoctorMapper {

    List<DoctorDTO> getDoctors(
        @Param("query") String query,
        @Param("city") String city,
        @Param("district") Set<String> district,
        @Param("category") Set<String> category,
        @Param("page") int page,
        @Param("size") int size
    );

    DoctorDetailDTO getDoctor(
        @Param("doctorId") long doctorId
    );

    List<Article> getEventReviewByHospitalId(@Param("hospitalId") long hospitalId);

    List<DoctorInfo> getDoctorsByHospitalId(@Param("hospitalId") long hospitalId);

    long countAll(
        @Param("query") String query,
        @Param("city") String city,
        @Param("district") Set<String> district,
        @Param("category") Set<String> category
    );
}
