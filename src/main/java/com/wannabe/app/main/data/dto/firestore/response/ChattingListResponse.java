package com.wannabe.app.main.data.dto.firestore.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChattingListResponse {

    private List<ChattingResponse> chattingList;

    public static ChattingListResponse from(List<ChattingResponse> chattingList) {
        return new ChattingListResponse(chattingList);
    }
}
