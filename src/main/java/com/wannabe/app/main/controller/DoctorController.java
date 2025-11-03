package com.wannabe.app.main.controller;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.doctor.DoctorDTO;
import com.wannabe.app.main.data.dto.doctor.DoctorDetailDTO;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.Callable;

import static com.wannabe.app.main.utility.constant.HeaderKey.USER_ID;

@RestController
@RequestMapping(value = "/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor")
public class DoctorController {

    private final DoctorService doctorService;


    @GetMapping(value = "")
    @Operation(summary = "의사 목록")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<DoctorDTO>>> getDoctors(
        @Parameter(description = "검색어") @RequestParam(required = false) String query,
        @Parameter(description = "도시") @RequestParam(required = false) String city,
        @Parameter(description = "지역") @RequestParam(required = false) Set<String> district,
        @Parameter(description = "카테고리") @RequestParam(required = false) Set<String> category,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> Response.of(doctorService.getDoctors(query, city, district, category, page, size));
    }

    @GetMapping(value = "/{doctorId}")
    @Operation(summary = "의사 상세 조회")
    @AnonymousCallable
    public Callable<Response<DoctorDetailDTO>> getDoctor(
        @RequestAttribute(name = USER_ID, required = false) Long userId,
        @Parameter(description = "조회 할 의사 아이디") @PathVariable long doctorId
    ) {
        return () -> Response.of(doctorService.getDoctor(doctorId, userId));
    }

}
