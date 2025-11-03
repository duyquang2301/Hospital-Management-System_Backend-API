package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.dto.request.user.JoinRequest;
import com.wannabe.app.main.data.dto.request.user.UpdateUserInfoRequest;
import com.wannabe.app.main.data.dto.request.user.UpdateUserRequest;
import com.wannabe.app.main.data.state.UserState;
import com.wannabe.app.main.data.state.YnColumn;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long id;
    private String name;
    private String nickname;
    private String loginKey;
    private String loginType;
    private String dateBirth;
    private String gender;
    private List<String> category;
    private String city;
    private List<String> district;
    private LocalDateTime dateCreated;
    private LocalDateTime dateDeleted;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateLastLoggedIn;
    private String state;
    private String profilePath;
    private String verifiedYn;
    private Integer point;
    private String deviceToken;
    private String marketingTerms;
    private String phoneNumber;
    private Long imageGroupId;
    private String bannedYn;
    private LocalDateTime dateBanned;

    private String loginEmail;

    public User(JoinRequest request) {
        this.name = request.getName();
        this.nickname = request.getNickname();
        this.loginKey = request.getLoginKey();
        this.loginType = request.getLoginType();
        this.state = UserState.JOINED.getUserSateValue();
        this.gender = request.getGender();
        this.dateBirth = request.getBirth();
        this.category = request.getCategory();
        this.city = request.getLocation().getCategory();
        this.district = request.getLocation().getValueList();
        this.marketingTerms = convertYn(request.getMarketingTermsAgree());
        this.loginEmail = request.getLoginEmail();
    }

    public void updateDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void updateAdditionInfo(UpdateUserInfoRequest request) {
        this.category = request.getCategory();
        this.city = request.getLocation().getCategory();
        this.district = request.getLocation().getValueList();
    }

    public void updateProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public boolean isDeleted() {
        return hasText(state) && state.equals(UserState.DELETED.getUserSateValue());
    }

    public boolean isActiveUser() {
        return hasText(state) && state.equals(UserState.JOINED.getUserSateValue());
    }

    public boolean isBannedUser() {
        return hasText(bannedYn) && bannedYn.equals("Y");
    }

    public void updateWithdrawal(long withdrawalSeq) {
        this.state = UserState.DELETED.getUserSateValue();
        this.nickname = this.nickname + "_000" + withdrawalSeq;
        this.name = null;
        this.loginType = null;
        this.loginKey = null;
        this.gender = null;
        this.dateBirth = null;
        this.category = null;
        this.point = 0;
        this.deviceToken = null;
        this.phoneNumber = null;
        this.city = null;
        this.district = null;
        this.verifiedYn = YnColumn.FALSE.getYnColumnValue();
        this.marketingTerms = YnColumn.FALSE.getYnColumnValue();
        this.imageGroupId = null;
    }

    public void updateUser(UpdateUserRequest request) {
        this.nickname = request.getNickname();
        this.name = request.getName();
        this.phoneNumber = request.getPhoneNumber();
        this.dateBirth = request.getBirth();
        this.gender = request.getGender();
    }

    public void updateImageGroupId(Long imageGroupId) {
        this.imageGroupId = imageGroupId;
    }

    private String convertYn(boolean value) {
        return value ? YnColumn.TRUE.getYnColumnValue() : YnColumn.FALSE.getYnColumnValue();
    }

    private boolean convertBoolean(String value) {
        return hasText(value) && value.equals(YnColumn.TRUE.getYnColumnValue());
    }

    private boolean hasText(String value) {
        return StringUtils.hasText(value);
    }
}
