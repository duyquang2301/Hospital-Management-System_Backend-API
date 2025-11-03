package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.article.ArticleListDto;
import com.wannabe.app.main.data.dto.article.CommonArticle;
import com.wannabe.app.main.data.dto.article.GetArticleDetailDto;
import com.wannabe.app.main.data.dto.article.GetArticleListDto;
import com.wannabe.app.main.data.dto.article.GetReviewDetailDto;
import com.wannabe.app.main.data.dto.article.GetReviewListDto;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.community.ArticleImage;
import com.wannabe.app.main.data.dto.community.request.CreateArticleRequest;
import com.wannabe.app.main.data.dto.community.request.UpdateArticleRequest;
import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.entity.ArticleBlock;
import com.wannabe.app.main.data.entity.Bookmark;
import com.wannabe.app.main.data.entity.Files;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.data.entity.VirtualSurgery;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.BookmarkType;
import com.wannabe.app.main.data.state.ReviewType;
import com.wannabe.app.main.exception.argument.IllegalArgumentException;
import com.wannabe.app.main.exception.paramter.InvalidParameterException;
import com.wannabe.app.main.mapper.BookmarkMapper;
import com.wannabe.app.main.mapper.CommunityMapper;
import com.wannabe.app.main.mapper.EventMapper;
import com.wannabe.app.main.mapper.FilesMapper;
import com.wannabe.app.main.mapper.UserMapper;
import com.wannabe.app.main.mapper.VirtualMapper;
import com.wannabe.app.main.response.ListResponseDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.wannabe.app.main.utility.ConverToLong.convertUserId;

@Log4j2
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CloudFrontService cloudFrontService;
    private final ImageService imageService;
    private final CommunityMapper communityMapper;
    private final UserMapper userMapper;
    private final FilesMapper filesMapper;
    private final BookmarkMapper bookmarkMapper;
    private final VirtualMapper virtualMapper;
    private final EventMapper eventMapper;


    /**
     * 이벤트 상세에 속한 이벤트 후기
     *
     * @param eventId 이벤트 아이디
     * @return ListResponseDto<ReviewDTO>
     */
    public ListResponseDto<GetReviewListDto> getEventReviews(long eventId, Long userId) {
        List<GetReviewListDto> reviews = communityMapper.findReviewListByEventId(eventId).stream().map(review -> {
            User user = userMapper.findUserById(review.getWriterId());
            YN isAuthor = this.isAuthor(review.getWriterId(), convertUserId(userId));

            String profileImg = getProfileImg(user);

            String beforeImage = "";
            String afterImage = "";
            List<String> images = new ArrayList<>();
            // 기획 수정(커뮤니티 목록에서는 가격은 안보여줘도 될것 같다는 의견)으로 인해 주석 처리
//            Integer cost = 0;

            if (Objects.equals(ReviewType.VIRTUAL, review.getReviewType())) {
                VirtualSurgery virtualSurgery = Optional.ofNullable(virtualMapper.findVirtualSurgeryById(review.getReviewTypeId()))
                    .orElse(new VirtualSurgery());
                beforeImage = Optional.ofNullable(findFileAndMakeSignedUrl(virtualSurgery.getOriginalFileGroupId())).orElse("");
                afterImage = Optional.ofNullable(findFileAndMakeSignedUrl(virtualSurgery.getVirtualFileGroupId())).orElse("");
//                cost = Optional.ofNullable(eventMapper.findEventById(review.getReviewTypeId()).getPrice()).orElse(0);
            }
            if (Optional.ofNullable(review.getImageGroupId()).isPresent()) {
                images = filesMapper.findFileListByGroupId(review.getImageGroupId())
                    .stream()
                    .map(file -> cloudFrontService.generateSignedUrl(file.getPath()))
                    .toList();
            }

            return GetReviewListDto.of(review, profileImg, user.getNickname(), beforeImage, afterImage, images, isAuthor);
        }).toList();

        return ListResponseDto.of(reviews, communityMapper.countAllReviewByEventId(eventId));
    }

    /**
     * 전체 커뮤니티 글 조회
     *
     * @param page     현재 페이지
     * @param size     가져올 사이즈
     * @param category 선택한 카테고리
     * @return ListResponseDto<ArticleListDto> 전체 게시글 목록
     */
    @Transactional
    public ListResponseDto<ArticleListDto> getAllContent(int page, int size, String category, Long userId) {
        Long convertUserId = convertUserId(userId);
        List<ArticleListDto> allArticle = communityMapper.findAllArticle(page, size, category, convertUserId).stream()
            .map(articleList -> {
                User user = userMapper.findUserById(articleList.getWriterId());
                YN isAuthor = this.isAuthor(articleList.getWriterId(), convertUserId);

                String profileImg = getProfileImg(user);

                List<String> image = Optional.ofNullable(articleList.getImageGroupId())
                    .map(filesMapper::findFileListByGroupId)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(files -> cloudFrontService.generateSignedUrl(files.getPath()))
                    .toList();

                String beforeImage = "";
                String afterImage = "";
//                Integer cost = 0;

                if (Objects.equals(ReviewType.VIRTUAL, articleList.getReviewType())) {
                    VirtualSurgery virtualSurgery = Optional.ofNullable(virtualMapper.findVirtualSurgeryById(articleList.getReviewTypeId()))
                        .orElse(new VirtualSurgery());
                    beforeImage = findFileAndMakeSignedUrl(virtualSurgery.getOriginalFileGroupId());
                    afterImage = findFileAndMakeSignedUrl(virtualSurgery.getVirtualFileGroupId());
//                    cost = activeEventById.getPrice();
                }

                return ArticleListDto.of(articleList, profileImg, user.getNickname(), image, beforeImage, afterImage, isAuthor);
            }).toList();
        return ListResponseDto.of(allArticle, communityMapper.countAll(category));
    }

    /**
     * 사용자 프로필 이미지 조회 후 Generate S3 Url
     *
     * @param user 사용자 정보
     * @return String 사용자 프로필 이미지 S3 Url
     */
    @NotNull
    private String getProfileImg(User user) {
        return Optional.ofNullable(user)
            .map(userInfo -> Optional.ofNullable(userInfo.getImageGroupId())
                .map(filesMapper::findFileByGroupId)
                .map(file -> cloudFrontService.generateSignedUrl(file.getPath()))
                .orElse(""))
            .orElse("");
    }

    /**
     * 후기 글 조회
     *
     * @param page     현재 페이지
     * @param size     가져올 사이즈
     * @param category 선택한 카테고리
     * @return ListResponseDto<GetReviewListDto> 후기 글 목록
     */
    public ListResponseDto<GetReviewListDto> getReviews(int page, int size, String category, Long userId) {
        List<GetReviewListDto> reviews = communityMapper.findAllReviews(page, size, category, userId).stream()
            .map(review -> {
                User user = userMapper.findUserById(review.getWriterId());
                YN isAuthor = this.isAuthor(review.getWriterId(), convertUserId(userId));

                String profileImg = getProfileImg(user);

                String beforeImage = "";
                String afterImage = "";
                List<String> images = new ArrayList<>();
                // 기획 수정(커뮤니티 목록에서는 가격은 안보여줘도 될것 같다는 의견)으로 인해 주석 처리
//            Integer cost = 0;

                if (Objects.equals(ReviewType.VIRTUAL, review.getReviewType())) {
                    VirtualSurgery virtualSurgery = Optional.ofNullable(virtualMapper.findVirtualSurgeryById(review.getReviewTypeId()))
                        .orElse(new VirtualSurgery());
                    beforeImage = Optional.ofNullable(findFileAndMakeSignedUrl(virtualSurgery.getOriginalFileGroupId())).orElse("");
                    afterImage = Optional.ofNullable(findFileAndMakeSignedUrl(virtualSurgery.getVirtualFileGroupId())).orElse("");
//                cost = Optional.ofNullable(eventMapper.findEventById(review.getReviewTypeId()).getPrice()).orElse(0);
                }
                if (Optional.ofNullable(review.getImageGroupId()).isPresent()) {
                    images = filesMapper.findFileListByGroupId(review.getImageGroupId())
                        .stream()
                        .map(file -> cloudFrontService.generateSignedUrl(file.getPath()))
                        .toList();
                }

                return GetReviewListDto.of(review, profileImg, user.getNickname(), beforeImage, afterImage, images, isAuthor);
            }).toList();

        return ListResponseDto.of(reviews, communityMapper.countAllReview(category));
    }

    /**
     * 이미지 그룹 아이디로 file 조회 후 presignedurl 반환
     *
     * @param groupId 이미지 그룹 아이디
     * @return (String) PresignedUrl
     */
    private String findFileAndMakeSignedUrl(Long groupId) {
        if (groupId == null) {
            return null;
        }
        Files file = filesMapper.findFileByGroupId(groupId);
        return cloudFrontService.generateSignedUrl(file.getPath());
    }

    /**
     * 게시글 조회
     *
     * @param page     현재 페이지
     * @param size     가져올 사이즈
     * @param category 선택한 카테고리
     * @return ListResponseDto<GetArticleListDto> 게시글 목록
     */
    public ListResponseDto<GetArticleListDto> getArticles(int page, int size, String category, Long userId) {
        List<GetArticleListDto> articles = communityMapper.findAllNormalArticle(page, size, category, userId).stream()
            .map(article -> {
                User user = userMapper.findUserById(article.getWriterId());
                YN isAuthor = this.isAuthor(article.getWriterId(), convertUserId(userId));

                String profileImg = getProfileImg(user);

                List<String> image = Optional.ofNullable(article.getImageGroupId())
                    .map(filesMapper::findFileListByGroupId)
                    .map(filesList -> filesList.stream()
                        .map(files -> cloudFrontService.generateSignedUrl(files.getPath()))
                        .filter(url -> !url.isEmpty())
                        .toList())
                    .orElseGet(Collections::emptyList);

                return GetArticleListDto.of(article, profileImg, user.getNickname(), image, isAuthor);
            }).toList();

        return ListResponseDto.of(articles, communityMapper.countAllNormalArticle(category));
    }

    private YN isAuthor(long writerId, long userId) {
        return YN.of(writerId == userId);
    }

    private Long convertUserId(Long userId) {
        return (userId != null) ? userId : -1L;
    }

    /**
     * 일반 게시글 상세 조회
     *
     * @param userId    (북마크 조회를 위한)사용자 아이디
     * @param articleId 게시글 아이디
     * @return (GetArticleDetailDto) 일반 게시글 상세 내용
     */
    @Transactional
    public GetArticleDetailDto getArticle(Long userId, Long articleId) {
        CommonArticle article = getResult(userId, articleId);
        if (Objects.equals(ArticleType.REVIEW, article.article().getType())) {
            throw new InvalidParameterException(log, "게시글이 존재 하지 않습니다.");
        }
        //게시글에 속한 이미지 목록
        List<ArticleImage> image = this.getArticleImage(article);
        YN isAuthor = article.isAuthor();

        return GetArticleDetailDto.of(article.article(), article.isBookMark(), article.profileImg(), article.user().getNickname(), image, isAuthor);
    }

    /**
     * 후기 게시글 상세 조회
     *
     * @param userId    (북마크 조회를 위한)사용자 아이디
     * @param articleId 게시글 아이디
     * @return (GetReviewDetailDto) 리뷰 게시글 상세 내용
     */
    @Transactional
    public GetReviewDetailDto getReview(Long userId, Long articleId) {
        CommonArticle commonArticle = getResult(userId, articleId);
        Article article = commonArticle.article();
        if (Objects.equals(ArticleType.ARTICLE, article.getType())) {
            throw new InvalidParameterException(log, "게시글이 존재 하지 않습니다.");
        }

        List<ArticleImage> images = new ArrayList<>();
        if (!ReviewType.VIRTUAL.equals(article.getReviewType())) {
            images = this.getArticleImage(commonArticle);
        }

        GetEventDto event = null;
        if (ReviewType.EVENT.equals(article.getReviewType())) {
            event = Optional.ofNullable(eventMapper.findEventDtoById(article.getReviewTypeId())).orElse(new GetEventDto());
            event.setThumbNail(cloudFrontService.generateSignedUrl(event.getThumbNail()));
        }

        String beforeImage = "";
        String afterImage = "";
        if (ReviewType.VIRTUAL.equals(article.getReviewType())) {
            VirtualSurgery virtualSurgery = Optional.ofNullable(virtualMapper.findVirtualSurgeryById(article.getReviewTypeId()))
                .orElse(new VirtualSurgery());
            beforeImage = Optional.ofNullable(findFileAndMakeSignedUrl(virtualSurgery.getOriginalFileGroupId())).orElse("");
            afterImage = Optional.ofNullable(findFileAndMakeSignedUrl(virtualSurgery.getVirtualFileGroupId())).orElse("");
        }

        YN isAuthor = this.isAuthor(article.getWriterId(), convertUserId(userId));

        return GetReviewDetailDto.of(commonArticle, images, beforeImage, afterImage, event, isAuthor);
    }

    /**
     * 게시글 이미지 조회 후 Generate S3 Url
     *
     * @param article 게시글 정보
     * @return List<ArticleImage> 이미지 정보
     */
    @NotNull
    private List<ArticleImage> getArticleImage(CommonArticle article) {
        return Optional.ofNullable(article.article().getImageGroupId())
            .map(groupId -> filesMapper.findFileListByGroupId(groupId)
                .stream()
                .map(file -> ArticleImage.of(file.getId(), cloudFrontService.generateSignedUrl(file.getPath()), file.getFileOrder()))
                .toList())
            .orElseGet(ArrayList::new);
    }

    /**
     * 게시글 상세 공통 조회 내역
     *
     * @param userId    (북마크 조회를 위한)사용자 아이디
     * @param articleId 게시글 아이디
     * @return (CommonArticle) 게시글 상세 내용
     */
    @NotNull
    private CommonArticle getResult(Long userId, Long articleId) {
        Article article = communityMapper.findByArticleId(articleId)
            .orElseThrow(() -> new InvalidParameterException(log, "게시글이 존재 하지 않습니다."));
        YN isBookMark = YN.of(false);
        if (userId != null) {
            Optional<Bookmark> bookmark = bookmarkMapper.findBookmark(
                Bookmark.of(userId, articleId, BookmarkType.ARTICLE.getBookmarkType())
            );
            isBookMark = YN.of(bookmark.isPresent());
        }
        YN isAuthor = this.isAuthor(article.getWriterId(), convertUserId(userId));

        User user = userMapper.findUserById(article.getWriterId());
        String profileImg = getProfileImg(user);
        communityMapper.updateViewCount(articleId);

        return new CommonArticle(article, isBookMark, user, profileImg, isAuthor);
    }

    /**
     * 북마크 등록
     *
     * @param articleId (북마크 등록 할) 게시글 아이디
     * @param userId    사용자 아이디
     */
    public void createArticleBookMark(Long articleId, Long userId) {
        Bookmark bookmark = new Bookmark(userId, articleId, BookmarkType.ARTICLE.getBookmarkType());
        bookmarkMapper.findBookmark(bookmark)
            .ifPresentOrElse(
                existBookmark -> {
                    throw new InvalidParameterException(log, "이미 존재하는 북마크입니다.");
                },
                () -> bookmarkMapper.insertBookmark(bookmark)
            );
    }

    /**
     * 북마크 해제
     *
     * @param articleId (북마크 해제 할) 게시글 아이디
     * @param userId    사용자 아이디
     */
    public void deleteArticleBookMark(Long articleId, Long userId) {
        Bookmark bookmark = new Bookmark(userId, articleId, BookmarkType.ARTICLE.getBookmarkType());

        bookmarkMapper.findBookmark(bookmark)
            .ifPresentOrElse(
                (existBookmark) -> bookmarkMapper.deleteBookmark(bookmark),
                () -> {
                    throw new InvalidParameterException(log, "이미 삭제된 북마크입니다.");
                });
    }

    /**
     * 게시글 등록
     *
     * @param userId               사용자 아이디
     * @param createArticleRequest 게시글 등록 내용
     * @param images               게시글에 등록 할 이미지
     */
    @Transactional
    public void createArticle(Long userId, CreateArticleRequest createArticleRequest, List<MultipartFile> images) {
        Article article = new Article(userId, createArticleRequest);
        communityMapper.saveArticle(article);
        Long articleId = article.getId();

        if (createArticleRequest.getArticleType().equals(ArticleType.REVIEW) && createArticleRequest.getReviewType().equals(ReviewType.EVENT)
            && ObjectUtils.isEmpty(createArticleRequest.getReviewId())) {
            throw new IllegalArgumentException(log, "이벤트를 선택해 주세요.");
        }

        if (ObjectUtils.isNotEmpty(images)) {
            if (createArticleRequest.getArticleType().equals(ArticleType.REVIEW) && createArticleRequest.getReviewType().equals(ReviewType.VIRTUAL)) {
                throw new IllegalArgumentException(log, "이미지를 저장 할 수 없습니다.");
            }
            if (images.size() > 3) {
                throw new IllegalArgumentException(log, "최대 저장 가능한 이미지 개수는 3개입니다.");
            }
        }

        if (ReviewType.VIRTUAL.equals(createArticleRequest.getReviewType())) {
            return;
        }

        saveImages(articleId, userId, images);

//        if (createArticleRequest.getArticleType().equals(ArticleType.REVIEW) && createArticleRequest.getReviewType().equals(ReviewType.VIRTUAL)) {
//            saveVirtualImage(createArticleRequest.getReviewId(), articleId);
//        } else {
//            saveImages(articleId, userId, images);
//        }
    }

    /**
     * 게시글 수정
     *
     * @param userId               사용자 아이디
     * @param updateArticleRequest 수정할 계시글 내용
     * @param images               새로 추가한 이미지
     * @param existImage           기존 이미지
     * @param articleId            게시글 아이디
     */
    @Transactional
    public void updateArticle(
        long userId,
        UpdateArticleRequest updateArticleRequest,
        List<MultipartFile> images,
        List<ArticleImage> existImage,
        long articleId
    ) {
        Article article = communityMapper.findByArticleId(articleId).orElseThrow(() -> new InvalidParameterException(log, "게시글이 존재 하지 않습니다."));

        if (!Objects.equals(userId, article.getWriterId())) {
            throw new InvalidParameterException(log, "수정 권한이 없습니다.");
        }

        if (Optional.ofNullable(article.getReviewType()).isPresent()
            && Objects.equals(ReviewType.VIRTUAL, article.getReviewType())
            && !images.isEmpty()
            && !existImage.isEmpty()
        ) {
            throw new InvalidParameterException(log, "사진을 추가 할 수 없습니다.");
        }

        if (Optional.ofNullable(updateArticleRequest.getContent()).isPresent()) {
            article.setContent(updateArticleRequest.getContent());
        }

        communityMapper.updateArticle(article);

        Long imageGroupId = article.getImageGroupId();

        if (ObjectUtils.isNotEmpty(updateArticleRequest.getDeletedImages())) {
            updateArticleRequest.getDeletedImages().forEach(image -> imageService.deleteImage(image.getId()));
        }

        if (ObjectUtils.isNotEmpty(existImage)) {
            existImage.forEach(image -> {
                Optional.ofNullable(filesMapper.findFileById(image.getId())).orElseThrow(() -> new InvalidParameterException(log, "삭제된 이미지 입니다."));
                imageService.updateImage(image.getId(), image.getFileOrder());
            });
        }

        if (ObjectUtils.isNotEmpty(images)) {
            List<Files> articleFileList = filesMapper.findFileListByGroupId(imageGroupId);
            int totalSize = images.size() + articleFileList.size();

            if (images.size() > 3 || totalSize > 3) {
                throw new IllegalArgumentException(log, "최대 저장 가능한 이미지 개수는 3개입니다.");
            }

            if (ObjectUtils.isEmpty(articleFileList)) {
                this.saveImages(articleId, userId, images);
            } else {
                for (MultipartFile image : images) {
                    long lastFileOrder = filesMapper.findLastFileOrderByGroupId(imageGroupId);
                    ++lastFileOrder;
                    imageService.uploadArticleImage(image, articleId, userId, lastFileOrder, imageGroupId);
                }
            }
        }
    }

    /**
     * 일반 이미지 저장
     *
     * @param images    이미지 목록
     * @param articleId 게시글 아이디
     * @param userId    사용자 아이디
     */
    private void saveImages(Long articleId, Long userId, List<MultipartFile> images) {

        if (ObjectUtils.isEmpty(images)) {
            return;
        }

        Long groupId = filesMapper.findGroupIdSequence();
        communityMapper.updateArticleGroupId(groupId, articleId);

        long fileOrder = 1L;
        for (MultipartFile image : images) {
            imageService.uploadArticleImage(image, articleId, userId, fileOrder, groupId);
            fileOrder += 1L;
        }
    }

    public void deleteArticle(Long articleId) {

        int result = communityMapper.deleteArticleById(articleId);
        System.err.println("###");
        System.err.println(result);
        System.err.println("###");

    }

    public void createArticleBlock(Long userId, Long articleId) {
        ArticleBlock articleBlock = new ArticleBlock(articleId, userId);
        communityMapper.saveArticleBlock(articleBlock);
    }

}
