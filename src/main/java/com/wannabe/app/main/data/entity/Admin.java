package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.dto.common.YN;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Admin {

    private Long id;
    private String accountId;
    private String password;
    private Integer accountType;
    private String frozenYn;
    private String deletedYn;

    public boolean isActive() {
        return frozenYn.equals("N") && deletedYn.equals("N");
    }
}
