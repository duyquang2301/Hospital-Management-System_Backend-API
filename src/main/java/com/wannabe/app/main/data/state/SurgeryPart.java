package com.wannabe.app.main.data.state;

import com.wannabe.app.main.data.dto.response.meta.DepthResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum SurgeryPart {
    EYE("눈", null),
        DOUBLE_EYELID("쌍꺼풀", EYE),
        SLIT("트임", EYE),
        PTOSIS_CORRECTION("눈매교정", EYE),
        EYE_SHAPE_CORRECTION("눈모양교정", EYE),
        EYE_RE_OPERATION("눈재수술", EYE),
    NOSE("코", null),
        NOSE_RIDGE("콧대", NOSE),
        END_OF_NOSE("코끝", NOSE),
        ALAR_RESECTION("콧볼", NOSE),
        CONTRACTED_NOSE("기능코", NOSE),
        NOSE_RE_OPERATION("코재수술", NOSE),
    FACE_CONTOUR("안면윤곽/양악", null),
        CHEEKBONE("광대", FACE_CONTOUR),
        CONTOUR("윤곽", FACE_CONTOUR),
        BIMAXILLARY_OPERATION("양악", FACE_CONTOUR),
        FACE_RE_OPERATION("안면윤곽재수술", FACE_CONTOUR),
        FOREHEAD("이마", FACE_CONTOUR),
    MOUSE("입", null),
        LIPS("입술", MOUSE),
    ETC("기타", null),
        ETC_ELEMENT("기타", ETC);

    private final String surgeryPartName;
    private final SurgeryPart parent;

    SurgeryPart(String surgeryPart, SurgeryPart parent) {
        this.surgeryPartName = surgeryPart;
        this.parent = parent;
    }

    public List<String> getParentSurgeryPartList() {
        return Arrays.stream(SurgeryPart.values())
            .filter(surgeryPart -> surgeryPart.getParent() == null)
            .map(SurgeryPart::getSurgeryPartName)
            .toList();
    }

    public List<DepthResponse> getLocationDepthResponse() {
        List<String> parentList = getParentSurgeryPartList();

        List<DepthResponse> depthResponseList = new ArrayList<>();

        for (String parent : parentList) {
            depthResponseList.add(buildDepthResponse(parent));

        }

        return depthResponseList;
    }

    private List<String> getSurgeryPartChildrenList(String parent) {
        return Arrays.stream(SurgeryPart.values())
            .filter(surgeryPart -> isSameParent(surgeryPart, parent))
            .map(SurgeryPart::getSurgeryPartName)
            .toList();
    }

    private boolean isSameParent(SurgeryPart surgeryPart, String parent) {
        return surgeryPart.getParent() != null && surgeryPart.getParent().getSurgeryPartName().equals(parent);
    }

    private DepthResponse buildDepthResponse(String parent) {
        return new DepthResponse(parent, getSurgeryPartChildrenList(parent));
    }

}
