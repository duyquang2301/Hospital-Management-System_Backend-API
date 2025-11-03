package com.wannabe.app.main.data.state;

import com.wannabe.app.main.data.dto.response.meta.DepthResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum Location {
    SEOUL("서울", null),
        SEOUL_WHOLE("서울 전체", SEOUL),
        GANGNAM("강남역/신논현/양재", SEOUL),
        SEOLLEUNG("선릉/역삼/삼성", SEOUL),
        DOGGOK("도곡/대치/한티", SEOUL),
        SINSA("신사/논현/반포", SEOUL),
        CHEONGDAM("청담/압구정", SEOUL),
        SUSEO("수서/개포/일원", SEOUL),
        GYODAE("교대/방배", SEOUL),
        DONGJAK("동작/사당", SEOUL),
        JAMSIL("잠실/방이/석촌", SEOUL),
        MOONJEONG("문정/장지", SEOUL),
        CHEONHO("천호/강동", SEOUL),
        SEOUL_DAE("서울대/봉천/신림", SEOUL),
        YEOUUIDO("여의도/영등포", SEOUL),
        SINDORIM("신도림/구로", SEOUL),
        GASAN("가산/금천", SEOUL),
        MOCKDONG("목동/등촌", SEOUL),
        GANGSEO("강서/마곡", SEOUL),
        HONG_DAE("홍대/공덕", SEOUL),
        SINCHON("신촌/서대문", SEOUL),
        SANGAM("상암/응암/은평", SEOUL),
        JONGNO("종로/을지로/명동", SEOUL),
        YONGSAN("용산/이태원/한남", SEOUL),
        DONGDAEMUN("동대문/대학로/성신여대", SEOUL),
        OCKSU("옥수/금호/약수", SEOUL),
        WANGSHIB("왕십리/성수/건대", SEOUL),
        CHEONGNYANGNI("청량리/답십리", SEOUL),
        SOOYU("수유/미아/창동", SEOUL),
        NOWON("노원/상계/중계/하계", SEOUL),
        JOONGRANG("중랑/상봉/사가정", SEOUL),
    GYEONGGI("경기", null),
        GYEONGGI_WHOLE("경기 전체", GYEONGGI),
        PANGYO("판교/분당", GYEONGGI),
        BOKJEONG("복정/태평/수정", GYEONGGI),
        MORAN("모란/중원", GYEONGGI),
        GYEONGGI_GWANGJU("광주/이천/여주", GYEONGGI),
        AHNYANG("안양/과천", GYEONGGI),
        GUNPO("군포/금정/의왕", GYEONGGI),
        HWASEONG("화성/동탄", GYEONGGI),
        OSAN("오산/안성/평택", GYEONGGI),
        YONGIN("용인/수지", GYEONGGI),
        SUWON("수원/광교", GYEONGGI),
        BUCHEON("부천/상동", GYEONGGI),
        GWANGMYEONG("광명/시흥", GYEONGGI),
        ANSAN("안산", GYEONGGI),
        GIMPO("김포", GYEONGGI),
        ILSAN("일산/고양", GYEONGGI),
        PAJU("파주/운정", GYEONGGI),
        NAMYANGJU("남양주/구리", GYEONGGI),
        HANAM("하남/미사", GYEONGGI),
        UIJEONGBU("의정부/양주", GYEONGGI),
        DONGDUCHEON("동두천/포천/연천", GYEONGGI),
        GAPYEONG("가평/양평", GYEONGGI),
    INCHEON("인천", null),
        INCHEON_WHOLE("인천 전체", INCHEON),
        INCHEON_SEO_GU("서구/청라", INCHEON),
        GYEYANG("계양/부평", INCHEON),
        NAMDONG_GU("남동구/구월/논현", INCHEON),
        SONGDO("송도/연수", INCHEON),
        INCHEON_DONG_GU("동구/미추홀", INCHEON),
        JOONG_GU("중구/강화/웅진", INCHEON),
    BUSAN("부산", null),
        BUSAN_WHOLE("부산 전체", BUSAN),
        HAEUNDAE("해운대/센텀", BUSAN),
        SOOYOUNG_GU("수영구/광안리", BUSAN),
        BUSAN_NAM_GU("남구/부경대", BUSAN),
        YEONSAN("연산/동래/부산대", BUSAN),
        BUSAN_JUNG_GU("중구/남포동/중앙동", BUSAN),
        BUSAN_DONG_GU("동구/부산역", BUSAN),
        BUSAN_SEO_GU("서구/영도", BUSAN),
        SAHA_GU("사하구/괴정/하단", BUSAN),
        BUSAN_GANG_SEO_GU("강서구/명지", BUSAN),
        BUSAN_BUK_GU("북구/사상", BUSAN),
        GEUMJEONG_GU("금정구/연제구", BUSAN),
        SONGJEONG("송정/기장", BUSAN),
    DAEGU("대구", null),
        DAEGU_WHOLE("대구 전체", DAEGU),
        DAEGU_JOONG_GU("중구/동성로/서문시장", DAEGU),
        DAEGU_DONG_GU("동구/동대구역", DAEGU),
        DAEGU_BUK_GU("북구/칠곡", DAEGU),
        SUSUNG_GU("수성구/범어", DAEGU),
        DALSEO_GU("달서구/죽전/계명대", DAEGU),
        DALSEONG("달성군", DAEGU),
        SEO_GU("서구/평리", DAEGU),
        DAEGU_NAM_GU("남구", DAEGU),
    DAEJEON("대전", null),
        DAEJEON_WHOLE("대전 전체", DAEJEON),
        YOU_SEONG_GU("유성구", DAEJEON),
        DAEJEON_DONG_GU("동구", DAEJEON),
        DAEDUCK_GU("대덕구", DAEJEON),
        DAEJEON_SEO_GU("서구/둔산동", DAEJEON),
        DAEJEON_JOONG_GU("중구", DAEJEON),
    GWANGJU("광주", null),
        GWANGJU_WHOLE("광주 전체", GWANGJU),
        GWANGJU_SEO_GU("서구/상무지구", GWANGJU),
        GWANGJU_BUK_GU("북구/광주역", GWANGJU),
        GWANGJU_NAM_GU("남구", GWANGJU),
        GWANGJU_DONG_GU("동구/남광주역/충장로", GWANGJU),
        GWANGSAN_GU("광산구/수완동", GWANGJU),
    ULSAN("울산", null),
        ULSAN_WHOLE("울산 전체", ULSAN),
        ULSAN_DONG_GU("동구/북구", ULSAN),
        ULSAN_JOONG_GU("중구/남구", ULSAN),
        ULJU_GU("울주구", ULSAN),
    CHOONGNAM("충남", null),
        CHOONGNAM_WHOLE("충남 전체", CHOONGNAM),
        CHEONAN("천안/아산", CHOONGNAM),
        GYONGJU("공주", CHOONGNAM),
        SEJONG("세종", CHOONGNAM),
        TAEAN("태안/서산", CHOONGNAM),
        CHOONGNAM_ETC("기타", CHOONGNAM),
    CHOONGBOOK("충북", null),
        CHOONGBOOK_WHOLE("충북 전체", CHOONGBOOK),
        CHEONGJU("청주", CHOONGBOOK),
        CHOONGJU("충주", CHOONGBOOK),
        JECHEON("제천/단양", CHOONGBOOK),
        JINCHEON("진천/음성", CHOONGBOOK),
        JEUNG_PYEONG("증평/괴산", CHOONGBOOK),
        YOUNG_DONG("영동/옥천", CHOONGBOOK),
        CHOONGBOOK_ETC("기타", CHOONGBOOK),
    JEONNAM("전남", null),
        JEONNAM_WHOLE("전남 전체", JEONNAM),
        YEOSU("여수/순천/광양", JEONNAM),
        MOCKPO("목포/영암/무안", JEONNAM),
        NAJU("나주/담양/함평", JEONNAM),
        HAENAM("해남/완도/진도", JEONNAM),
        JEONNAM_ETC("기타", JEONNAM),
    JEONBOOK("전북", null),
        JEONBOOK_WHOLE("전북 전체", JEONBOOK),
        JEONJU("전주/완주", JEONBOOK),
        GUNSAN("군산/익산", JEONBOOK),
        GIMJE("김제/부안", JEONBOOK),
        GOCHANG("고창/정읍", JEONBOOK),
        NAMWON("남원/임실/순창", JEONBOOK),
        MOOJU("무주", JEONBOOK),
        JEONBOOK_ETC("기타", JEONBOOK),
    GYEONGNAM("경남", null),
        GYEONGNAM_WHOLE("경남 전체", GYEONGNAM),
        GIMHAE("김해", GYEONGNAM),
        CHANGWON("창원/마산/진해", GYEONGNAM),
        YANGSAN("양산/밀양", GYEONGNAM),
        GEOJE("거제/통영/고성", GYEONGNAM),
        JINJU("진주/사천", GYEONGNAM),
        NAMHAE("남해/하동", GYEONGNAM),
        GYEONGNAM_ETC("기타", GYEONGNAM),
    GYEONGBOOK("경북", null),
        GYEONGBOOK_WHOLE("경북 전체", GYEONGBOOK),
        POHANG("포항", GYEONGBOOK),
        GYEONGJU("경주/구미", GYEONGBOOK),
        GYEONGSAN("경산/영천/청도", GYEONGBOOK),
        ANDONG("안동/의성", GYEONGBOOK),
        GIMCHEON("김천/칠곡", GYEONGBOOK),
        MOONKYUNG("문경/상주/영주/예천", GYEONGBOOK),
        WOOLJIN("울진/영덕/청송", GYEONGBOOK),
        GYEONGBOOK_ETC("기타", GYEONGBOOK),
    GANGWON("강원", null),
        GANGWON_WHOLE("강원 전체", GANGWON),
        GANGNEUNG("강릉", GANGWON),
        SOKCHO("속초/양양/고성", GANGWON),
        DONGHAE("동해/삼척", GANGWON),
        CHOONCHUN("춘천/홍천/인제", GANGWON),
        HWACHEON("화천/철원", GANGWON),
        WONJU("원주/횡성/평창", GANGWON),
        GANGWON_ETC("기타", GANGWON),
    JEJU("제주", null),
        JEJU_WHOLE("제주 전체", JEJU),
        JEJU_SI("제주시", JEJU),
        SEOGWIPO("서귀포시", JEJU),
        JEJU_ETC("기타", JEJU),
    OTHER("기타", null);

    private final String locationCategory;
    private final Location parent;
    private List<Location> childrenList;

    Location(String locationCategory, Location parent) {
        this.locationCategory = locationCategory;
        this.parent = parent;
    }

    public List<DepthResponse> getLocationDepthResponse() {
        List<String> parentList = getLocationParentList();

        List<DepthResponse> depthResponseList = new ArrayList<>();

        for (String parent : parentList) {
            depthResponseList.add(buildDepthResponse(parent));

        }

        return depthResponseList;
    }

    public List<String> getLocationParentList() {
        return Arrays.stream(Location.values())
            .filter(location -> location.getParent() == null)
            .map(Location::getLocationCategory)
            .toList();
    }

    public List<String> getLocationList() {
        return Arrays.stream(Location.values())
            .map(Location::getLocationCategory)
            .toList();
    }

    public List<String> getLocationList(String parent) {
        return Arrays.stream(Location.values())
            .filter(location -> isSameParent(location, parent))
            .map(Location::getLocationCategory)
            .toList();
    }

    public List<DepthResponse> buildUserLocation(List<String> locationList) {
        List<DepthResponse> depthResponseList = new ArrayList<>();

        if (locationList == null || locationList.isEmpty()) {
            return depthResponseList;
        }

        return addChildrenListByUser(locationList);
    }

    public List<Location> getLocationList(List<String> childrenList) {
        return Arrays.stream(Location.values())
            .filter(location -> childrenList.contains(location.getLocationCategory()))
            .toList();

    }

    private List<DepthResponse> addChildrenListByUser(List<String> userLocation) {
        List<DepthResponse> depthResponses = buildDepthResponseList();
        List<Location> locationList = getLocationList(userLocation);

        for (Location locationEle : locationList) {
            for (DepthResponse depthResponse : depthResponses) {
                if (depthResponse.getCategory().equals(locationEle.getParent().getLocationCategory())) {
                    depthResponse.getValueList().add(locationEle.getLocationCategory());
                }
            }
        }

        return depthResponses;
    }

    private List<DepthResponse> buildDepthResponseList() {
        List<DepthResponse> depthResponseList = new ArrayList<>();
        List<String> parentList = getLocationParentList();

        for (String parent : parentList) {
            depthResponseList.add(new DepthResponse(parent));
        }

        return depthResponseList;
    }

    private List<String> getLocationChildrenList(String parent) {
        return Arrays.stream(Location.values())
            .filter(location -> isSameParent(location, parent))
            .map(Location::getLocationCategory)
            .toList();
    }

    private boolean isSameParent(Location location, String parent) {
        return location.getParent() != null && location.getParent().getLocationCategory().equals(parent);
    }

    private DepthResponse buildDepthResponse(String parent) {
        return new DepthResponse(parent, getLocationChildrenList(parent));
    }
}
