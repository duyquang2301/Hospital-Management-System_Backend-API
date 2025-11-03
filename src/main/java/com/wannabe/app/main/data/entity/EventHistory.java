package com.wannabe.app.main.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventHistory {

    private Long id;
    private Long eventId;
    private Long beforeEventId;

    public static EventHistory of(Long eventId, Long beforeEventId) {
        return new EventHistory(eventId, beforeEventId);
    }

    private EventHistory(Long eventId, Long beforeEventId) {
        this.eventId = eventId;
        this.beforeEventId = beforeEventId;
    }
}
