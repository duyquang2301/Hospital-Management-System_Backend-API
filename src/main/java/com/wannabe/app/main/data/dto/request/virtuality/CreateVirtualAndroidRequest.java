package com.wannabe.app.main.data.dto.request.virtuality;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateVirtualAndroidRequest {
    private String type;
    private List<String> categoryTypeList;
    private List<String> categoryDetailList;
    private MultipartFile beforeImage;
    private MultipartFile afterImage;
}
