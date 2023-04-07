package io.github.lerojune.oss.files;

import lombok.Data;

@Data
public class FileUpload {
    private String name; //文件名
    private String ext; // 文件后缀名称
    private String md5;//文件指纹
    private Long size; //文件大小

}
