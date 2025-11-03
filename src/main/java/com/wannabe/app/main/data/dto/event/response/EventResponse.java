package com.wannabe.app.main.data.dto.event.response;

import com.wannabe.app.main.data.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventResponse {

    private long id;
    private String name;
    private String category;

    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getCategory());
    }

}
