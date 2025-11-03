package com.wannabe.app.main.controller;


import com.wannabe.app.main.data.dto.term.response.TermDetailResponse;
import com.wannabe.app.main.data.entity.Term;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/term")
public class TermController {

    private final TermService termService;

    @GetMapping("/{id}")
    public Callable<Response<TermDetailResponse>> getTerm(@PathVariable Long id) {

        return () -> {


            Term result = termService.findTermByTermId(id);
            return Response.of(TermDetailResponse.from(result));

        };

    }
}
