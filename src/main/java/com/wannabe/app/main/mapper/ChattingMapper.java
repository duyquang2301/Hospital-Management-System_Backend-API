package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.Block;
import com.wannabe.app.main.data.entity.Chatting;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChattingMapper {

    Chatting findChattingByUserIds(@Param("userId") long userId, @Param("subjectUserId") long subjectUserId);

    Chatting findChattingByChatId(long chatId);

    Long increaseChattingSeq();

    void insertChatting(Chatting chatting);

    List<Chatting> findChattingListByUserId(long userId);

    List<Long> findBlockUserIdListByUserId(long userId);

    void insertBlock(Block block);

    void deleteBlock(Block block);
}
