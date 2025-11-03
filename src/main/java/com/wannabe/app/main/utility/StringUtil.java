package com.wannabe.app.main.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class StringUtil {

    public static boolean hasText(String value) {
        return StringUtils.hasText(value);
    }

}
