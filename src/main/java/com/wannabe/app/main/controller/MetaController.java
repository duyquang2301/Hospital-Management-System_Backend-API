package com.wannabe.app.main.controller;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.response.meta.MetaDepthListResponse;
import com.wannabe.app.main.data.dto.response.meta.StringListResponse;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.MetaService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meta")
public class MetaController {

    private final MetaService metaService;

    @GetMapping("/surgery/part")
    @Operation(summary = "선호 성형부위 조회(1뎁스)")
    @AnonymousCallable
    public Callable<Response<StringListResponse>> getSurgeryPartList() {
        return () -> new Response<>(new StringListResponse(metaService.getSurgeryPartList()));
    }

    @GetMapping("/surgery/part/detail")
    @Operation(summary = "선호 성형부위 조회(2뎁스)")
    @AnonymousCallable
    public Callable<Response<MetaDepthListResponse>> getDetailSurgeryPartList() {
        return () -> new Response<>(new MetaDepthListResponse(metaService.getDetailSurgeryPartList()));
    }

    @GetMapping("/location")
    @Operation(summary = "지역 정보 조회")
    @AnonymousCallable
    public Callable<Response<MetaDepthListResponse>> getMetaLocationList() {
        return () -> new Response<>(new MetaDepthListResponse(metaService.getLocationList()));
    }

    @GetMapping("/hospital/feature")
    @Operation(summary = "병원 특징 조회")
    @AnonymousCallable
    public Callable<Response<StringListResponse>> getHospitalFeatureList() {
        return () -> new Response<>(new StringListResponse(metaService.getHospitalFeatureList()));
    }
}
