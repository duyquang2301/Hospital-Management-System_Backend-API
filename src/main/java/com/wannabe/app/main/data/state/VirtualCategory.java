package com.wannabe.app.main.data.state;

import com.wannabe.app.main.data.dto.request.virtuality.VirtualCategoryRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum VirtualCategory {

    EYE("눈", null),
        EYE_CORRECTION("눈매교정", EYE),
        FRONT_EFFECT("앞트임", EYE),
        BACK_SLIT("뒤트임", EYE),
        EYE_LOWER("뒷꼬리내리기", EYE),
    DOUBLE_EYELID("쌍꺼풀", null),
        IN_LINE("인라인", DOUBLE_EYELID),
        IN_OUT_LINE("인아웃라인", DOUBLE_EYELID),
        SEMI_OUT_LINE("세미아웃라인", DOUBLE_EYELID),
        OUT_LINE("아웃라인", DOUBLE_EYELID),
    NOSE("코", null),
        NOSE_RIDGE("콧대", NOSE),
        END_OF_NOSE("코끝", NOSE),
        ROMAN_NOSE_1("휜코1", NOSE),
        ROMAN_NOSE_2("휜코2", NOSE),
        ALAR_RESECTION("콧볼축소", NOSE),
    MOUTH("입", null),
        MOUTH_LENGTH("입길이", MOUTH),
        UPPER_LIP("윗입술", MOUTH),
        LOWER_LIP("아랫입술", MOUTH),
        ORAL_ANGLE("입꼬리", MOUTH),
    FACE("얼굴형", null),
        CHIN_V_LINE("턱끝V라인", FACE),
        CHIN("턱끝", FACE),
        PROMINENT_V_LINE("턱끝V라인", FACE),
        PROMINENT("사각턱", FACE),
        LOWER_CHEEKBONE("아랫광대", FACE),
        UPPER_CHEEKBONE("윗광대", FACE),
    EYEBROW("눈썹", null),
        BASIC("기본형", EYEBROW),
        ARCHED("아치형", EYEBROW),
        HIGHER_ARCHED("높은아치형", EYEBROW),
        STRAIGHT("일자/직선형", EYEBROW),
        WAVY("물결형", EYEBROW),
        ANGLED_ARCHED("각진 아치형", EYEBROW),
        ANGLED("각진형", EYEBROW),
        ROUND("둥근형", EYEBROW);

    private final String Detail;
    private final VirtualCategory type;
    private List<VirtualCategory> childrenList;

    VirtualCategory(String Detail, VirtualCategory type) {
        this.Detail = Detail;
        this.type = type;
    }

    public List<VirtualCategoryRequest> getVirtualCategoryRequestList(List<String> detailList) {
        List<String> noseDetailList = new ArrayList<>();
        List<String> eyeDetailList = new ArrayList<>();
        List<String> mouthDetailList = new ArrayList<>();
        List<String> faceDetailList = new ArrayList<>();
        List<String> doubleEyelidDetailList = new ArrayList<>();
        List<String> eyebrowDetailList = new ArrayList<>();

        for (String detailEle : detailList) {
            if (!StringUtils.hasText(detailEle)) {
                continue;
            }

            if (detailEle.startsWith("\"")) {
                detailEle = detailEle.substring(1);
            }

            if (detailEle.endsWith("\"")) {
                detailEle = detailEle.substring(0, detailEle.length() - 1);
            }
            VirtualCategory virtualCategory = getVirtualCategory(detailEle);

            if (virtualCategory == null) {
                continue;
            }

            switch (virtualCategory.getType()) {
                case NOSE:
                    noseDetailList.add(virtualCategory.getDetail());
                    break;
                case EYE:
                    eyeDetailList.add(virtualCategory.getDetail());
                    break;
                case MOUTH:
                    mouthDetailList.add(virtualCategory.getDetail());
                    break;
                case FACE:
                    faceDetailList.add(virtualCategory.getDetail());
                    break;
                case DOUBLE_EYELID:
                    doubleEyelidDetailList.add(virtualCategory.getDetail());
                    break;
                case EYEBROW:
                    eyebrowDetailList.add(virtualCategory.getDetail());
                    break;
                default:
                    break;
            }
        }

        List<VirtualCategoryRequest> virtualCategoryRequestList = new ArrayList<>();

        if (!noseDetailList.isEmpty()) {
            virtualCategoryRequestList.add(VirtualCategoryRequest.of(NOSE.getDetail(), noseDetailList));
        }

        if (!eyeDetailList.isEmpty()) {
            virtualCategoryRequestList.add(VirtualCategoryRequest.of(EYE.getDetail(), eyeDetailList));
        }

        if (!mouthDetailList.isEmpty()) {
            virtualCategoryRequestList.add(VirtualCategoryRequest.of(MOUTH.getDetail(), mouthDetailList));
        }

        if (!faceDetailList.isEmpty()) {
            virtualCategoryRequestList.add(VirtualCategoryRequest.of(FACE.getDetail(), faceDetailList));
        }

        if (!doubleEyelidDetailList.isEmpty()) {
            virtualCategoryRequestList.add(VirtualCategoryRequest.of(DOUBLE_EYELID.getDetail(), doubleEyelidDetailList));
        }

        if (!eyebrowDetailList.isEmpty()) {
            virtualCategoryRequestList.add(VirtualCategoryRequest.of(EYEBROW.getDetail(), eyebrowDetailList));
        }

        return virtualCategoryRequestList;
    }

    public VirtualCategory getVirtualCategory(String detail) {
        return Arrays.stream(VirtualCategory.values())
            .filter(virtualCategory -> virtualCategory.getDetail().equals(detail))
            .findFirst()
            .orElse(null);
    }
}
