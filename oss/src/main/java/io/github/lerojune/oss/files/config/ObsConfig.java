package io.github.lerojune.oss.files.config;

import lombok.Data;

@Data
public class ObsConfig {
    public String endPoint = "https://obs.cn-north-4.myhuaweicloud.com";
    public String ak = "";
    public String sk = "";
    public String bucket = "";
    public  String domain = "";
}
