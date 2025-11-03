package com.wannabe.app.main.data.dto.user;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.entity.Counsel;
import com.wannabe.app.main.data.entity.Hospital;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.data.state.CounselState;
import com.wannabe.app.main.data.state.CounselType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyHospitalCounselDto {

    private Long id;
    private Long hospitalId;
    private String thumbNail;
    private String name;
    private Region region;
    private LocalDateTime createAt;
    private CounselState state;
    private String nickName;
    private String phoneNumber;
    private String content;
    private String answer;
    private List<String> surgeryPart;
    private List<String> image;
    private String beforeImage;
    private String afterImage;
    private String leftImage;
    private String rightImage;
    private CounselType type;

    public static MyHospitalCounselDto of(
        Counsel counsel,
        Hospital hospital,
        String hospitalImg,
        User user,
        List<String> image,
        String beforeImage,
        String afterImage,
        String leftImage,
        String rightImage
    ) {
        return new MyHospitalCounselDto(
            counsel.getId(),
            hospital.getId(),
            hospitalImg,
            hospital.getName(),
            Region.of(hospital.getCity(), hospital.getDistrict()),
            counsel.getCreatedAt(),
            counsel.getState(),
            user.getNickname(),
            user.getPhoneNumber(),
            counsel.getDescription(),
            counsel.getCounselAnswer(),
            counsel.getCategory(),
            image,
            beforeImage,
            afterImage,
            leftImage,
            rightImage,
            counsel.getCounselType()
        );
    }
}
