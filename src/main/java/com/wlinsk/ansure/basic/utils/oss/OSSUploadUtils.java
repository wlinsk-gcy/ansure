package com.wlinsk.ansure.basic.utils.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @Author: wlinsk
 * @Date: 2024/5/26
 */
@Slf4j
@Component
public class OSSUploadUtils {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    public UploadRespDTO uploadFile(MultipartFile file) {
        if (Objects.isNull(file) || Objects.isNull(file.getOriginalFilename())) {
            throw new BasicException(SysCode.SYSTEM_FILE_UPLOAD_ERROR);
        }
        String random = RandomStringUtils.random(32, false, true);
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = String.format("%s/%s%s", date, random, suffix);
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucketName, fileName, file.getInputStream());
            UploadRespDTO uploadRespDTO = new UploadRespDTO();
            uploadRespDTO.setFileName(fileName);
            uploadRespDTO.setImageUrl(String.format("https://%s.%s/%s", bucketName, endpoint, fileName));
            return uploadRespDTO;
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            throw new BasicException(SysCode.SYSTEM_FILE_UPLOAD_ERROR);
        } finally {
            ossClient.shutdown();
        }
    }
}
