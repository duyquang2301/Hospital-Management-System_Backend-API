package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.file.FileDto;
import com.wannabe.app.main.data.entity.Files;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FilesMapper {

    List<Files> findFileListByGroupId(@Param("groupId") long groupId);

    Files findFileByGroupId(@Param("groupId") long groupId);

    Files findFileById(@Param("id") long id);

    long findLastFileOrderByGroupId(@Param("imageGroupId") long imageGroupId);

    Long findGroupIdSequence();

    void saveFiles(@Param("file") FileDto file);

    void updateFile(@Param("file") FileDto file);

    void updateFileOrder(@Param("fileId") long fileId, @Param("fileOrder") long fileOrder);

    void deleteFile(@Param("fileId") long fileId);
}
