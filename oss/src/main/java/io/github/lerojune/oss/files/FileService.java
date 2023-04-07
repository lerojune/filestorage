package io.github.lerojune.oss.files;

import io.github.lerojune.oss.files.exception.FileUploadException;
import io.github.lerojune.oss.files.exception.NotFindException;
import io.github.lerojune.oss.files.exception.NotSupportException;
import io.github.lerojune.oss.files.impl.LocalService;
import io.github.lerojune.oss.files.impl.ObsService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * */

public class FileService {

    SimpleDateFormat directoryFormat = new SimpleDateFormat("yyyyMMdd");
    String defaultDirectory = "uploads";
    String tmp = "tmp";

    /**
     * 创建上传数据
     * @param isTemp 是否临时文件
     * @param name 文件名称
     * */
    public String createUpload(Boolean isTemp, String name){
        return String.format("%s/%s/%s", isTemp?tmp:defaultDirectory, directoryFormat.format(new Date()), name);
    }


    /**
     * 模板
     * */
    public String uploadFile(FileUploadDto fileUploadDto) throws NotSupportException, NotFindException, FileUploadException {

        FileUploadService fileUploadService = null;
        if (fileUploadDto.getFileUploadType().equals(FileUploadType.FILE_LOCAL)){
            fileUploadService = (new LocalService());
        }else if (fileUploadDto.getFileUploadType().equals(FileUploadType.FILE_OBS)){
            fileUploadService =  (new ObsService());
        }else{
            throw new NotSupportException("没有查询到匹配的文件服务");
        }

        //第一步
        String path = createUpload(fileUploadDto.getIsTemp(), fileUploadDto.getName());
        fileUploadDto.setName(path);;
        //第二步
        fileUploadService.upload(fileUploadDto);
        //第三步
        fileUploadService.clean();
        return path;
    }
}
