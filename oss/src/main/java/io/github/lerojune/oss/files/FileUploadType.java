package io.github.lerojune.oss.files;

public enum FileUploadType {
    FILE_LOCAL(0), //本地存储
    FILE_OBS(1), //华为 OBS
    FILE_OSS(2), // 阿里云 OSS
    FILE_QINIU(3); //七牛云

    public int code;
    FileUploadType(int code){
        this.code = code;
    }
}
