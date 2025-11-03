package com.wannabe.app.main.data.dto.request.user;

import com.wannabe.app.main.data.dto.meta.LocationDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequest {

    private String loginType;
    private String loginKey;
    private String name;
    private String birth;
    private String gender;
    private String nickname;
    private List<String> category;
    private LocationDTO location;
    private Boolean marketingTermsAgree;

    // 20241118
    private String loginEmail;
}
