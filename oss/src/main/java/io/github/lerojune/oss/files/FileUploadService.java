package io.github.lerojune.oss.files;

import io.github.lerojune.oss.files.exception.FileUploadException;
import io.github.lerojune.oss.files.exception.NotFindException;

import java.io.FileInputStream;

/**
 * file service for upload file and download file
 *
 * programs:
 *   1.create file
 *   2.make the file path throw the same rule
 * */

public interface FileUploadService {

    /**
     * 执行上传任务
     * */
    void upload(FileUploadDto fileUploadDto) throws NotFindException, FileUploadException;

    /**
     * 清理缓存数据
     * */
    void clean();
}
