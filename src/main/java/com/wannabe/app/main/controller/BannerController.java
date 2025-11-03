package com.wannabe.app.main.controller;

import com.wannabe.app.main.data.dto.response.event.BannerDetailResponse;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.AuthService;
import com.wannabe.app.main.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/banners")
@Tag(name = "Banner")
public class BannerController {

    private final AuthService authService;
    private final BannerService bannerService;

    @GetMapping("/{bannerId}")
    @Operation(summary = "배너 상세 조회")
    public Callable<Response<BannerDetailResponse>> getBanner(@PathVariable long bannerId) {

        BannerDetailResponse result = bannerService.getBannerById(bannerId);

        return () -> Response.of(result);

    }

}
