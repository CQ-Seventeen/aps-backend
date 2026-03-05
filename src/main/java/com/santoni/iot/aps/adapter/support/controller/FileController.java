package com.santoni.iot.aps.adapter.support.controller;

import com.santoni.iot.aps.common.minio.MinioExecutor;
import com.santoni.iot.utils.entity.ReturnData;
import com.santoni.iot.utils.record.annotation.SantoniHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequestMapping("/file")
@RestController
public class FileController {

    @Autowired
    private MinioExecutor minioExecutor;

    @PostMapping("/upload")
    @ResponseBody
    @SantoniHeader
    public ReturnData<String> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            var url = minioExecutor.uploadFile(file, false);
            return new ReturnData<>(url);
        } catch (Exception e) {
            log.error("File upload error, fileName;{}", file.getName(), e);
            return new ReturnData<>(500, "");
        }
    }

}
