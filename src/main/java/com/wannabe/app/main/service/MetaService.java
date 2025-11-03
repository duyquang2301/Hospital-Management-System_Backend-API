package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.meta.LocationDTO;
import com.wannabe.app.main.data.dto.response.meta.DepthResponse;
import com.wannabe.app.main.data.state.Gender;
import com.wannabe.app.main.data.state.HospitalFeature;
import com.wannabe.app.main.data.state.Location;
import com.wannabe.app.main.data.state.LoginType;
import com.wannabe.app.main.data.state.SurgeryPart;
import com.wannabe.app.main.exception.paramter.InvalidGenderException;
import com.wannabe.app.main.exception.paramter.InvalidLocationException;
import com.wannabe.app.main.exception.paramter.InvalidLoginParameterException;
import com.wannabe.app.main.exception.paramter.InvalidSurgeryPartException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaService {

    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 수술/상담 부위 목록 조회
     *
     * @return List<String> 수술/상담 부위 목록
     */
    public List<String> getSurgeryPartList() {
        return getSurgeryPartInstance().getParentSurgeryPartList();
    }

    /**
     * 수술/상담 상세 부위 까지 목록 조회
     *
     * @return List<DepthResponse> 수술/상담 상세 부위
     */
    public List<DepthResponse> getDetailSurgeryPartList() {
        return getSurgeryPartInstance().getLocationDepthResponse();
    }

    /**
     * 지역 목록 조회
     *
     * @return List<DepthResponse> 지역 목록
     */
    public List<DepthResponse> getLocationList() {
        return getLocationInstance().getLocationDepthResponse();
    }

    /**
     * 병원 특징 목록 조회
     *
     * @return List<String> 병원 특징 목록
     */
    public List<String> getHospitalFeatureList() {
        return getHospitalFeatureInstance().getHospitalFeatureValueList();
    }

    /**
     * 로그인 타입 목록 조회
     *
     * @return List<String> 로그인 타입 목록
     */
    public List<String> getLoginTypeList() {
        return getLoginTypeInstance().getLoginTypes();
    }

    /**
     * 로그인 타입 검증
     *
     * @param loginType 로그인 타입
     */
    public void validateLoginType(String loginType) {
        if (hasText(loginType) && getLoginTypeList().contains(loginType)) {
            return;
        }

        throw new InvalidLoginParameterException(logger, true);
    }

    /**
     * 지역 정보 검증
     *
     * @param locationList 지역 목록
     */
    public void validateLocation(List<String> locationList) {
        locationList.forEach(this::validateLocation);
    }

    /**
     * 지역 위치 검증
     *
     * @param location 지역 위치
     */
    public void validateLocation(String location) {
        List<String> metaLocationList = getLocationInstance().getLocationList();

        if (hasText(location) && metaLocationList.contains(location)) {
            return;
        }

        throw new InvalidLocationException(logger);
    }

    /**
     * 지역 객체로 지역 정보 검증
     *
     * @param locationDTO 지역 객체
     */
    public void validateLocation(LocationDTO locationDTO) {
        validateLocationCategory(locationDTO.getCategory());
        validateLocation(locationDTO.getValueList());
    }

    /**
     * 수술/상담 부위 검증
     *
     * @param surgeryList 수술/상담 부위 목록
     */
    public void validateSurgeryPart(List<String> surgeryList) {
        surgeryList.forEach(this::validateSurgeryPart);
    }

    /**
     * 수술/상담 부위 검증
     *
     * @param surgeryPart 수술/상담 부위
     */
    public void validateSurgeryPart(String surgeryPart) {
        List<String> metaSurgeryPartList = getSurgeryPartInstance().getParentSurgeryPartList();

        if (hasText(surgeryPart) && metaSurgeryPartList.contains(surgeryPart)) {
            return;
        }

        throw new InvalidSurgeryPartException(logger);
    }

    /**
     * 성별 검증
     *
     * @param gender 성별
     */
    public void validateGender(String gender) {
        List<String> metaGenderList = getGenderInstance().getGenderValueList();

        if (hasText(gender) && metaGenderList.contains(gender)) {
            return;
        }

        throw new InvalidGenderException(logger);
    }

    /**
     * TODO 미사용
     * 사용자 지역 정보 조회
     *
     * @param userLocation 사용자 지역 정보
     * @return List<DepthResponse> 지역 정보
     */
    public List<DepthResponse> getUserLocation(List<String> userLocation) {
        return getLocationInstance().buildUserLocation(userLocation);
    }

    /**
     * TODO 미사용
     * 도시로 지역 검증
     *
     * @param locationDTO 지역 객체
     */
    private void validateLocationByCategory(LocationDTO locationDTO) {
        validateLocationCategory(locationDTO.getCategory());
        validateLocation(locationDTO.getValueList());
    }

    /**
     * 지역 도시 검증
     *
     * @param category 도시
     */
    private void validateLocationCategory(String category) {
        List<String> locationParentList = getLocationInstance().getLocationParentList();

        if (locationParentList.contains(category)) {
            return;
        }

        throw new InvalidLocationException(logger);
    }

    /**
     * 도시 초기화
     *
     * @return Location 지역 정보
     */
    private Location getLocationInstance() {
        return Location.SEOUL;
    }

    /**
     * 병원 특징 초기화
     *
     * @return HospitalFeature 병원 특징 정보
     */
    private HospitalFeature getHospitalFeatureInstance() {
        return HospitalFeature.CCTV;
    }

    /**
     * 수술/상담 부위 초기회
     *
     * @return SurgeryPart 수술/상담 부위 정보
     */
    private SurgeryPart getSurgeryPartInstance() {
        return SurgeryPart.NOSE;
    }

    /**
     * 로그인 타입 초기화
     *
     * @return LoginType 로그인 타입 정보
     */
    private LoginType getLoginTypeInstance() {
        return LoginType.APPLE;
    }

    /**
     * 성별 정보 초기화
     *
     * @return Gender 성별 정보
     */
    private Gender getGenderInstance() {
        return Gender.MAN;
    }

    private boolean hasText(String value) {
        return StringUtils.hasText(value);
    }
}
