package io.github.lerojune.oss.files;

import lombok.Data;

import java.io.File;
import java.io.FileInputStream;

@Data
public class FileUploadDto {
    private String name; //文件名
    private String ext; // 文件后缀名称
    private String md5;//文件指纹
    private Long size; //文件大小
    private FileInputStream fileInputStream;
    private File file;
    private FileUploadType fileUploadType = FileUploadType.FILE_LOCAL;
    private Boolean isTemp = true; //是否零时文件
}
