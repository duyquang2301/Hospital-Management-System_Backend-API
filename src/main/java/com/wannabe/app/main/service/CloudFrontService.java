package com.wannabe.app.main.service;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.wannabe.app.main.utility.StringUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CloudFrontService {

    private final String PRIVATE_KEY_FILENAME = "cloudfront_private_key.pem";
    @Value("${cloud.aws.cloud-front.distribution-domain}")
    private String DISTRIBUTION_DOMAIN;
    @Value("${cloud.aws.cloud-front.key-pair-id}")
    private String KEY_PAIR_ID;
    @Value("${cloud.aws.cloud-front.private-key}")
    private String PRIVATE_KEY_STRING;
    @Value("${cloud.aws.cloud-front.expired-milli-second}")
    private Long EXPIRED_SIGNED_URL_MILLISECOND;
    private PrivateKey privateKey;

    /**
     * S3 PreSigned Url 생성
     *
     * @param filePath 파일 경로
     * @return String S3 PreSigned Url
     */
    public String generateSignedUrl(String filePath) {
        if (!StringUtil.hasText(filePath)) {
            return "";
        }
        PrivateKey privateKey = getPrivateKey();
        return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
            buildDomainPath(filePath),
            KEY_PAIR_ID,
            privateKey,
            getExpirationDate());
    }

    /**
     * S3 PreSigned Url 생성
     *
     * @param filePaths 파일 경로
     * @return S3 PreSigned Url
     */
    public List<String> generateSignedUrl(List<String> filePaths) {
        PrivateKey privateKey = getPrivateKey();

        if (filePaths == null || filePaths.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();

        for (String filePath : filePaths) {
            result.add(CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                buildDomainPath(filePath),
                KEY_PAIR_ID,
                privateKey,
                getExpirationDate()));
        }

        return result;
    }

    /**
     * cloudFront 도메인 주소 생성
     *
     * @param filePath 파일 경로
     * @return cloudFront 도메인 주소
     */
    private String buildDomainPath(String filePath) {
        filePath = filePath.startsWith("/") ? filePath : "/" + filePath;
        return DISTRIBUTION_DOMAIN + filePath;
    }

    /**
     * 만료 기한 설정
     *
     * @return 만료 날짜
     */
    private Date getExpirationDate() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += EXPIRED_SIGNED_URL_MILLISECOND;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    /**
     * 비밀키 조회
     *
     * @return 비밀 키
     */
    private PrivateKey getPrivateKey() {
        if (privateKey == null) {
            try {
                File file = convertFile(PRIVATE_KEY_STRING);
                privateKey = SignerUtils.loadPrivateKey(file);
                file.delete();
            } catch (InvalidKeySpecException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return privateKey;
    }

    /**
     * 파일 변화
     *
     * @param data 비밀 키
     * @return 파일 객체
     */
    private File convertFile(String data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(PRIVATE_KEY_FILENAME));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new File(PRIVATE_KEY_FILENAME);
    }

}
