package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.EventHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventHistoryMapper {

    void insertEventHistory(EventHistory eventHistory);
}
