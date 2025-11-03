package com.wannabe.app.main.service;

import static com.wannabe.app.main.utility.constant.FileUpload.ARTICLE_FILE_PATH;
import static com.wannabe.app.main.utility.constant.FileUpload.COUNSEL_IMAGE_FILE_PATH;
import static com.wannabe.app.main.utility.constant.FileUpload.USER_PROFILE_FILE_PATH;
import static com.wannabe.app.main.utility.constant.FileUpload.VIRTUAL_FILE_PATH;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.wannabe.app.main.data.dto.file.FileDto;
import com.wannabe.app.main.data.entity.Files;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.exception.argument.IllegalArgumentException;
import com.wannabe.app.main.exception.paramter.InvalidFileFormatException;
import com.wannabe.app.main.mapper.FilesMapper;
import com.wannabe.app.main.mapper.UserMapper;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;
    private final FilesMapper filesMapper;
    private final UserMapper userMapper;

    private final List<String> fileExts = Arrays.asList("jpg", "png", "jpeg", "JPG", "PNG", "JPEG");

    /**
     * 사용자 프로필 이미지 S3 업로드
     *
     * @param user 사용자 정보
     * @param file 사용자 프로필 이미지
     */
    public void uploadUserProfileImage(User user, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        log.info("!!!!!!! uploadUserProfileImage, userId : {}", user.getId());
        log.info("!!!!!!! uploadUserProfileImage, originalFilename : {}", originalFilename);
        String createFileName = createFileName(user.getId(), getFileNameExtension(originalFilename));

        if (!isValidExtension(getFileNameExtension(originalFilename))) {
            throw new InvalidFileFormatException(log);
        }

        String filePath = createUserProfileFilePath(user.getId()) + createFileName;

        uploadS3Bucket(filePath, file);
        FileDto newFile = FileDto.of(user, file, createFileName, filePath);

        if (user.getImageGroupId() != null) {
            log.info("!!!!!!! change profile image, userId : {}", user.getId());
            Files findImage = filesMapper.findFileByGroupId(user.getImageGroupId());

            if (findImage == null) {
                insertNewProfileImage(user, newFile);
                return;
            }

            amazonS3.deleteObject(bucketName, findImage.getPath());
            filesMapper.updateFile(newFile);
            return;
        }

        insertNewProfileImage(user, newFile);
    }

    /**
     * 파일 확장자 검증
     *
     * @param extension 확장자
     * @return 팔파일 확장자 검증 여부
     */
    private boolean isValidExtension(String extension) {
        return fileExts.contains(extension);
    }

    /**
     * 상담 신청 이미지 S3 업로드
     *
     * @param files     상담 신청 이미지 목록
     * @param counselId 상담 아이디
     * @param userId    사용자 아이디
     * @return long 이미지 그룹 아이디
     */
    public long uploadCounselImage(List<MultipartFile> files, long counselId, long userId) {
        if (files == null || files.isEmpty()) {
            return 0;
        }

        long groupId = filesMapper.findGroupIdSequence();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String createFileName = createFileName(counselId, getFileNameExtension(originalFilename));
            String filePath = createCounselFilePath(counselId) + createFileName;

            uploadS3Bucket(filePath, file);
            long initOrder = 1;
            FileDto newFile = FileDto.of(groupId, userId, file, createFileName, filePath, files.indexOf(file) + initOrder);
            filesMapper.saveFiles(newFile);
        }

        return groupId;
    }

    /**
     * 가상 성형 전 이미지 S3 업로드
     *
     * @param userId      사용자 아이디
     * @param virtualId   가상 성형 아이디
     * @param beforeImage 가상 성형 전 이미지
     * @return long 이미지 그룹 아이디
     */
    public long uploadBeforeVirtualSurgeryImage(long userId, long virtualId, MultipartFile beforeImage) {
        if (beforeImage == null) {
            return 0;
        }

        long groupId = filesMapper.findGroupIdSequence();

        String originalFilename = beforeImage.getOriginalFilename();
        String createFileName = createBeforeVirtualSurgeryName(virtualId, getFileNameExtension(originalFilename));
        String filePath = createVirtualSurgeryFilePath(virtualId) + createFileName;

        uploadS3Bucket(filePath, beforeImage);
        FileDto newFile = FileDto.of(groupId, userId, beforeImage, createFileName, filePath, 1L);
        filesMapper.saveFiles(newFile);

        return groupId;
    }

    /**
     * 이미지 삭제
     *
     * @param imageId 이미지 아이디
     */
    public void deleteImage(Long imageId) {
        Files findImage = filesMapper.findFileById(imageId);
        filesMapper.deleteFile(imageId);
        amazonS3.deleteObject(bucketName, findImage.getPath());
    }

    /**
     * 이미지 삭제
     *
     * @param groupId 이미지 그룹 아이디
     */
    public void deleteImageByGroupId(Long groupId) {
        List<Files> findImageList = filesMapper.findFileListByGroupId(groupId);

        if (findImageList == null || findImageList.isEmpty()) {
            return;
        }

        for (Files findImage : findImageList) {
            filesMapper.deleteFile(findImage.getId());
            amazonS3.deleteObject(bucketName, findImage.getPath());
        }
    }

    /**
     * 이미지 S3 업로드
     *
     * @param imageId   이미지 아이디
     * @param fileOrder 파일 순서
     */
    public void updateImage(Long imageId, long fileOrder) {
        filesMapper.updateFileOrder(imageId, fileOrder);
    }

    /**
     * 가상 성형 후 이미지 S3 업로드
     *
     * @param userId     사용자 아이디
     * @param virtualId  가상 성형 아이디
     * @param afterImage 가상 성형 후 이미지
     * @return long 이미지 그룹 아이디
     */
    public long uploadAfterVirtualSurgeryImage(long userId, long virtualId, MultipartFile afterImage) {

        if (afterImage == null) {
            return 0;
        }

        long groupId = filesMapper.findGroupIdSequence();

        String originalFilename = afterImage.getOriginalFilename();
        String createFileName = createAfterVirtualSurgeryName(virtualId, getFileNameExtension(originalFilename));
        String filePath = createVirtualSurgeryFilePath(virtualId) + createFileName;

        uploadS3Bucket(filePath, afterImage);
        FileDto newFile = FileDto.of(groupId, userId, afterImage, createFileName, filePath, 1L);
        filesMapper.saveFiles(newFile);

        return groupId;
    }

    /**
     * 가상 성형 좌 이미지 S3 업로드
     *
     * @param userId    사용자 아이디
     * @param virtualId 가상 성형 아이디
     * @param leftImage 가상 성형 좌 이미지
     * @return long 이미지 그룹 아이디
     */
    public long uploadVirtualSurgeryLeftImage(long userId, long virtualId, MultipartFile leftImage) {
        if (leftImage == null) {
            return 0;
        }

        long groupId = filesMapper.findGroupIdSequence();

        String originalFilename = leftImage.getOriginalFilename();
        String createFileName = createVirtualSurgeryLeftName(virtualId, getFileNameExtension(originalFilename));
        String filePath = createVirtualSurgeryFilePath(virtualId) + createFileName;

        uploadS3Bucket(filePath, leftImage);
        FileDto newFile = FileDto.of(groupId, userId, leftImage, createFileName, filePath, 1L);
        filesMapper.saveFiles(newFile);

        return groupId;
    }

    /**
     * 가상 성형 우 이미지 S3 업로드
     *
     * @param userId     사용자 아이디
     * @param virtualId  가상 성형 아이디
     * @param rightImage 가상 성형 우 이미지
     * @return long 이미지 그룹 아이디
     */
    public long uploadVirtualSurgeryRightImage(long userId, long virtualId, MultipartFile rightImage) {
        if (rightImage == null) {
            return 0;
        }

        long groupId = filesMapper.findGroupIdSequence();

        String originalFilename = rightImage.getOriginalFilename();
        String createFileName = createVirtualSurgeryRightName(virtualId, getFileNameExtension(originalFilename));
        String filePath = createVirtualSurgeryFilePath(virtualId) + createFileName;

        uploadS3Bucket(filePath, rightImage);
        FileDto newFile = FileDto.of(groupId, userId, rightImage, createFileName, filePath, 1L);
        filesMapper.saveFiles(newFile);

        return groupId;
    }

    /**
     * 게시글 이미지 S3 업로드
     *
     * @param image     이미지 파일
     * @param articleId 게시글 아이디
     * @param userId    사용자 아이디
     * @param fileOrder 파일 순서
     * @param groupId   이미지 그룹 아이디
     */
    public void uploadArticleImage(MultipartFile image, Long articleId, Long userId, long fileOrder, long groupId) {
        String originalFilename = image.getOriginalFilename();

        String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
        String filename = createFileName(userId, extension);

        if (!fileExts.contains(extension)) {
            throw new IllegalArgumentException(log, "유효하지 않은 파일 형식 입니다.");
        }
        String path = String.format(ARTICLE_FILE_PATH, articleId) + filename;

        uploadS3Bucket(path, image);

        FileDto file = FileDto.of(groupId, userId, image, filename, path, fileOrder);
        filesMapper.saveFiles(file);
    }

    /**
     * 새로운 프로필 이미지 생성
     *
     * @param user    사용자 정보
     * @param newFile 새로운 프로필 이미지
     */
    private void insertNewProfileImage(User user, FileDto newFile) {
        long groupId = filesMapper.findGroupIdSequence();
        newFile.updateGroupId(groupId);
        filesMapper.saveFiles(newFile);
        user.updateImageGroupId(groupId);
        userMapper.updateUserImageGroupId(user);
    }

    /**
     * 파일 이름 생성
     *
     * @param userId            사용자 아이디
     * @param filenameExtension 파일 확장자
     * @return String 파일 이름
     */
    private String createFileName(long userId, String filenameExtension) {
        return userId + "-" + createDateString() + "-" + createRandomId() + "." + filenameExtension;
    }

    /**
     * 가상 성형 전 사진 파일 이름 생성
     *
     * @param virtualId         가상 성형 아이디
     * @param filenameExtension 파일 확장저
     * @return String 가상 성형 전 사진 파일 이름
     */
    private String createBeforeVirtualSurgeryName(long virtualId, String filenameExtension) {
        return virtualId + "-before" + "-" + createDateString() + "-" + createRandomId() + "." + filenameExtension;
    }

    /**
     * 가상 성형 후 사진 파일 이름 생성
     *
     * @param virtualId         가상 성형 아이디
     * @param filenameExtension 파일 확장저
     * @return String 가상 성형 후 사진 파일 이름
     */
    private String createAfterVirtualSurgeryName(long virtualId, String filenameExtension) {
        return virtualId + "-after" + "-" + createDateString() + "-" + createRandomId() + "." + filenameExtension;
    }

    /**
     * 가상 성형 좌 사진 파일 이름 생성
     *
     * @param virtualId         가상 성형 아이디
     * @param filenameExtension 파일 확장저
     * @return String 가상 성형 좌 사진 파일 이름
     */
    private String createVirtualSurgeryLeftName(long virtualId, String filenameExtension) {
        return virtualId + "-left" + "-" + createDateString() + "-" + createRandomId() + "." + filenameExtension;
    }

    /**
     * 가상 성형 우 사진 파일 이름 생성
     *
     * @param virtualId         가상 성형 아이디
     * @param filenameExtension 파일 확장저
     * @return String 가상 성형 우 사진 파일 이름
     */
    private String createVirtualSurgeryRightName(long virtualId, String filenameExtension) {
        return virtualId + "-right" + "-" + createDateString() + "-" + createRandomId() + "." + filenameExtension;
    }

    /**
     * Date -> String
     *
     * @return String 날짜
     */
    private String createDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.now().format(formatter);
    }

    /**
     * Random UUID 생성
     *
     * @return String Random UUID
     */
    private String createRandomId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 파일 확장자 조회
     *
     * @param fileName 파일 이름
     * @return String 파일 확장자
     */
    private String getFileNameExtension(String fileName) {
        return StringUtils.getFilenameExtension(fileName);
    }

    /**
     * 사용자 프로필 이미지 파일 경로 생성
     *
     * @param userId 사용자 아이디
     * @return String 사용자 프로필 이미지 파일 경로
     */
    private String createUserProfileFilePath(long userId) {
        return String.format(USER_PROFILE_FILE_PATH, userId);
    }

    /**
     * 상담 신청 이미지 파일 경로 생성
     *
     * @param counselId 상담 아이디
     * @return String 상담 신청 이미지 파일
     */
    private String createCounselFilePath(long counselId) {
        return String.format(COUNSEL_IMAGE_FILE_PATH, counselId);
    }

    /**
     * 가상 성형 이미지 파일 경로 설정
     *
     * @param virtualId 가상 성형 아이디
     * @return String 가상 성형 이미지 파일 경로
     */
    private String createVirtualSurgeryFilePath(long virtualId) {
        return String.format(VIRTUAL_FILE_PATH, virtualId);
    }

    /**
     * 이미지 S3 업로드
     *
     * @param fileName 파일 이름
     * @param file     파일 객체
     */
    private void uploadS3Bucket(String fileName, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, getObjectMetadata(file))
                .withCannedAcl(com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            log.error("!!!!!!!!!!!!!!!!!!!!!! uploadS3Bucket Error: {}", e.getMessage());
        }
    }

    /**
     * S3 Upload 용 파일 객체 생성
     *
     * @param multipartFile 파일 객체
     * @return ObjectMetadata
     */
    private ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
    }
}
