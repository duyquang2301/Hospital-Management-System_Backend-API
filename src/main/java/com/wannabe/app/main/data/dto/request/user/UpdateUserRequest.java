package com.wannabe.app.main.data.dto.request.user;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    private String nickname;
    private String name;
    private String phoneNumber;
    private String gender;
    private String birth;
}
