package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.home.BannerDto;
import com.wannabe.app.main.data.dto.banner.BannerResponse;
import com.wannabe.app.main.data.dto.response.event.BannerDetailResponse;
import com.wannabe.app.main.data.entity.Banner;
import com.wannabe.app.main.data.entity.Files;
import com.wannabe.app.main.mapper.*;
import com.wannabe.app.main.response.ListResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BannerService {

    private final BannerMapper bannerMapper;
    private final FilesMapper filesMapper;
    private final CloudFrontService cloudFrontService;

    public BannerDetailResponse getBannerById(Long bannerId) {

        Banner banner = bannerMapper.findBannerById(bannerId);
        banner.setUrl2(findFileAndMakeSignedUrl(banner.getBannerLinkImageGroupId()));

        BannerDetailResponse result = BannerDetailResponse.builder()
            .id(banner.getId())
            .image(findFileAndMakeSignedUrl(banner.getImageGroupId()))
            .image2(findFileAndMakeSignedUrl(banner.getBannerLinkImageGroupId()))
            .bannerLinkYn(banner.getBannerLinkYn())
            .hospitalId(banner.getHospitalId())
            .build();

        return result;
    }

    private String findFileAndMakeSignedUrl(Long groupId) {

        if (Objects.isNull(groupId)) {
            return null;
        }

        Files file = Optional.ofNullable(filesMapper.findFileByGroupId(groupId)).orElse(new Files());
        return cloudFrontService.generateSignedUrl(file.getPath());

    }

}
