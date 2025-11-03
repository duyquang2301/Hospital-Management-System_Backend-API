package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.Term;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TermMapper {

    Term findTermByTermId(@Param("termId") Long termId);

}
