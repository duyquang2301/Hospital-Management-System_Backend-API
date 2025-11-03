package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.auth.SocialLoginFilter;
import com.wannabe.app.main.data.dto.user.UserChatProfileDTO;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.data.state.ArticleType;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {

    User findUserByLogin(
        @Param("loginType") String loginType,
        @Param("loginKey") String loginKey
    );

    User findUserById(long userId);

    int insertUser(User user);


    User findUserByDeviceToken(User user);

    int updateDeviceToken(User user);

    int deleteDeviceToken(String deviceToken);

    int deleteUserDeviceToken(long userId);

    int updateAdditionInfo(User user);

    Optional<User> findUserBySocialLoginFilter(SocialLoginFilter socialLoginFilter);

    Optional<User> findUserByNickname(String nickname);

    void withdrawal(User user);

    Long findWithdrawalSeq();

    void updateUserPersonalInfo(User user);

    void updateUserImageGroupId(User user);

    Optional<UserChatProfileDTO> findUserChatProfile(long userId);

    String findDeviceToken(long userId);

    List<Article> findAllArticles(
        @Param("page") int page,
        @Param("size") int size,
        @Param("userId") long userId
    );

    List<Article> findAllNormalArticle(
        @Param("page") int page,
        @Param("size") int size,
        @Param("userId") long userId
    );

    List<Article> findAllReviews(
        @Param("page") int page,
        @Param("size") int size,
        @Param("userId") Long userId
    );

    Integer findPointByUserId(@Param("userId") Long userId);

    long countAll(@Param("userId") Long userId, @Param("type") ArticleType type);

}
