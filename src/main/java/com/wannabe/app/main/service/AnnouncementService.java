package com.wannabe.app.main.service;

import com.wannabe.app.main.data.entity.Announcement;
import com.wannabe.app.main.data.entity.Files;
import com.wannabe.app.main.exception.sql.DatabaseException;
import com.wannabe.app.main.mapper.AnnouncementMapper;
import com.wannabe.app.main.mapper.FilesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final ImageService imageService;
    private final CloudFrontService cloudFrontService;
    private final FilesMapper filesMapper;
    private final AnnouncementMapper announcementMapper;

    Logger logger = LogManager.getLogger(this.getClass());

    public List<Announcement> getAnnouncementsActive() {
        List<Announcement> announcements = announcementMapper.getAnnouncementsActive();
        List<Announcement> result = announcements.stream()
            .peek(item -> {
                List<Files> files = setPathToFullPath(item.getImageGroupId());
                if (files.size() == 1) {
                    Files files1 = files.get(0);
                    String path = files1.getPath();
                    item.setImage(path);
                }
            })
            .collect(Collectors.toList());

        return result;
    }


    public List<Files> setPathToFullPath(Long imageGroupId) {
        List<Files> files;

        try {

            files = filesMapper.findFileListByGroupId(imageGroupId);

        } catch (Exception e) {

            throw new DatabaseException(logger, e.getMessage());

        }

        files = files.stream()
            .peek(item -> item.setPath(cloudFrontService.generateSignedUrl("/" + item.getPath())))
            .collect(Collectors.toList());

        return files;
    }


}
