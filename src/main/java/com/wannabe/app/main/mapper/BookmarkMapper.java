package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.Bookmark;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookmarkMapper {

    void insertBookmark(Bookmark bookmark);

    Optional<Bookmark> findBookmark(Bookmark bookmark);

    void deleteBookmark(Bookmark bookmark);

    List<Bookmark> findEventBookMarkByUserId(@Param("userId") Long userId, @Param("page") int page, @Param("size") int size);

    List<Bookmark> findHospitalBookmarkByUserId(@Param("userId") Long userId, @Param("page") int page, @Param("size") int size);

    List<Bookmark> findArticleBookmarkByUserId(@Param("userId") Long userId, @Param("page") int page, @Param("size") int size);


    long countCommunityBookMark(@Param("userId") Long userId);

    long countHospitalBookMark(@Param("userId") Long userId);

    long countEventBookMark(@Param("userId") Long userId);
}
