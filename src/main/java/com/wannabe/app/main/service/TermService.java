package com.wannabe.app.main.service;

import com.wannabe.app.main.data.entity.Term;
import com.wannabe.app.main.mapper.TermMapper;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TermService {

    private final TermMapper termMapper;

    public Term findTermByTermId(@Param("termId") Long termId){
        Term result = termMapper.findTermByTermId(termId);
        return result;
    }
}
