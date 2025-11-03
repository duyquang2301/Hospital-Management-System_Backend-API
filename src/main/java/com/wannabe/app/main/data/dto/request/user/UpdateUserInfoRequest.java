package com.wannabe.app.main.data.dto.request.user;

import com.wannabe.app.main.data.dto.meta.LocationDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoRequest {

    private List<String> category;
    private LocationDTO location;
}
