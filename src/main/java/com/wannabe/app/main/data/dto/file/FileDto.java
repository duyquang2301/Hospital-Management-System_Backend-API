package com.wannabe.app.main.data.dto.file;

import static com.wannabe.app.main.utility.constant.Role.USER;

import com.wannabe.app.main.data.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {

    private Long groupId;
    private String originalFilename;
    private String filename;
    private String path;
    private Long fileOrder;
    private String extension;
    private Double size;
    private String userType;
    private Long userId;
    private String updateUserType;
    private Long updateUserId;

    public static FileDto of(Long groupId, long userId, MultipartFile file, String filename, String path, Long fileOrder) {
        return new FileDto(
            groupId,
            file.getOriginalFilename(),
            filename,
            path,
            fileOrder,
            StringUtils.getFilenameExtension(file.getOriginalFilename()),
            (double) file.getSize(),
            USER,
            userId,
            USER,
            userId
        );
    }

    public static FileDto of(User user, MultipartFile originalFile, String fileName, String path) {
        return new FileDto(
            user.getImageGroupId(),
            originalFile.getOriginalFilename(),
            fileName,
            path,
            1L,
            StringUtils.getFilenameExtension(originalFile.getOriginalFilename()),
            (double) originalFile.getSize(),
            USER,
            user.getId(),
            USER,
            user.getId()
        );
    }

    public void updateGroupId(long groupId) {
        this.groupId = groupId;
    }
}
