package com.wlinsk.ansure.controller.base;

import com.wlinsk.ansure.basic.utils.oss.OSSUploadUtils;
import com.wlinsk.ansure.basic.utils.oss.UploadRespDTO;
import com.wlinsk.ansure.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: wlinsk
 * @Date: 2024/5/26
 */
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class FileController {
    private final OSSUploadUtils ossUploadUtils;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    public Result<UploadRespDTO> uploadFile(@RequestParam("file") MultipartFile file){
        UploadRespDTO result = ossUploadUtils.uploadFile(file);
        return Result.ok(result);
    }
}
