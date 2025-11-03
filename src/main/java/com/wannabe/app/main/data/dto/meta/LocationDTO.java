package com.wannabe.app.main.data.dto.meta;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

    private String category;
    private List<String> valueList;
}
